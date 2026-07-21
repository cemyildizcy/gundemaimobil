package com.example.data

import android.util.Log
import com.aistudio.gundemai.vkyzq.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class NewsRepository(
    private val db: AppDatabase,
    private val geminiService: GeminiService
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // .env API keys via BuildConfig
    private val gnewsApiKey = try { BuildConfig.GNEWS_API_KEY } catch (e: Exception) { "" }
    private val telegramBotToken = try { BuildConfig.TELEGRAM_BOT_TOKEN } catch (e: Exception) { "" }

    suspend fun syncNews(categories: Set<String>) = withContext(Dispatchers.IO) {
        val fetchedRaw = mutableListOf<RawNewsItem>()
        
        // 1. Fetch from GNews
        if (gnewsApiKey.isNotEmpty() && !gnewsApiKey.contains("placeholder")) {
            val q = categories.joinToString(" OR ")
            val url = "https://gnews.io/api/v4/search?q=${java.net.URLEncoder.encode(q, "UTF-8")}&lang=tr&country=tr&max=10&apikey=$gnewsApiKey"
            
            try {
                val req = Request.Builder().url(url).build()
                client.newCall(req).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (body != null) {
                            val json = JSONObject(body)
                            val articles = json.optJSONArray("articles")
                            if (articles != null) {
                                for (i in 0 until articles.length()) {
                                    val art = articles.getJSONObject(i)
                                    fetchedRaw.add(
                                        RawNewsItem(
                                            title = art.optString("title", ""),
                                            description = art.optString("description", ""),
                                            sourceName = art.optJSONObject("source")?.optString("name", "GNews") ?: "GNews",
                                            url = art.optString("url", ""),
                                            category = categories.firstOrNull() ?: "Gündem"
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("NewsRepo", "GNews fetch error", e)
            }
        }
        
        // 2. Process & Cluster
        if (fetchedRaw.isEmpty()) return@withContext
        
        val storyDao = db.storyDao()
        val timelineDao = db.timelineDao()
        
        // Get last 24h stories for clustering
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        val recentStories = storyDao.getRecentStoriesForClustering(oneDayAgo)
        
        for (raw in fetchedRaw) {
            val matchStoryId = findClusterMatch(raw, recentStories)
            val now = System.currentTimeMillis()
            
            if (matchStoryId != null) {
                // UPDATE EXISTING STORY (Timeline Append)
                val existing = storyDao.getStoryById(matchStoryId)
                if (existing != null) {
                    // Generate updated summary using Gemini
                    val newSummary = geminiService.generateTimelineSummary(existing.summary, raw.title, raw.description)
                    
                    val updated = existing.copy(
                        timestamp = now,
                        summary = newSummary
                    )
                    storyDao.updateStory(updated)
                    
                    val timeline = StoryTimelineEntity(
                        id = UUID.randomUUID().toString().take(10),
                        storyId = existing.id,
                        time = java.text.SimpleDateFormat("HH:mm").format(java.util.Date(now)),
                        description = raw.title,
                        sourceName = raw.sourceName
                    )
                    timelineDao.insertTimeline(timeline)
                }
            } else {
                // INSERT NEW STORY
                val newId = "st_${UUID.randomUUID().toString().take(8)}"
                val newStory = StoryEntity(
                    id = newId,
                    category = raw.category,
                    importance = "HIGH", // Simplified
                    status = "VERIFIED",
                    title = raw.title,
                    summary = raw.description.take(200),
                    originalTitle = raw.title,
                    eventHash = "hash_${newId}",
                    url = raw.url,
                    sourceName = raw.sourceName,
                    timestamp = now
                )
                storyDao.insertStory(newStory)
            }
        }
    }
    
    private suspend fun findClusterMatch(newNews: RawNewsItem, recentStories: List<StoryEntity>): String? {
        if (recentStories.isEmpty()) return null
        // Ask Gemini to cluster
        return geminiService.checkClusteringMatch(newNews.title, newNews.description, recentStories)
    }
}

data class RawNewsItem(
    val title: String,
    val description: String,
    val sourceName: String,
    val url: String,
    val category: String
)
