package com.example.data

import android.util.Log
import com.aistudio.gundemai.vkyzq.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey: String = try {
        BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
        ""
    }

    suspend fun checkClusteringMatch(newTitle: String, newDesc: String, recentStories: List<StoryEntity>): String? = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey.contains("placeholder")) return@withContext null
        
        try {
            val listText = recentStories.joinToString("\n") { "ID: ${it.id} | TITLE: ${it.title}" }
            val prompt = """
                Aşağıdaki yeni haber, listedeki eski haberlerden birinin doğrudan devamı, güncellemesi veya aynı olay örgüsüne aitse o haberin ID'sini döndür. 
                Eğer yepyeni, tamamen farklı bir olaysa "NONE" yaz. 
                SADECE "ID" VEYA "NONE" DÖNDÜR, BAŞKA METİN YAZMA.
                
                YENİ HABER:
                Başlık: $newTitle
                Açıklama: $newDesc
                
                SON 24 SAATİN HABERLERİ:
                $listText
            """.trimIndent()
            
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }
            
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
                
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val resultJson = JSONObject(body)
                    val text = resultJson.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")?.trim() ?: "NONE"
                        
                    if (text != "NONE" && recentStories.any { it.id == text }) {
                        return@withContext text
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Cluster match error", e)
        }
        return@withContext null
    }

    suspend fun generateTimelineSummary(oldSummary: String, newTitle: String, newDesc: String): String = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey.contains("placeholder")) return@withContext "$oldSummary\nSon Gelişme: $newTitle"
        
        try {
            val prompt = """
                Bir haber olayının eski özeti ile en son gelişmesini birleştirip, akıcı, tek paragraflık ve sürükleyici bir GündemAI özeti yaz.
                En fazla 3-4 cümle olsun. Dili profesyonel Türkçe olsun.
                
                ESKİ ÖZET: $oldSummary
                YENİ GELİŞME: $newTitle - $newDesc
            """.trimIndent()
            
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }
            
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
                
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val resultJson = JSONObject(body)
                    val text = resultJson.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")?.trim()
                        
                    if (!text.isNullOrEmpty()) {
                        return@withContext text
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Summary error", e)
        }
        return@withContext "$oldSummary\nSon Gelişme: $newTitle"
    }

    suspend fun askQuestion(prompt: String, contextStories: List<Story>): String = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey.contains("placeholder", ignoreCase = true)) {
            return@withContext "Üzgünüm, şu anda yanıt veremiyorum. Lütfen API anahtarınızı yapılandırın."
        }

        try {
            val contextText = contextStories.take(3).joinToString("\n\n") { 
                "HABER: ${it.title}\nÖZET: ${it.summary}" 
            }
            
            val fullPrompt = """
                Sen GündemAI adlı Türkçe konuşan bir haber asistanısın. 
                Sana bazı güncel haberleri ve kullanıcının sorusunu vereceğim.
                Soruyu bu haberlere dayanarak, net, kısa ve profesyonel bir Türkçe ile yanıtla.
                
                BAĞLAM:
                $contextText
                
                KULLANICI: $prompt
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", fullPrompt)
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val resultJson = JSONObject(body)
                    val text = resultJson.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")
                    
                    if (!text.isNullOrEmpty()) {
                        return@withContext text
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Error in AI interaction", e)
        }
        
        return@withContext "Üzgünüm, şu an bağlantı kuramıyorum. Lütfen tekrar deneyin."
    }

    suspend fun generateStoryEnrichment(title: String, category: String, sourceName: String): EnrichmentResult = withContext(Dispatchers.IO) {
        if (apiKey.isEmpty() || apiKey.contains("placeholder", ignoreCase = true)) {
            return@withContext getLocalFallbackEnrichment(title, category, sourceName)
        }

        try {
            val prompt = """
                Aşağıdaki haberi analiz et ve JSON formatında dön:
                Başlık: "$title"
                Kategori: "$category"
                
                JSON Formatı:
                {
                  "contentWhat": "Haberin konusu nedir? (1 cümle)",
                  "contentWhy": "Neden önemli? (2 cümle)",
                  "aiComment": "GündemAI Yorumu (2 cümle)",
                  "consensusPoints": ["Teyitli nokta 1", "Teyitli nokta 2"],
                  "unresolvedPoints": ["Belirsiz nokta 1"],
                  "importance": "CRITICAL, HIGH, MEDIUM, LOW",
                  "status": "VERIFIED, CLAIM, OFFICIAL_STATEMENT",
                  "timeline": [
                    {"time": "10:00", "description": "Önceki olay"}
                  ]
                }
                Lütfen SADECE JSON döndür, kod bloku kullanma.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val resultJson = JSONObject(body)
                    val rawText = resultJson.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text") ?: ""
                        
                    val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
                    
                    if (cleanJson.isNotEmpty()) {
                        val parsed = JSONObject(cleanJson)
                        
                        val consensusList = mutableListOf<String>()
                        val cArray = parsed.optJSONArray("consensusPoints")
                        if (cArray != null) {
                            for (i in 0 until cArray.length()) {
                                consensusList.add(cArray.getString(i))
                            }
                        }
                        
                        val unresolvedList = mutableListOf<String>()
                        val uArray = parsed.optJSONArray("unresolvedPoints")
                        if (uArray != null) {
                            for (i in 0 until uArray.length()) {
                                unresolvedList.add(uArray.getString(i))
                            }
                        }
                        
                        val timelineList = mutableListOf<TimelineEvent>()
                        val tArray = parsed.optJSONArray("timeline")
                        if (tArray != null) {
                            for (i in 0 until tArray.length()) {
                                val tObj = tArray.getJSONObject(i)
                                timelineList.add(
                                    TimelineEvent(
                                        time = tObj.optString("time", "12:00"),
                                        description = tObj.optString("description", "")
                                    )
                                )
                            }
                        }

                        return@withContext EnrichmentResult(
                            contentWhat = parsed.optString("contentWhat", title),
                            contentWhy = parsed.optString("contentWhy", ""),
                            aiComment = parsed.optString("aiComment", "GündemAI olarak bu gelişmeyi izlemeye devam ediyoruz."),
                            consensusPoints = if (consensusList.isEmpty()) listOf("Detaylar teyit ediliyor.") else consensusList,
                            unresolvedPoints = unresolvedList,
                            importance = try { ImportanceLevel.valueOf(parsed.optString("importance", "MEDIUM")) } catch (e: Exception) { ImportanceLevel.MEDIUM },
                            status = try { VerificationStatus.valueOf(parsed.optString("status", "MULTIPLE_SOURCES")) } catch (e: Exception) { VerificationStatus.MULTIPLE_SOURCES },
                            timeline = timelineList
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Enrichment error", e)
        }
        
        return@withContext getLocalFallbackEnrichment(title, category, sourceName)
    }

    private fun getLocalFallbackEnrichment(title: String, category: String, sourceName: String): EnrichmentResult {
        return EnrichmentResult(
            contentWhat = title,
            contentWhy = "Bu gelişme, ${category} alanındaki mevcut trendleri doğrudan etkileyebilecek potansiyele sahip.",
            aiComment = "Veri akışı şu an için yerel kaynaklardan sağlanıyor, detaylı teyit mekanizmaları devrede.",
            consensusPoints = listOf("$sourceName bu olayı doğruladı.", "Olayın $category dinamiklerine etkisi bekleniyor."),
            unresolvedPoints = listOf("Diğer bağımsız kaynaklardan teyit bekleniyor."),
            importance = ImportanceLevel.MEDIUM,
            status = VerificationStatus.DEVELOPING,
            timeline = listOf(
                TimelineEvent("Şimdi", "$sourceName haberi servis etti.")
            )
        )
    }
}

data class EnrichmentResult(
    val contentWhat: String,
    val contentWhy: String,
    val aiComment: String,
    val consensusPoints: List<String>,
    val unresolvedPoints: List<String>,
    val importance: ImportanceLevel,
    val status: VerificationStatus,
    val timeline: List<TimelineEvent>
)

data class TimelineEvent(
    val time: String,
    val description: String
)
