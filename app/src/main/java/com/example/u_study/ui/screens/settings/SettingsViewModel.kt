package com.example.u_study.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.models.Theme
import com.example.u_study.data.repositories.SettingsRepository
import com.example.u_study.ui.screens.login.LoginState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsState(val theme: Theme)

interface SettingsActions {
    fun changeTheme(theme: Theme) : Job
}

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    val state = repository.theme.map { SettingsState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SettingsState(Theme.System)
    )

    val actions = object : SettingsActions {
        override fun changeTheme(theme: Theme) = viewModelScope.launch {
            repository.setTheme(theme)
        }
    }
}