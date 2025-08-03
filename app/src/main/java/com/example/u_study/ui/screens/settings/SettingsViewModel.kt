package com.example.u_study.ui.screens.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.models.Language
import com.example.u_study.data.models.Theme
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.SettingsRepository
import com.example.u_study.ui.screens.login.LoginState
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val theme: Theme = Theme.System,
    val lang: Language = Language.ENGLISH,
    val isAuthenticated: Boolean = false
)

interface SettingsActions {
    fun changeTheme(theme: Theme) : Job
    fun changeLang(lang: Language) : Job
    fun logout()
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val state: StateFlow<SettingsState> =
        combine(
            settingsRepository.theme,
            settingsRepository.language,
            authRepository.sessionStatus
        ) { theme, language, sessionStatus ->
            SettingsState(
                theme = theme,
                lang = language,
                isAuthenticated = sessionStatus is SessionStatus.Authenticated
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

    /*val state: StateFlow<SettingsState> =
        settingsRepository.theme.combine(settingsRepository.language) { theme, language ->
            SettingsState(theme, language)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

    private val _state = MutableStateFlow(SettingsState())

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        val isAuthenticated = authRepository.user != null
        _state.update { it.copy(isAuthenticated = isAuthenticated) }
    }*/




    /*val state = repository.theme.map { SettingsState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingsState()
        //questo codice serviva quando c'era solo Tema, senza Linguaggio
    )*/

    val actions = object : SettingsActions {
        override fun changeTheme(theme: Theme) = viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }

        override fun changeLang(lang: Language) = viewModelScope.launch {
            settingsRepository.setLanguage(lang)
            val appLocale = LocaleListCompat.forLanguageTags(lang.toString())
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        override fun logout() {
            viewModelScope.launch {
                authRepository.signOut()
            }
        }
    }
}

/* nota per quando aggiungeremo altri dati, per esempio le notifiche push
 * - Considera che a questo punto avremo già aggiornato SettingsRepository
 * (con valori semplici tipo booleani non è necessario creare ulteriori file
 * in models. Infatti per theme è stato creato perché dato particolare (enum class con 3 valori))
 * - in Screen ci andrà la chiamata alla funzione fatta qui in ViewModel per aggiornare lo stato
 * - qui in ViewModel va aggiunta la variabile nello state, chiamare la funzione nella actions e
 * definirla nella funzione principale. Inoltre, "val state" va cambiata come segue:

// lo stato viene creato combinando i due flussi dal repository
    val state: StateFlow<SettingsState> =
        repository.theme.combine(repository.notificationsEnabled) { theme, notificationsEnabled ->
            SettingsState(theme, notificationsEnabled)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

 * Quel 5000 è un timeout di 5000 millisecondi (5 secondi). Non è strettamente necessario,
 * ma è una buona pratica per ottimizzare le risorse dell'app.
 */