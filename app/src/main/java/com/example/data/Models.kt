package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

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
    val type: String, // "AI_SUMMARY", "BREAKING", "TOPIC_UPDATE"
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionUrl: String? = null // Yönlendirilecek storyId
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val originalTitle: String,
    val eventHash: String, // clustering id
    val category: String, // "Yapay Zekâ", "Türkiye" vs
    val importance: String, // CRITICAL, HIGH, MEDIUM, LOW
    val status: String, // VERIFIED, MULTIPLE_SOURCES
    val url: String,
    val sourceName: String,
    val timestamp: Long,
    val isArchived: Boolean = false
)

@Entity(tableName = "story_timeline")
data class StoryTimelineEntity(
    @PrimaryKey val id: String,
    val storyId: String,
    val time: String, // Örn: "10:30" veya timestamp
    val description: String,
    val sourceName: String
)

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType: Type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }
}

// UI Data Classes (kept for UI compatibility)
data class Story(
    val id: String,
    val category: String,
    val importance: ImportanceLevel,
    val status: VerificationStatus,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val sourcesCount: Int,
    val lastTimestamp: String,
    val isSaved: Boolean = false,
    val isBookmarked: Boolean = false, // Alias
    val timeline: List<StoryTimelineItem> = emptyList(),
    val isExpanded: Boolean = false,
    val relatedTopics: List<String> = emptyList(),
    val enrichedSources: List<StorySourceRelation> = emptyList()
)

data class StoryTimelineItem(
    val id: String,
    val storyId: String,
    val time: String,
    val title: String,
    val content: String,
    val isKeyMoment: Boolean = false
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
    val storyCount: Int,
    val iconName: String, // e.g. "rocket_launch"
    val isFollowing: Boolean = false,
    val isTrending: Boolean = false,
    val category: String
)

data class Topic(
    val id: String,
    val title: String,
    val iconName: String
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
