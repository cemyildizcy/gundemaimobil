package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GundoDao {
    @Query("SELECT * FROM saved_stories")
    fun getSavedStories(): Flow<List<SavedStoryEntity>>

    @Query("SELECT * FROM saved_stories")
    suspend fun getSavedStoriesList(): List<SavedStoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStory(story: SavedStoryEntity)

    @Query("DELETE FROM saved_stories WHERE id = :storyId")
    suspend fun unsaveStory(storyId: String)


    @Query("SELECT * FROM followed_sources")
    fun getFollowedSources(): Flow<List<FollowedSourceEntity>>

    @Query("SELECT * FROM followed_sources")
    suspend fun getFollowedSourcesList(): List<FollowedSourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followSource(source: FollowedSourceEntity)

    @Query("DELETE FROM followed_sources WHERE id = :sourceId")
    suspend fun unfollowSource(sourceId: String)


    @Query("SELECT * FROM followed_packages")
    fun getFollowedPackages(): Flow<List<FollowedPackageEntity>>

    @Query("SELECT * FROM followed_packages")
    suspend fun getFollowedPackagesList(): List<FollowedPackageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followPackage(pkg: FollowedPackageEntity)

    @Query("DELETE FROM followed_packages WHERE id = :pkgId")
    suspend fun unfollowPackage(pkgId: String)


    @Query("SELECT * FROM notifications")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()
}

@Database(
    entities = [
        SavedStoryEntity::class,
        FollowedSourceEntity::class,
        FollowedPackageEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): GundoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gundem_ai_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
