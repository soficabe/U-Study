package com.example.u_study.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val errorMessage: String? = null //se settata viene mostrata nella LoginScreen
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
                _state.update { it.copy(errorMessage = "Tutti i campi sono obbligatori.") }
                return
            }

            if (!currentState.termsAccepted) {
                _state.update { it.copy(errorMessage = "Devi accettare i termini e le condizioni.") }
                return
            }

            if (currentState.password != currentState.confirmPassword) {
                _state.update { it.copy(errorMessage = "Le password non coincidono.") }
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val result = authRepository.signUp(currentState.email, currentState.password, currentState.firstName, currentState.lastName)
                _state.update { it.copy(registerResult = result, isLoading = false) }

                when(result) {
                    RegisterResult.UserExisting -> {
                        _state.update { it.copy(errorMessage = "Questo utente esiste già.") }
                    }
                    RegisterResult.Error -> {
                        _state.update { it.copy(errorMessage = "Si è verificato un errore imprevisto.") }
                    }
                    RegisterResult.Success -> {
                        //preso da supabase (documentazione). è qui che va aggiunto il codice per l'inserimento
                        //dei dati in supabase?
                        /*val addUser = City(name = "The Shire", countryId = 554)
                        supabase.from("cities").insert(city)*/
                    }
                }
            }

        }

    }
}