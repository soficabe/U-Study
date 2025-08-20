package com.example.u_study.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.User
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.UserRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
)

interface HomeActions {

}

class HomeViewModel (
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    val actions = object : HomeActions {

    }

    init {
        viewModelScope.launch {
            authRepository.sessionStatus.collect { sessionStatus ->
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        val userProfile = sessionStatus.session.user?.let {
                            userRepository.getUser(
                                it.id)
                        }
                        _state.update {
                            it.copy(
                                isAuthenticated = true,
                                user = userProfile
                            )
                        }
                    }
                    else -> {
                        _state.update {
                            it.copy(
                                isAuthenticated = false,
                                user = null
                            )
                        }
                    }
                }
            }
        }
    }
}

