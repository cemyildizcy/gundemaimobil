package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferencesManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "gundem_ai_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _preferencesFlow = MutableStateFlow(getPreferences())
    val preferencesFlow: StateFlow<UserPreferences> = _preferencesFlow.asStateFlow()

    fun getPreferences(): UserPreferences {
        val name = prefs.getString("user_name", "Cem") ?: "Cem"
        val selectedInterests = prefs.getStringSet("selected_interests", setOf("Yapay Zekâ", "Teknoloji")) ?: setOf("Yapay Zekâ", "Teknoloji")
        val volumeLevel = prefs.getString("volume_level", "Dengeli") ?: "Dengeli"
        val notificationFreq = prefs.getStringSet("notification_freq", setOf("Sabah özeti", "Akşam özeti")) ?: setOf("Sabah özeti", "Akşam özeti")
        val isDarkTheme = prefs.getBoolean("is_dark_theme", true)
        val language = prefs.getString("language", "Türkçe") ?: "Türkçe"
        val isOnboarded = prefs.getBoolean("is_onboarded", false)

        return UserPreferences(
            name = name,
            selectedInterests = selectedInterests,
            volumeLevel = volumeLevel,
            notificationFreq = notificationFreq,
            isDarkTheme = isDarkTheme,
            language = language,
            isOnboarded = isOnboarded
        )
    }

    fun updateOnboarding(
        name: String,
        interests: Set<String>,
        volume: String,
        notifications: Set<String>
    ) {
        prefs.edit().apply {
            putString("user_name", name)
            putStringSet("selected_interests", interests)
            putString("volume_level", volume)
            putStringSet("notification_freq", notifications)
            putBoolean("is_onboarded", true)
            apply()
        }
        _preferencesFlow.value = getPreferences()
    }

    fun updateTheme(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
        _preferencesFlow.value = getPreferences()
    }

    fun updatePreferences(prefsInput: UserPreferences) {
        prefs.edit().apply {
            putString("user_name", prefsInput.name)
            putStringSet("selected_interests", prefsInput.selectedInterests)
            putString("volume_level", prefsInput.volumeLevel)
            putStringSet("notification_freq", prefsInput.notificationFreq)
            putBoolean("is_dark_theme", prefsInput.isDarkTheme)
            putString("language", prefsInput.language)
            putBoolean("is_onboarded", prefsInput.isOnboarded)
            apply()
        }
        _preferencesFlow.value = getPreferences()
    }

    fun resetData() {
        prefs.edit().clear().apply()
        _preferencesFlow.value = getPreferences()
    }
}
