package com.example.u_study.ui.screens.register

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.R
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.RegisterResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,
    val registerResult: RegisterResult = RegisterResult.Error,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null //se settata viene mostrata nella RegisterScreen
)

interface RegisterActions {
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun setConfirmPassword(confirmPassword: String)
    fun changeTerms(termsAccepted: Boolean)
    fun register()
}

class RegisterViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    val actions = object : RegisterActions {
        override fun setFirstName(firstName: String) =
            _state.update { it.copy(firstName = firstName) }

        override fun setLastName(lastName: String) =
            _state.update { it.copy(lastName = lastName) }

        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }

        override fun setPassword(password: String) =
            _state.update { it.copy(password = password) }

        override fun setConfirmPassword(confirmPassword: String) =
            _state.update { it.copy(confirmPassword = confirmPassword) }

        override fun changeTerms(termsAccepted: Boolean) =
            _state.update { it.copy(termsAccepted = termsAccepted) }

        override fun register() {
            //per resettare messaggio di errore
            _state.update { it.copy(errorMessage = null) }

            val currentState = _state.value

            //prima fa i dovuti controlli: che tutti i campi siano compilati, che i termini siano
            //accettati, che le due password corrispondano
            if (currentState.firstName.isBlank() || currentState.lastName.isBlank() ||
                currentState.email.isBlank() || currentState.password.isBlank()
            ) {
                _state.update { it.copy(errorMessage = R.string.requiredFields_error) }
                return
            }

            if (!currentState.termsAccepted) {
                _state.update { it.copy(errorMessage = R.string.acceptTerms_error) }
                return
            }

            if (currentState.password != currentState.confirmPassword) {
                _state.update { it.copy(errorMessage = R.string.passwordNotMatch_error) }
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val result = authRepository.signUp(currentState.email, currentState.password, currentState.firstName, currentState.lastName)
                _state.update { it.copy(registerResult = result, isLoading = false) }

                when(result) {
                    RegisterResult.Start -> {

                    }
                    RegisterResult.UserExisting -> {
                        _state.update { it.copy(errorMessage = R.string.userExisting_error) }
                    }
                    RegisterResult.Error -> {
                        _state.update { it.copy(errorMessage = R.string.classicError_error) }
                    }
                    RegisterResult.Success -> {
                        //teoricamente non va scritto niente qui
                    }
                }
            }

        }

    }
}