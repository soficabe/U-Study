package com.example.u_study

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.u_study.data.models.Theme
import com.example.u_study.ui.UStudyNavGraph
import com.example.u_study.ui.screens.settings.SettingsViewModel
import com.example.u_study.ui.theme.U_StudyTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.Auth

class MainActivity : ComponentActivity() {
    private val auth: Auth by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gestisce il deep link per OAuth (intent iniziale)
        lifecycleScope.launch {
            handleDeepLink(intent)
        }

        enableEdgeToEdge()
        setContent {
            val settingsViewModel = koinViewModel<SettingsViewModel>()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

            /*LaunchedEffect(settingsState.lang) {
                val appLocale = LocaleListCompat.forLanguageTags(settingsState.lang.code)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }*/

            U_StudyTheme(
                darkTheme = when (settingsState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()
                UStudyNavGraph(settingsViewModel, settingsState, navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Gestisce il deep link quando l'app è già aperta
        lifecycleScope.launch {
            handleDeepLink(intent)
        }
    }

    private suspend fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "app" && uri.host == "supabase.com") {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    try {
                        auth.exchangeCodeForSession(code)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "OAuth callback failed", e)
                    }
                }
            }
        }
    }
}