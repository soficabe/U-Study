package com.example.u_study.utils


import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.u_study.data.models.Language
import com.example.u_study.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


fun getUserLanguageSync(context: Context): Language {
    val LANGUAGE_KEY = stringPreferencesKey("language")
    return runBlocking {
        val prefs = context.dataStore.data.first()
        val langString = prefs[LANGUAGE_KEY]
        try {
            if (langString != null) Language.valueOf(langString) else Language.ENGLISH
        } catch (_: Exception) {
            Language.ENGLISH
        }
    }
}