package com.example.u_study.ui.screens.modifyUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.UpdateUserResult
import com.example.u_study.data.repositories.UserRepository
import com.example.u_study.data.repositories.UpdateUserProfileResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SaveProfileResult {
    data object Success : SaveProfileResult
    data object EmailAlreadyExists : SaveProfileResult
    data object ValidationError : SaveProfileResult
    data object NetworkError : SaveProfileResult
    data class Error(val message: String) : SaveProfileResult
}

data class ModifyUserState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val originalFirstName: String = "",
    val originalLastName: String = "",
    val originalEmail: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val hasChanges: Boolean
        get() = firstName != originalFirstName ||
                lastName != originalLastName ||
                email != originalEmail
}

interface ModifyUserActions {
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setEmail(email: String)
    fun saveChanges()
    fun clearMessages()
}

class ModifyUserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ModifyUserState())
    val state = _state.asStateFlow()

    val actions = object : ModifyUserActions {
        override fun setFirstName(firstName: String) {
            _state.update { it.copy(firstName = firstName, errorMessage = null) }
        }

        override fun setLastName(lastName: String) {
            _state.update { it.copy(lastName = lastName, errorMessage = null) }
        }

        override fun setEmail(email: String) {
            _state.update { it.copy(email = email, errorMessage = null) }
        }

        override fun saveChanges() {
            saveUserProfile()
        }

        override fun clearMessages() {
            _state.update { it.copy(errorMessage = null, isSuccess = false) }
        }
    }

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currentUser = authRepository.getUser()
                val userProfile = userRepository.getUser(currentUser.id)

                val firstName = userProfile?.name ?: ""
                val lastName = userProfile?.surname ?: ""
                val email = userProfile?.email ?: currentUser.email ?: ""

                _state.update { currentState ->
                    currentState.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        originalFirstName = firstName,
                        originalLastName = lastName,
                        originalEmail = email,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Errore nel caricamento dei dati del profilo"
                    )
                }
            }
        }
    }

    private fun saveUserProfile() {
        viewModelScope.launch {
            when (val result = validateAndSaveProfile()) {
                is SaveProfileResult.Success -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            isSuccess = true,
                            originalFirstName = it.firstName,
                            originalLastName = it.lastName,
                            originalEmail = it.email
                        )
                    }
                }
                is SaveProfileResult.EmailAlreadyExists -> {
                    _state.update {
                        it.copy(isSaving = false, errorMessage = "Questa email è già in uso")
                    }
                }
                is SaveProfileResult.ValidationError -> {
                    // Il messaggio di errore è già stato impostato dalla validazione
                    _state.update { it.copy(isSaving = false) }
                }
                is SaveProfileResult.NetworkError -> {
                    _state.update {
                        it.copy(isSaving = false, errorMessage = "Errore di connessione")
                    }
                }
                is SaveProfileResult.Error -> {
                    _state.update {
                        it.copy(isSaving = false, errorMessage = result.message)
                    }
                }
            }
        }
    }

    private suspend fun validateAndSaveProfile(): SaveProfileResult {
        val currentState = _state.value

        // Validazione
        if (currentState.firstName.isBlank()) {
            _state.update { it.copy(errorMessage = "Il nome non può essere vuoto") }
            return SaveProfileResult.ValidationError
        }

        if (currentState.lastName.isBlank()) {
            _state.update { it.copy(errorMessage = "Il cognome non può essere vuoto") }
            return SaveProfileResult.ValidationError
        }

        if (currentState.email.isBlank()) {
            _state.update { it.copy(errorMessage = "L'email non può essere vuota") }
            return SaveProfileResult.ValidationError
        }

        if (!isValidEmail(currentState.email)) {
            _state.update { it.copy(errorMessage = "Formato email non valido") }
            return SaveProfileResult.ValidationError
        }

        if (!currentState.hasChanges) {
            _state.update { it.copy(errorMessage = "Nessuna modifica da salvare") }
            return SaveProfileResult.ValidationError
        }

        _state.update { it.copy(isSaving = true, errorMessage = null) }

        try {
            val currentUser = authRepository.getUser()

            // Determina cosa è cambiato
            val nameChanged = currentState.firstName != currentState.originalFirstName
            val surnameChanged = currentState.lastName != currentState.originalLastName
            val emailChanged = currentState.email != currentState.originalEmail

            // Aggiorna l'email tramite Supabase Auth se è cambiata
            if (emailChanged) {
                when (val authResult = authRepository.updateUserEmail(currentState.email)) {
                    is UpdateUserResult.Success -> {
                        // Email aggiornata con successo in auth
                    }
                    is UpdateUserResult.EmailAlreadyExists -> {
                        return SaveProfileResult.EmailAlreadyExists
                    }
                    is UpdateUserResult.Error -> {
                        return SaveProfileResult.Error("Errore nell'aggiornamento dell'email")
                    }
                }
            }

            // Aggiorna nome e cognome se necessario
            if (nameChanged || surnameChanged) {
                when (val userResult = userRepository.updateUserProfile(
                    userId = currentUser.id,
                    name = if (nameChanged) currentState.firstName else null,
                    surname = if (surnameChanged) currentState.lastName else null
                )) {
                    is UpdateUserProfileResult.Success -> {
                        return SaveProfileResult.Success
                    }
                    is UpdateUserProfileResult.Error -> {
                        return SaveProfileResult.Error("Errore nell'aggiornamento del profilo")
                    }
                }
            } else {
                // Solo email cambiata, già gestita dal trigger
                return SaveProfileResult.Success
            }

        } catch (e: Exception) {
            return SaveProfileResult.Error("Errore imprevisto: ${e.message}")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}