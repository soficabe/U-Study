package com.example.u_study

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.u_study.data.models.Theme
import com.example.u_study.ui.UStudyNavGraph
import com.example.u_study.ui.screens.settings.SettingsViewModel
import com.example.u_study.ui.theme.U_StudyTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import com.example.u_study.data.models.Language
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.Auth
import java.util.Locale

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

            LanguageProvider(language = settingsState.lang) {
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

@Composable
fun LanguageProvider(
    language: Language,
    content: @Composable () -> Unit
) {
    val locale = Locale(language.code)
    val configuration = LocalConfiguration.current
    configuration.setLocale(locale)
    val context = LocalContext.current.createConfigurationContext(configuration)

    // Fornisce il contesto con la lingua corretta a tutti i Composable figli
    CompositionLocalProvider(LocalContext provides context) {
        content()
    }
}