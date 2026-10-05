package com.example.aurorah.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.aurorah.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserSession(
    val isLoggedIn: Boolean = false,
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "Guest"
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
        val FULL_NAME = stringPreferencesKey("full_name")
        val EMAIL = stringPreferencesKey("email")
        val ROLE = stringPreferencesKey("role")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LAST_SYNCED = stringPreferencesKey("last_synced")
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val modeName = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        runCatching { ThemeMode.valueOf(modeName) }.getOrDefault(ThemeMode.SYSTEM)
    }

    val languageCodeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LANGUAGE_CODE] ?: "en"
    }

    val userSessionFlow: Flow<UserSession> = context.dataStore.data.map { preferences ->
        UserSession(
            isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] ?: false,
            username = preferences[PreferencesKeys.USERNAME] ?: "",
            fullName = preferences[PreferencesKeys.FULL_NAME] ?: "",
            email = preferences[PreferencesKeys.EMAIL] ?: "",
            role = preferences[PreferencesKeys.ROLE] ?: "Guest"
        )
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    val lastSyncedFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_SYNCED] ?: "Just now"
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setLanguageCode(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CODE] = languageCode
        }
    }

    suspend fun setUserSession(session: UserSession) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = session.isLoggedIn
            preferences[PreferencesKeys.USERNAME] = session.username
            preferences[PreferencesKeys.FULL_NAME] = session.fullName
            preferences[PreferencesKeys.EMAIL] = session.email
            preferences[PreferencesKeys.ROLE] = session.role
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences[PreferencesKeys.USERNAME] = ""
            preferences[PreferencesKeys.FULL_NAME] = ""
            preferences[PreferencesKeys.EMAIL] = ""
            preferences[PreferencesKeys.ROLE] = "Guest"
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateLastSynced(timestamp: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNCED] = timestamp
        }
    }
}
