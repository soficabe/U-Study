package com.example.u_study.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val firstName: String = "S",
    val lastName: String = "",
    val email: String = "sofiabianchi@gmail.com",
    val numTasksDone: Number = 0,
    val numStudySessions: Number = 0,
    val numVisitedLibraries: Number = 0,
    val numStudyHours: Number = 0,


    val isRefreshing: Boolean = false
)

interface ProfileActions {
    fun changeVariables(firstName: String, lastName: String, email: String)
    fun refreshProfile()
}

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val actions = object : ProfileActions {
        override fun changeVariables(firstName: String, lastName: String, email: String) {
            _state.update { it.copy(firstName = firstName,
                lastName = lastName,
                email = email)
            }
        }

        override fun refreshProfile() {
            authRepository.user?.let { user ->
                viewModelScope.launch {
                    _state.update { it.copy(isRefreshing = true) }
                    val profile = usersRepository.getUser(Uuid.parse(user.id))

                    _state.update {
                        it.copy(
                            profile = profile,

                            //isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }

    init {
        actions.refreshProfile()
    }
}