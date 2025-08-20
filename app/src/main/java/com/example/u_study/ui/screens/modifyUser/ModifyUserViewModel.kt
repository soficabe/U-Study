package com.example.u_study.ui.screens.modifyUser

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.ImageRepository
import com.example.u_study.data.repositories.UpdateUserResult
import com.example.u_study.data.repositories.UserRepository
import com.example.u_study.data.repositories.UpdateUserProfileResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
    val imageUrl: String? = null,
    val originalFirstName: String = "",
    val originalLastName: String = "",
    val originalEmail: String = "",
    val originalImageUrl: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingImage: Boolean = false,
    val showImagePicker: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
) {
    val hasChanges: Boolean
        get() = firstName != originalFirstName ||
                lastName != originalLastName ||
                email != originalEmail ||
                imageUrl != originalImageUrl
}

interface ModifyUserActions {
    fun setFirstName(firstName: String)
    fun setLastName(lastName: String)
    fun setEmail(email: String)
    fun showImagePicker()
    fun hideImagePicker()
    fun onImageError(error: String)
    fun saveChanges()
    fun clearMessages()
    fun onProfileImageSelected(uri: Uri, context: Context)
}

class ModifyUserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
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

        override fun showImagePicker() {
            _state.update { it.copy(showImagePicker = true) }
        }

        override fun hideImagePicker() {
            _state.update { it.copy(showImagePicker = false) }
        }

        override fun onImageError(error: String) {
            _state.update { it.copy(errorMessage = error, isUploadingImage = false) }
        }

        override fun saveChanges() {
            saveUserProfile()
        }

        override fun clearMessages() {
            _state.update { it.copy(errorMessage = null, isSuccess = false) }
        }

        override fun onProfileImageSelected(uri: Uri, context: Context) {
            uploadProfileImage(uri, context)
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
                val imageUrl = userProfile?.image

                _state.update { currentState ->
                    currentState.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        imageUrl = imageUrl,
                        originalFirstName = firstName,
                        originalLastName = lastName,
                        originalEmail = email,
                        originalImageUrl = imageUrl,
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
                is SaveProfileResult.EmailAlreadyExists -> {
                    _state.update {
                        it.copy(isSaving = false, errorMessage = "Questa email è già in uso")
                    }
                }
                is SaveProfileResult.ValidationError -> {
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
                is SaveProfileResult.Success -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            isSuccess = true,
                            originalFirstName = it.firstName,
                            originalLastName = it.lastName,
                            originalEmail = it.email,
                            originalImageUrl = it.imageUrl
                        )
                    }
                }
            }
        }
    }

    private suspend fun validateAndSaveProfile(): SaveProfileResult {
        val currentState = _state.value

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

            val nameChanged = currentState.firstName != currentState.originalFirstName
            val surnameChanged = currentState.lastName != currentState.originalLastName
            val emailChanged = currentState.email != currentState.originalEmail
            val imageChanged = currentState.imageUrl != currentState.originalImageUrl

            if (emailChanged) {
                when (val authResult = authRepository.updateUserEmail(currentState.email)) {
                    is UpdateUserResult.Success -> {
                    }
                    is UpdateUserResult.EmailAlreadyExists -> {
                        return SaveProfileResult.EmailAlreadyExists
                    }
                    is UpdateUserResult.Error -> {
                        return SaveProfileResult.Error("Errore nell'aggiornamento dell'email")
                    }
                }
            }

            if (nameChanged || surnameChanged || imageChanged) {
                when (val userResult = userRepository.updateUserProfile(
                    userId = currentUser.id,
                    name = if (nameChanged) currentState.firstName else null,
                    surname = if (surnameChanged) currentState.lastName else null,
                    imageUrl = if (imageChanged) currentState.imageUrl else null
                )) {
                    is UpdateUserProfileResult.Success -> {
                        return SaveProfileResult.Success
                    }
                    is UpdateUserProfileResult.Error -> {
                        return SaveProfileResult.Error("Errore nell'aggiornamento del profilo")
                    }
                }
            } else {
                return SaveProfileResult.Success
            }

        } catch (e: Exception) {
            return SaveProfileResult.Error("Errore imprevisto: ${e.message}")
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun uploadProfileImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingImage = true, errorMessage = null) }
            try {
                Log.d("PROFILE_PHOTO", "Selected URI: $uri (scheme: ${uri.scheme})")
                // Se lo schema è file:// fai anche il controllo file diretto (per device vecchi)
                if (uri.scheme == "file" && uri.path != null) {
                    val file = File(uri.path!!)
                    Log.d("PROFILE_PHOTO", "path=${file.path} exists=${file.exists()} size=${file.length()}")
                    if (!file.exists() || file.length() == 0L) {
                        _state.update { it.copy(isUploadingImage = false, errorMessage = "Foto non trovata o file vuoto") }
                        return@launch
                    }
                }
                val currentUser = authRepository.getUser()
                val imageUrl = withContext(Dispatchers.IO) {
                    imageRepository.uploadProfileImage(currentUser.id, uri, context)
                }
                if (imageUrl != null) {
                    _state.update { it.copy(imageUrl = imageUrl, isUploadingImage = false) }
                } else {
                    _state.update { it.copy(isUploadingImage = false, errorMessage = "Errore upload immagine (file vuoto o non accessibile)") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isUploadingImage = false, errorMessage = "Errore upload immagine") }
            }
        }
    }
}