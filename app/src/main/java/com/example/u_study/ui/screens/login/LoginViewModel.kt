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

/**
 * Data class rappresentante lo stato della schermata di login.
 *
 * @param email indirizzo email inserito dall'utente
 * @param password password inserita dall'utente
 * @param loginResult risultato dell'operazione di login
 * @param isLoggingIn flag per indicare login in corso
 * @param errorMessageLog risorsa string per messaggi di errore localizzati
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val loginResult: LoginResult = LoginResult.Start,
    val isLoggingIn: Boolean = false,
    val errorMessageLog: Int? = null
)

/**
 * Interface che definisce le azioni disponibili nella schermata di login.
 * Mantiene separazione tra UI actions e business logic.
 */
interface LoginActions {
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun login()
    fun loginWithGoogle()
}

/**
 * ViewModel per la gestione della logica della schermata di login.
 *
 * Supporta due modalità di autenticazione:
 * - Login tradizionale con email/password
 * - Login tramite Google OAuth
 *
 * Segue il pattern UDF (Unidirectional Data Flow) per garantire
 * stato prevedibile e testabilità.
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // StateFlow privato per aggiornamenti interni
    private val _state = MutableStateFlow(LoginState())

    // StateFlow pubblico read-only per osservazione da UI
    val state = _state.asStateFlow()

    /**
     * Implementazione delle azioni di login.
     * Object expression per mantenere le azioni nel contesto del ViewModel.
     */
    val actions = object : LoginActions {
        override fun setEmail(email: String) =
            _state.update { it.copy(email = email) }

        override fun setPassword(password: String) =
            _state.update { it.copy(password = password) }

        /**
         * Esegue il login con email e password.
         *
         * Processo:
         * 1. Reset messaggi di errore precedenti
         * 2. Impostazione stato di caricamento
         * 3. Chiamata asincrona al repository
         * 4. Gestione dei vari risultati possibili
         * 5. Aggiornamento dello stato con il risultato
         */
        override fun login() {
            // Reset errore precedente
            _state.update { it.copy(errorMessageLog = null) }

            viewModelScope.launch {
                // Imposta stato di caricamento
                _state.update { it.copy(isLoggingIn = true) }

                // Estrae credenziali dallo stato corrente
                val email = _state.value.email
                val password = _state.value.password

                // Chiamata al repository per autenticazione
                val result = authRepository.signIn(email, password)

                // Aggiornamento stato con risultato
                _state.update { it.copy(loginResult = result, isLoggingIn = false) }

                // Gestione specifica dei risultati
                when(result) {
                    LoginResult.Start -> {
                        // Stato iniziale - nessuna azione richiesta
                    }
                    LoginResult.Success -> {
                        // Successo - navigazione gestita dalla UI
                    }
                    LoginResult.InvalidCredentials -> {
                        _state.update { it.copy(errorMessageLog = R.string.invalidCredentials_error) }
                    }
                    LoginResult.Error -> {
                        _state.update { it.copy(errorMessageLog = R.string.classicError_error) }
                    }
                }
            }
        }

        override fun loginWithGoogle() {
            viewModelScope.launch {
                _state.update { it.copy(isLoggingIn = true) }
                val result = authRepository.signInWithGoogle()
                _state.update { it.copy(loginResult = result, isLoggingIn = false) }
            }
        }

    }
}
