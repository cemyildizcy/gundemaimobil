package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class SourceType { OFFICIAL, EXPERT, JOURNALIST, NEWS_SITE, BLOG, YOUTUBE }
enum class PlatformType { X, THREADS, YOUTUBE, BLOG, NEWS, OFFICIAL, TELEGRAM }
enum class ImportanceLevel { CRITICAL, HIGH, MEDIUM, LOW }
enum class VerificationStatus { VERIFIED, MULTIPLE_SOURCES, DEVELOPING, CLAIM, OFFICIAL_STATEMENT }

@Entity(tableName = "followed_sources")
data class FollowedSourceEntity(@PrimaryKey val id: String)

@Entity(tableName = "saved_stories")
data class SavedStoryEntity(@PrimaryKey val id: String, val savedAt: Long = System.currentTimeMillis())

@Entity(tableName = "followed_packages")
data class FollowedPackageEntity(@PrimaryKey val id: String)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val type: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val storyId: String? = null
)

// CORE CLUSTERING ENTITIES
@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val category: String,
    val importance: String, // Stored as String for Room
    val status: String,
    val title: String,
    val summary: String,
    val contentWhat: String,
    val contentWhy: String,
    val aiComment: String,
    val consensusPoints: String, // JSON serialized
    val unresolvedPoints: String, // JSON serialized
    val coverUrl: String,
    val firstTimestamp: String,
    val lastTimestamp: Long, // Use Long (Epoch) for proper sorting
    val sourcesCount: Int,
    val sourceName: String,
    val originalUrl: String
)

@Entity(tableName = "story_timelines")
data class StoryTimelineEntity(
    @PrimaryKey val id: String,
    val storyId: String,
    val timestampStr: String,
    val eventDescription: String,
    val sourceId: String,
    val epochTime: Long
)

// Mappers for UI
fun StoryEntity.toUIStory(isSaved: Boolean, timelines: List<StoryTimelineEntity>): Story {
    val gson = Gson()
    val consensusList = try { gson.fromJson<List<String>>(consensusPoints, object : TypeToken<List<String>>() {}.type) } catch(e: Exception) { emptyList() }
    val unresolvedList = try { gson.fromJson<List<String>>(unresolvedPoints, object : TypeToken<List<String>>() {}.type) } catch(e: Exception) { emptyList() }
    
    // Formatting the epoch to HH:mm for UI
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale("tr", "TR"))
    val lastTimeStr = sdf.format(java.util.Date(lastTimestamp))

    return Story(
        id = id,
        category = category,
        importance = ImportanceLevel.valueOf(importance),
        status = VerificationStatus.valueOf(status),
        title = title,
        summary = summary,
        contentWhat = contentWhat,
        contentWhy = contentWhy,
        aiComment = aiComment,
        consensusPoints = consensusList ?: emptyList(),
        unresolvedPoints = unresolvedList ?: emptyList(),
        coverUrl = coverUrl,
        firstTimestamp = firstTimestamp,
        lastTimestamp = lastTimeStr,
        sourcesCount = sourcesCount + timelines.size,
        isSaved = isSaved,
        sourceName = sourceName,
        originalUrl = originalUrl,
        enrichedTimeline = timelines.map { StoryTimelineItem(it.id, it.storyId, it.timestampStr, it.eventDescription, it.sourceId) },
        enrichedSources = emptyList() // Will be unified in UI if needed
    )
}

// UI Data Classes (kept for UI compatibility)
data class Story(
    val id: String,
    val category: String,
    val importance: ImportanceLevel,
    val status: VerificationStatus,
    val title: String,
    val summary: String,
    val contentWhat: String,
    val contentWhy: String,
    val aiComment: String,
    val consensusPoints: List<String>,
    val unresolvedPoints: List<String>,
    val coverUrl: String,
    val firstTimestamp: String,
    val lastTimestamp: String,
    val sourcesCount: Int,
    val isSaved: Boolean = false,
    val sourceName: String = "GündemAI Core",
    val originalUrl: String = "",
    val enrichedTimeline: List<StoryTimelineItem> = emptyList(),
    val enrichedSources: List<StorySourceRelation> = emptyList()
)

data class StoryTimelineItem(
    val id: String,
    val storyId: String,
    val timestamp: String,
    val eventDescription: String,
    val sourceId: String
)

data class StorySourceRelation(
    val storyId: String,
    val sourceId: String,
    val postSnippet: String,
    val timestamp: String,
    val originalUrl: String
)

data class GundemPackage(
    val id: String,
    val title: String,
    val description: String,
    val sourcesCount: Int,
    val dailyVolume: Int,
    val followersCount: Int,
    val isFollowing: Boolean = false,
    val category: String
)

data class Source(
    val id: String,
    val name: String,
    val username: String,
    val type: SourceType,
    val platform: PlatformType,
    val avatarUrl: String,
    val fieldOfExpertise: String,
    val reliabilityLabel: String,
    val isFollowing: Boolean = false
)

data class UserPreferences(
    val name: String = "Cem",
    val selectedInterests: Set<String> = setOf("Yapay Zekâ", "Teknoloji"),
    val volumeLevel: String = "Dengeli", 
    val notificationFreq: Set<String> = setOf("Sabah özeti", "Akşam özeti"),
    val isDarkTheme: Boolean = true,
    val language: String = "Türkçe",
    val isOnboarded: Boolean = false
)
