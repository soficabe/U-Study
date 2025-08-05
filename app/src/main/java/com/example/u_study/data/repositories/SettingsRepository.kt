package com.example.u_study.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.u_study.data.models.Language
import com.example.u_study.data.models.Theme
import kotlinx.coroutines.flow.map

class SettingsRepository (
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    val theme = dataStore.data
        .map { preferences ->
            try {
                Theme.valueOf(preferences[THEME_KEY] ?: "System")
            } catch (_: Exception) {
                Theme.System
            }
        }

    val language = dataStore.data
        .map { preferences ->
            try {
                Language.valueOf(preferences[LANGUAGE_KEY] ?: Language.ENGLISH.name)
            } catch (_: Exception) {
                Language.ENGLISH
            }
        }

    suspend fun setTheme(theme: Theme) = dataStore.edit { preferences ->
        preferences[THEME_KEY] = theme.toString()
    }

    suspend fun setLanguage(languageCode: Language) = dataStore.edit { preferences ->
        preferences[LANGUAGE_KEY] = languageCode.toString()
    }

}