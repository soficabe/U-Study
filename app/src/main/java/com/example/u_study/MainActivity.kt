package com.example.u_study

import android.os.Bundle
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
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}

