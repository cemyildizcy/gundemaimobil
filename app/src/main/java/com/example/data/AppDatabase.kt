package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM saved_stories")
    fun getSavedStories(): Flow<List<SavedStoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStory(story: SavedStoryEntity)

    @Query("DELETE FROM saved_stories WHERE id = :storyId")
    suspend fun unsaveStory(storyId: String)
    
    @Query("DELETE FROM saved_stories")
    suspend fun clearAll()

    // --- Clustering Queries ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Update
    suspend fun updateStory(story: StoryEntity)

    @Query("SELECT * FROM stories ORDER BY timestamp DESC")
    fun getAllLiveStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE timestamp > :timeThreshold ORDER BY timestamp DESC")
    suspend fun getRecentStoriesForClustering(timeThreshold: Long): List<StoryEntity>
    
    @Query("SELECT * FROM stories WHERE id = :id LIMIT 1")
    suspend fun getStoryById(id: String): StoryEntity?

    @Query("DELETE FROM stories")
    suspend fun clearLiveStories()
}

@Dao
interface TimelineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeline(item: StoryTimelineEntity)

    @Query("SELECT * FROM story_timeline WHERE storyId = :storyId ORDER BY time ASC")
    fun getTimelinesForStory(storyId: String): Flow<List<StoryTimelineEntity>>
    
    @Query("SELECT * FROM story_timeline WHERE storyId = :storyId ORDER BY time ASC")
    suspend fun getTimelinesForStorySync(storyId: String): List<StoryTimelineEntity>

    @Query("DELETE FROM story_timeline")
    suspend fun clearAllTimelines()
}

@Dao
interface SourceDao {
    @Query("SELECT * FROM followed_sources")
    fun getFollowedSources(): Flow<List<FollowedSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followSource(source: FollowedSourceEntity)

    @Query("DELETE FROM followed_sources WHERE id = :sourceId")
    suspend fun unfollowSource(sourceId: String)
    
    @Query("DELETE FROM followed_sources")
    suspend fun clearAll()
}

@Dao
interface PackageDao {
    @Query("SELECT * FROM followed_packages")
    fun getFollowedPackages(): Flow<List<FollowedPackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followPackage(pkg: FollowedPackageEntity)

    @Query("DELETE FROM followed_packages WHERE id = :pkgId")
    suspend fun unfollowPackage(pkgId: String)
    
    @Query("DELETE FROM followed_packages")
    suspend fun clearAll()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
    
    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}

@Database(
    entities = [
        SavedStoryEntity::class, 
        FollowedSourceEntity::class, 
        FollowedPackageEntity::class, 
        NotificationEntity::class,
        StoryEntity::class,
        StoryTimelineEntity::class
    ], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun timelineDao(): TimelineDao
    abstract fun sourceDao(): SourceDao
    abstract fun packageDao(): PackageDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gundem_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
