package com.example.u_study.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.R
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.RegisterResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class rappresentante lo stato completo della schermata di registrazione.
 * Contiene tutti i dati del form e lo stato dell'operazione di registrazione.
 *
 * @param firstName nome dell'utente
 * @param lastName cognome dell'utente
 * @param email indirizzo email per la registrazione
 * @param password password scelta dall'utente
 * @param confirmPassword conferma della password
 * @param termsAccepted flag per accettazione termini e condizioni
 * @param registerResult risultato dell'operazione di registrazione
 * @param isLoading flag per indicare operazione in corso
 * @param errorMessage risorsa string per messaggi di errore localizzati
 */
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

/**
 * Interface che definisce tutte le azioni possibili nella schermata di registrazione.
 * Utilizzato per separare le azioni dalla logica del ViewModel.
 */
interface RegisterActions {
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setEmail(email: String)
    fun setPassword(password: String)
    fun setConfirmPassword(confirmPassword: String)
    fun changeTerms(termsAccepted: Boolean)
    fun register()
}

/**
 * ViewModel per la gestione della logica della schermata di registrazione.
 *
 * Implementa il pattern Unidirectional Data Flow (UDF):
 * - State: stato immutabile esposto tramite StateFlow
 * - Actions: azioni che modificano lo stato
 * - Repository: per operazioni asincrone
 *
 * Gestisce validazione client-side, chiamate al repository e aggiornamenti di stato reattivi.
 */
class RegisterViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {

    // StateFlow privato mutabile per aggiornamenti interni
    private val _state = MutableStateFlow(RegisterState())

    // StateFlow pubblico read-only per osservazione da UI
    val state = _state.asStateFlow()

    /**
     * Implementazione concreta delle azioni di registrazione.
     * Utilizzata object expression per mantenere le azioni vicine alla logica del ViewModel.
     */
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

        /**
         * Esegue la registrazione dell'utente con validazione completa.
         *
         * Fasi del processo:
         * 1. Reset eventuali messaggi di errore precedenti
         * 2. Validazione client-side dei campi obbligatori
         * 3. Controllo accettazione termini
         * 4. Verifica corrispondenza password
         * 5. Chiamata asincrona al repository
         * 6. Gestione risultato con aggiornamento stato
         */
        override fun register() {
            // Reset messaggio di errore precedente
            _state.update { it.copy(errorMessage = null) }

            val currentState = _state.value

            // Validazione campi obbligatori
            if (currentState.firstName.isBlank() || currentState.lastName.isBlank() ||
                currentState.email.isBlank() || currentState.password.isBlank()
            ) {
                _state.update { it.copy(errorMessage = R.string.requiredFields_error) }
                return
            }

            // Controllo accettazione termini
            if (!currentState.termsAccepted) {
                _state.update { it.copy(errorMessage = R.string.acceptTerms_error) }
                return
            }

            // Verifica corrispondenza password
            if (currentState.password != currentState.confirmPassword) {
                _state.update { it.copy(errorMessage = R.string.passwordNotMatch_error) }
                return
            }

            // Esecuzione registrazione asincrona
            viewModelScope.launch {
                // Imposta stato di caricamento
                _state.update { it.copy(isLoading = true) }

                // Chiamata al repository
                val result = authRepository.signUp(
                    currentState.email,
                    currentState.password,
                    currentState.firstName,
                    currentState.lastName
                )

                // Aggiornamento stato con risultato
                _state.update { it.copy(registerResult = result, isLoading = false) }

                // Gestione specifica dei vari risultati
                when(result) {
                    RegisterResult.Start -> {
                        // Stato iniziale - nessuna azione richiesta
                    }
                    RegisterResult.Success -> {
                        // Successo - navigazione gestita dalla UI
                    }
                    RegisterResult.UserExisting -> {
                        _state.update { it.copy(errorMessage = R.string.userExisting_error) }
                    }
                    RegisterResult.Error -> {
                        _state.update { it.copy(errorMessage = R.string.classicError_error) }
                    }
                }
            }

        }

    }
}