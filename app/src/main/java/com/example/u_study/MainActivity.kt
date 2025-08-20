package com.example.u_study

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.u_study.data.models.Language
import com.example.u_study.data.models.Theme
import com.example.u_study.ui.UStudyNavGraph
import com.example.u_study.ui.screens.settings.SettingsViewModel
import com.example.u_study.ui.theme.U_StudyTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.Auth
import java.util.Locale

// Importa la tua utility per leggere la lingua
import com.example.u_study.utils.getUserLanguageSync

class MainActivity : ComponentActivity() {
    private val auth: Auth by inject()

    // Helper per applicare la lingua subito
    private fun applyLocale(language: Language) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Carica la lingua PRIMA di setContent
        val userLang = getUserLanguageSync(applicationContext)
        applyLocale(userLang)

        // Gestisce il deep link OAuth se presente nell'intent di apertura
        lifecycleScope.launch { handleDeepLink(intent) }
        enableEdgeToEdge()

        setContent {
            val settingsViewModel = koinViewModel<SettingsViewModel>()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

            U_StudyTheme(
                darkTheme = when (settingsState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()
                UStudyNavGraph(
                    settingsViewModel,
                    settingsState,
                    navController
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch { handleDeepLink(intent) }
    }

    private suspend fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "app" && uri.host == "supabase.com") {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    try {
                        auth.exchangeCodeForSession(code)
                        Log.d("MainActivity", "OAuth login successful")
                    } catch (e: Exception) {
                        Log.e("MainActivity", "OAuth callback failed", e)
                    }
                }
            }
        }
    }
}