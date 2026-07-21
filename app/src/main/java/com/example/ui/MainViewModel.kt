package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val storyDao = db.storyDao()
    private val sourceDao = db.sourceDao()
    private val packageDao = db.packageDao()
    private val notificationDao = db.notificationDao()
    private val prefsManager = PreferencesManager(application)
    private val geminiService = GeminiService()

    // Preferences state
    val preferences: StateFlow<UserPreferences> = prefsManager.preferencesFlow

    // DB state observers
    private val savedStoryIds = storyDao.getSavedStories().map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet<String>())

    private val followedSourceIds = sourceDao.getFollowedSources().map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet<String>())

    private val followedPackageIds = packageDao.getFollowedPackages().map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet<String>())

    val notifications: StateFlow<List<NotificationEntity>> = notificationDao.getNotifications()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // UI filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tümü")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedFilter = MutableStateFlow("En Yeni")
    val selectedFilter = _selectedFilter.asStateFlow()

    private val rssService = RssService()
    private val _liveStories = MutableStateFlow<List<Story>>(emptyList())
    val liveStories = _liveStories.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _enrichedStories = MutableStateFlow<Map<String, Story>>(emptyMap())
    val enrichedStories = _enrichedStories.asStateFlow()

    private val _isEnriching = MutableStateFlow<Set<String>>(emptySet())
    val isEnriching = _isEnriching.asStateFlow()

    // Hydrated Lists (combining live RSS data and dynamic AI enrichments with local DB state)
    val stories: StateFlow<List<Story>> = combine(savedStoryIds, _liveStories, _enrichedStories) { savedIds, live, enriched ->
        val allStories = live
        allStories.map { story ->
            val finalStory = enriched[story.id] ?: story
            finalStory.copy(isSaved = savedIds.contains(story.id))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val packages: StateFlow<List<GundemPackage>> = followedPackageIds.map { followedIds ->
        MockData.gundemPackages.map { pkg ->
            pkg.copy(isFollowing = followedIds.contains(pkg.id))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MockData.gundemPackages)

    val sources: StateFlow<List<Source>> = followedSourceIds.map { followedIds ->
        MockData.sources.map { src ->
            src.copy(isFollowing = followedIds.contains(src.id))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MockData.sources)

    // Chatbot State
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("AI", "Merhaba! Ben GündemAI asistanıyım. Takip ettiğiniz konulardaki gelişmelerle ilgili sorularınızı bana sorabilirsiniz. Örn: 'Bugün yapay zekâ alanında ne oldu?' veya 'Osimhen transfer edildi mi?'")
        )
    )
    val chatHistory = _chatHistory.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading = _isChatLoading.asStateFlow()

    init {
        // Fetch live news on startup
        refreshNews()
    }

    fun refreshNews() {
        if (_isRefreshing.value) return
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                val categories = listOf("Yapay Zekâ", "Teknoloji", "Transfer", "Ekonomi")
                val fetchedStories = mutableListOf<Story>()
                categories.forEach { cat ->
                    val list = rssService.fetchCategoryRss(cat)
                    fetchedStories.addAll(list)
                }
                _liveStories.value = fetchedStories

                // Generate real-time notifications for the top critical/high stories from our actual live feed!
                val importantStories = fetchedStories.filter { it.importance == ImportanceLevel.CRITICAL || it.importance == ImportanceLevel.HIGH }
                if (importantStories.isNotEmpty()) {
                    val newNotifs = importantStories.take(5).map { story ->
                        NotificationEntity(
                            id = "notif_live_${story.id}",
                            title = if (story.importance == ImportanceLevel.CRITICAL) "Kritik Gelişme 🚨" else "Önemli Gelişme 📌",
                            body = story.title,
                            type = story.importance.name,
                            timestamp = System.currentTimeMillis(),
                            isRead = false,
                            actionUrl = story.id
                        )
                    }
                    notificationDao.insertNotifications(newNotifs)
                } else {
                    // Fallback to general notification if no critical stories yet
                    val welcomeNotif = NotificationEntity(
                        id = "notif_welcome",
                        title = "Gündem Başladı! ⚡",
                        body = "Gelişmiş GündemAI ile gerçek zamanlı haber akışı ve analizler tamamen hazır.",
                        type = "INFO",
                        timestamp = System.currentTimeMillis(),
                        isRead = false,
                        actionUrl = null
                    )
                    notificationDao.insertNotifications(listOf(welcomeNotif))
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error refreshing live news: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun enrichStoryWithGemini(storyId: String) {
        if (!storyId.startsWith("rss_")) return
        if (_enrichedStories.value.containsKey(storyId)) return
        if (_isEnriching.value.contains(storyId)) return

        _isEnriching.value = _isEnriching.value + storyId
        viewModelScope.launch {
            try {
                val baseStory = stories.value.firstOrNull { it.id == storyId } ?: return@launch
                val result = geminiService.generateStoryEnrichment(baseStory.title, baseStory.category, baseStory.sourceName)
                if (result != null) {
                    val enrichedStory = baseStory.copy(
                        contentWhat = result.contentWhat,
                        contentWhy = result.contentWhy,
                        aiComment = result.aiComment,
                        consensusPoints = result.consensusPoints,
                        unresolvedPoints = result.unresolvedPoints,
                        importance = result.importance,
                        status = result.status,
                        timeline = result.timeline.mapIndexed { index, t ->
                            com.example.data.StoryTimelineItem(
                                id = "tl_${storyId}_$index",
                                storyId = storyId,
                                time = t.time,
                                title = t.description,
                                content = t.description,
                                sourceName = t.sourceName
                            )
                        },
                        enrichedSources = emptyList()
                    )
                    _enrichedStories.value = _enrichedStories.value + (storyId to enrichedStory)
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Error enriching story $storyId: ${e.message}")
            } finally {
                _isEnriching.value = _isEnriching.value - storyId
            }
        }
    }

    // Actions
    fun toggleSaveStory(storyId: String) {
        viewModelScope.launch {
            val isSaved = savedStoryIds.value.contains(storyId)
            if (isSaved) {
                dao.unsaveStory(storyId)
            } else {
                dao.saveStory(SavedStoryEntity(storyId))
            }
        }
    }

    fun toggleFollowSource(sourceId: String) {
        viewModelScope.launch {
            val isFollowed = followedSourceIds.value.contains(sourceId)
            if (isFollowed) {
                dao.unfollowSource(sourceId)
            } else {
                dao.followSource(FollowedSourceEntity(sourceId))
            }
        }
    }

    fun toggleFollowPackage(pkgId: String) {
        viewModelScope.launch {
            val isFollowed = followedPackageIds.value.contains(pkgId)
            if (isFollowed) {
                dao.unfollowPackage(pkgId)
            } else {
                dao.followPackage(FollowedPackageEntity(pkgId))
            }
        }
    }

    fun markNotificationAsRead(notifId: String) {
        viewModelScope.launch {
            dao.markNotificationAsRead(notifId)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            dao.markAllNotificationsAsRead()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setOnboardingPreferences(
        name: String,
        interests: Set<String>,
        volume: String,
        frequencies: Set<String>
    ) {
        prefsManager.updateOnboarding(name, interests, volume, frequencies)
    }

    fun setTheme(isDark: Boolean) {
        prefsManager.updateTheme(isDark)
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            // Reset prefs
            prefsManager.resetData()
            // Reset DB tables for complete fresh restart
            storyDao.clearSavedStories()
            sourceDao.clearFollowedSources()
            packageDao.clearFollowedPackages()
            
            // Reinsert notifications
            notificationDao.clearAllNotifications()
            
            // Clear chat
            _chatHistory.value = listOf(
                ChatMessage("AI", "Onboarding sıfırlandı. Yeni profiliniz için hazırım! Bana her şeyi sorabilirsiniz.")
            )
        }
    }

    fun sendMessageToAI(text: String) {
        if (text.isBlank()) return
        
        val userMsg = ChatMessage("USER", text)
        _chatHistory.value = _chatHistory.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            try {
                val response = geminiService.askQuestion(text, stories.value)
                val aiMsg = ChatMessage("AI", response)
                _chatHistory.value = _chatHistory.value + aiMsg
            } catch (e: Exception) {
                _chatHistory.value = _chatHistory.value + ChatMessage("AI", "Bir hata oluştu: ${e.localizedMessage}")
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    // Returns a beautiful 2-minute summary in Turkish summarizing current high priority developments
    fun generate2MinuteSummary(): String {
        val prefs = preferences.value
        val userInterests = prefs.selectedInterests
        
        // Filter stories relevant to interests
        val relevantStories = stories.value.filter { story ->
            userInterests.any { interest -> story.category.contains(interest, ignoreCase = true) || interest.contains(story.category, ignoreCase = true) }
        }

        val targetStories = if (relevantStories.isNotEmpty()) relevantStories else stories.value
        val critical = targetStories.filter { it.importance == ImportanceLevel.CRITICAL }
        val high = targetStories.filter { it.importance == ImportanceLevel.HIGH }
        
        val builder = StringBuilder()
        builder.append("Günün Önemli Gelişmeleri Özet Raporu ⚡\n\n")
        builder.append("Merhaba ${prefs.name}! Seçtiğiniz ilgi alanlarına göre hazırladığımız 2 dakikalık kişisel bülteniniz hazır:\n\n")

        if (critical.isNotEmpty()) {
            builder.append("🚨 KRİTİK GELİŞME:\n")
            critical.take(2).forEach {
                builder.append("• *${it.title}*: ${it.summary}\n")
            }
            builder.append("\n")
        }

        if (high.isNotEmpty()) {
            builder.append("📌 ÖNEMLİ BAŞLIKLAR:\n")
            high.take(2).forEach {
                builder.append("• *${it.title}*: ${it.summary}\n")
            }
            builder.append("\n")
        }

        builder.append("Kaydedilen kaynak havuzunuzda bugün toplam ${targetStories.size} gelişme analiz edildi. ")
        builder.append("Gündem yoğunluğu seviyeniz '${prefs.volumeLevel}' olarak ayarlı.")
        
        return builder.toString()
    }
}
