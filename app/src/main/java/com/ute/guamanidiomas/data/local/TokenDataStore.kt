package com.ute.guamanidiomas.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = intPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
        private val IS_STAFF = booleanPreferencesKey("is_staff")
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val APP_LANGUAGE = stringPreferencesKey("app_language")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val userSnapshot: Flow<UserSnapshot?> = context.dataStore.data.map { prefs ->
        val id = prefs[USER_ID]
        val username = prefs[USERNAME]
        val email = prefs[EMAIL]
        val isStaff = prefs[IS_STAFF] ?: false
        val role = prefs[USER_ROLE] ?: "user"
        
        if (id != null && username != null && email != null) {
            UserSnapshot(id, username, email, isStaff, role)
        } else {
            null
        }
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE] ?: false
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[APP_LANGUAGE] ?: "es"
    }

    val isSoundEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SOUND_ENABLED] ?: true
    }

    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }

    suspend fun setAppLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[APP_LANGUAGE] = lang
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_ENABLED] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun saveTokens(access: String, refresh: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = access
            prefs[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveUser(id: Int, username: String, email: String, isStaff: Boolean, role: String = "user") {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USERNAME] = username
            prefs[EMAIL] = email
            prefs[IS_STAFF] = isStaff
            prefs[USER_ROLE] = role
        }
    }

    suspend fun getAccessToken(): String? = context.dataStore.data.map { it[ACCESS_TOKEN] }.first()
    suspend fun getRefreshToken(): String? = context.dataStore.data.map { it[REFRESH_TOKEN] }.first()

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    data class UserSnapshot(
        val id: Int,
        val username: String,
        val email: String,
        val isStaff: Boolean,
        val role: String = "user"
    )
}