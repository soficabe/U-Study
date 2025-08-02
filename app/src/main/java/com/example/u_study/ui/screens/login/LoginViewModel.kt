package com.example.u_study.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.R
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val loginResult: LoginResult = LoginResult.Start,
    val isLoggingIn: Boolean = false,
    val errorMessageLog: Int? = null
)

interface LoginActions {
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun login()
}

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    val actions = object : LoginActions {
        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }

        override fun setPassword(password: String) =
            _state.update { it.copy(password = password) }

        override fun login() {
            _state.update { it.copy(errorMessageLog = null) }


            viewModelScope.launch {
                _state.update { it.copy(isLoggingIn = true) }
                val email = _state.value.email
                val password = _state.value.password
                val result = authRepository.signIn(email, password)
                _state.update { it.copy(loginResult = result, isLoggingIn = false) }

                when(result) {
                    LoginResult.InvalidCredentials -> {
                        _state.update { it.copy(errorMessageLog = R.string.invalidCredentials_error) }
                    }
                    LoginResult.Error -> {
                        _state.update { it.copy(errorMessageLog = R.string.classicError_error) }
                    }
                    else -> {
                        /* niente ciao */
                    }
                }
            }
        }

    }
}
