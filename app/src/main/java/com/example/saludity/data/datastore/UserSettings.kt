package com.example.saludity.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserSettings(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LANGUAGE_PREFERENCE = stringPreferencesKey("language_preference")
    }

    val languagePreference: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_PREFERENCE] ?: "es"
    }

    suspend fun saveLanguagePreference(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_PREFERENCE] = language
        }
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    val themePreference: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[THEME_PREFERENCE]
    }

    suspend fun saveThemePreference(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PREFERENCE] = theme
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: false
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
