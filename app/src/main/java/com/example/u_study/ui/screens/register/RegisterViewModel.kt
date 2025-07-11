package com.example.u_study.ui.screens.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false
)

interface RegisterActions {
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun setConfirmPassword(confirmPassword: String)
    fun changeTerms(termsAccepted: Boolean)
    //fun register()
}

class LoginViewModel : ViewModel() {
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




        //override fun register() = {}

    }
}