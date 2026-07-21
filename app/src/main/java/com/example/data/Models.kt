package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SourceType {
    OFFICIAL, EXPERT, JOURNALIST, NEWS_SITE, BLOG, YOUTUBE
}

enum class PlatformType {
    X, THREADS, YOUTUBE, BLOG, NEWS, OFFICIAL
}

enum class ImportanceLevel {
    CRITICAL, HIGH, MEDIUM, LOW
}

enum class VerificationStatus {
    VERIFIED, MULTIPLE_SOURCES, DEVELOPING, CLAIM, OFFICIAL_STATEMENT
}

data class Topic(
    val id: String,
    val name: String,
    val iconName: String
)

@Entity(tableName = "followed_sources")
data class FollowedSourceEntity(
    @PrimaryKey val id: String
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

data class SourcePost(
    val id: String,
    val sourceId: String,
    val platform: PlatformType,
    val timestamp: String,
    val snippet: String,
    val originalUrl: String
)

@Entity(tableName = "saved_stories")
data class SavedStoryEntity(
    @PrimaryKey val id: String,
    val savedAt: Long = System.currentTimeMillis()
)

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
    val sourceName: String = "Google Haberler",
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

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val type: String, // e.g. CRITICAL, RECENT_UPDATE, BRIEF
    val timestamp: String,
    val isRead: Boolean = false,
    val storyId: String? = null
)

@Entity(tableName = "followed_packages")
data class FollowedPackageEntity(
    @PrimaryKey val id: String
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

data class UserPreferences(
    val name: String = "Cem",
    val selectedInterests: Set<String> = setOf("Yapay Zekâ", "Teknoloji"),
    val volumeLevel: String = "Dengeli", // Sakin, Dengeli, Yoğun
    val notificationFreq: Set<String> = setOf("Sabah özeti", "Akşam özeti"),
    val isDarkTheme: Boolean = true,
    val language: String = "Türkçe",
    val isOnboarded: Boolean = false
)
