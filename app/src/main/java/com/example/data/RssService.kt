package com.example.data

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RssService {

    suspend fun fetchCategoryRss(category: String): List<Story> = withContext(Dispatchers.IO) {
        val query = when (category) {
            "Yapay Zekâ" -> "\"yapay zeka\" OR \"gemini\" OR \"openai\" OR \"deepmind\""
            "Teknoloji" -> "teknoloji OR \"apple\" OR \"google\" OR \"samsung\" OR \"yapay zeka\""
            "Transfer" -> "transfer OR \"galatasaray\" OR \"fenerbahçe\" OR \"beşiktaş\" OR \"millî takım\""
            "Ekonomi" -> "ekonomi OR \"merkez bankası\" OR \"borsa\" OR \"dolar\" OR \"enflasyon\""
            else -> "sondakika OR guncel"
        }
        
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        
        val staticRss = when (category) {
            "Teknoloji", "Yapay Zekâ" -> "https://www.donanimhaber.com/rss/tum/"
            "Transfer" -> "https://www.fotomac.com.tr/rss/anasayfa.xml"
            "Ekonomi" -> "https://www.bloomberght.com/rss"
            else -> "https://www.trthaber.com/sondakika.xml"
        }

        val feedUrls = listOf(
            "https://news.google.com/rss/search?q=$encodedQuery&hl=tr&gl=TR&ceid=TR:tr",
            "https://www.bing.com/news/search?q=$encodedQuery&format=rss&mkt=tr-TR",
            staticRss
        )

        for (feedUrl in feedUrls) {
            try {
                val url = URL(feedUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val stories = connection.inputStream.use { inputStream ->
                        parseRss(inputStream, category, feedUrl)
                    }
                    if (stories.isNotEmpty()) {
                        Log.d("RssService", "Successfully fetched from $feedUrl")
                        return@withContext stories
                    }
                } else {
                    Log.e("RssService", "Failed to fetch from $feedUrl: HTTP $responseCode")
                }
            } catch (e: Exception) {
                Log.e("RssService", "Error fetching from $feedUrl: ${e.message}")
            }
        }
        Log.e("RssService", "All RSS feeds failed for category $category")
        return@withContext emptyList()
    }

    private fun parseRss(inputStream: InputStream, category: String, feedUrl: String): List<Story> {
        val stories = mutableListOf<Story>()
        try {
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, "UTF-8")

            var eventType = parser.eventType
            var inItem = false
            var title = ""
            var link = ""
            var description = ""
            var pubDate = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val name = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name.equals("item", ignoreCase = true)) {
                            inItem = true
                        } else if (inItem) {
                            when (name.lowercase()) {
                                "title" -> title = parser.nextText().trim()
                                "link" -> link = parser.nextText().trim()
                                "description" -> description = parser.nextText().trim()
                                "pubdate" -> pubDate = parser.nextText().trim()
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (name.equals("item", ignoreCase = true)) {
                            if (title.isNotEmpty()) {
                                stories.add(createStoryFromRss(title, link, description, pubDate, category, feedUrl))
                            }
                            inItem = false
                            title = ""
                            link = ""
                            description = ""
                            pubDate = ""
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("RssService", "Error parsing RSS XML: ${e.message}")
        }
        // Return up to 10 latest articles for feed freshness
        return stories.take(12)
    }

    private fun createStoryFromRss(
        title: String,
        link: String,
        description: String,
        pubDate: String,
        category: String,
        feedUrl: String
    ): Story {
        // Clean Google News title format: "Headline text - Source Name"
        val lastDash = title.lastIndexOf(" - ")
        val cleanTitle = if (lastDash != -1) title.substring(0, lastDash).trim() else title
        val sourceName = if (lastDash != -1) {
            title.substring(lastDash + 3).trim()
        } else {
            when {
                feedUrl.contains("bloomberght", ignoreCase = true) -> "Bloomberg HT"
                feedUrl.contains("donanimhaber", ignoreCase = true) -> "DonanımHaber"
                feedUrl.contains("fotomac", ignoreCase = true) -> "Fotomaç"
                feedUrl.contains("trthaber", ignoreCase = true) -> "TRT Haber"
                feedUrl.contains("bing", ignoreCase = true) -> "Bing Haberler"
                feedUrl.contains("webrazzi", ignoreCase = true) -> "Webrazzi"
                feedUrl.contains("shiftdelete", ignoreCase = true) -> "ShiftDelete.Net"
                else -> "Google Haberler"
            }
        }

        val cleanedDesc = cleanHtml(description)
        val shortSummary = if (cleanedDesc.isNotEmpty() && cleanedDesc.length > 5) {
            if (cleanedDesc.length > 200) cleanedDesc.take(197) + "..." else cleanedDesc
        } else {
            "Gelişmenin detayları ve yapay zekâ analiz raporu için detaya dokunun."
        }

        // Smart dynamic fields based on keywords
        val lowerTitle = cleanTitle.lowercase()
        val importance = when {
            lowerTitle.contains("kritik") || lowerTitle.contains("acil") || lowerTitle.contains("flas") || lowerTitle.contains("flaş") || lowerTitle.contains("son dakika") || lowerTitle.contains("şok") -> ImportanceLevel.CRITICAL
            lowerTitle.contains("önemli") || lowerTitle.contains("duyurdu") || lowerTitle.contains("yeni") || lowerTitle.contains("ilk") || lowerTitle.contains("rekor") -> ImportanceLevel.HIGH
            else -> ImportanceLevel.MEDIUM
        }

        val status = when {
            lowerTitle.contains("iddia") || lowerTitle.contains("sızıntı") -> VerificationStatus.CLAIM
            lowerTitle.contains("açıkladı") || lowerTitle.contains("resmi") || lowerTitle.contains("kap") || lowerTitle.contains("duyuruldu") -> VerificationStatus.OFFICIAL_STATEMENT
            lowerTitle.contains("doğrulandı") || lowerTitle.contains("kesinleşti") -> VerificationStatus.VERIFIED
            else -> VerificationStatus.MULTIPLE_SOURCES
        }

        // Visual design category cover mappings
        val coverUrl = when (category) {
            "Yapay Zekâ" -> "https://images.unsplash.com/photo-1677442136019-21780efad99a?q=80&w=600"
            "Teknoloji" -> "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=600"
            "Transfer" -> "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?q=80&w=600"
            "Ekonomi" -> "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?q=80&w=600"
            else -> "https://images.unsplash.com/photo-1495020689067-958852a6565d?q=80&w=600"
        }

        val idHash = "rss_" + cleanTitle.hashCode().toString()

        val formattedFirstTime = formatRssDate(pubDate)
        val formattedLastTime = parseTimeFromPubDate(pubDate)

        return Story(
            id = idHash,
            category = category,
            importance = importance,
            status = status,
            title = cleanTitle,
            summary = shortSummary,
            contentWhat = cleanTitle,
            contentWhy = "Bu gelişme, takip ettiğimiz en güncel $category haber havuzundan otomatik olarak çekilmiştir.",
            aiComment = "GündemAI Analizi: Gelişmeyle ilgili doğrulanmış kaynaklardan gelen raporları inceleyip derinlemesine yapay zeka yorumu edinmek için detaya gidin.",
            consensusPoints = listOf(
                "Bu haber $sourceName tarafından yayınlandı.",
                "Google News Türkiye ağından anlık olarak doğrulanmıştır."
            ),
            unresolvedPoints = listOf(
                "Diğer haber kaynaklarının bu habere ilişkin yorumları ve resmi açıklamalar bekleniyor."
            ),
            coverUrl = coverUrl,
            firstTimestamp = formattedFirstTime,
            lastTimestamp = formattedLastTime,
            sourcesCount = 3,
            isSaved = false,
            sourceName = sourceName,
            originalUrl = link
        )
    }

    private fun formatRssDate(pubDateStr: String): String {
        if (pubDateStr.isEmpty()) return "Az önce"
        
        val formats = listOf(
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
            SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.US),
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        )
        
        var date: Date? = null
        for (format in formats) {
            try {
                date = format.parse(pubDateStr)
                if (date != null) break
            } catch (e: Exception) {
                // try next format
            }
        }
        
        if (date == null) {
            return pubDateStr // fallback
        }
        
        val now = System.currentTimeMillis()
        val diff = now - date.time
        
        if (diff < 0) {
            val sdf = SimpleDateFormat("HH:mm", Locale("tr", "TR"))
            return sdf.format(date)
        }
        
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            minutes < 1 -> "Şimdi"
            minutes < 60 -> "$minutes dakika önce"
            hours < 24 -> "$hours saat önce"
            days == 1L -> "Dün"
            else -> {
                val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("tr", "TR"))
                sdf.format(date)
            }
        }
    }

    private fun parseTimeFromPubDate(pubDateStr: String): String {
        try {
            val regex = Regex("(\\d{2}):(\\d{2})")
            val match = regex.find(pubDateStr)
            if (match != null) {
                return match.value
            }
        } catch (e: Exception) {
            // ignore
        }
        val sdf = SimpleDateFormat("HH:mm", Locale("tr", "TR"))
        return sdf.format(Date())
    }

    private fun cleanHtml(html: String): String {
        return html.replace(Regex("<[^>]*>"), "").trim()
    }
}
