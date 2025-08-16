package com.example.u_study.ui.screens.libraryDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.LibraryRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LibraryDetailState(
    //che valori inserire? forse qualcosa sui preferiti
    val library: Library? = null,
    val isAuthenticated: Boolean = false
)

interface LibraryDetailActions {
    fun onFavouriteClick()
}

class LibraryDetailViewModel(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryDetailState())
    val state = _state.asStateFlow()

    init {
        val libraryId: String? = savedStateHandle["libraryId"]

        if (libraryId != null) {
            loadLibraryDetails(libraryId.toInt())
        }

        viewModelScope.launch {
            authRepository.sessionStatus.collect { sessionStatus ->
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        _state.update {
                            it.copy(
                                isAuthenticated = true,
                            )
                        }
                    }
                    else -> {
                        _state.update {
                            it.copy(
                                isAuthenticated = false,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadLibraryDetails(id: Int) {
        viewModelScope.launch {
            val libraryDetails = libraryRepository.getLibraryById(id)
            _state.update { it.copy( library = libraryDetails) }
        }
    }

    val actions = object : LibraryDetailActions {
        override fun onFavouriteClick() {
            val currentLibrary = _state.value.library ?: return

            viewModelScope.launch {
                if (currentLibrary.isFavourite) {
                    libraryRepository.removeFavourite(currentLibrary.id)
                } else {
                    libraryRepository.addFavourite(currentLibrary.id)
                }
                //ricarico dati per aggiornare UI
                loadLibraryDetails(currentLibrary.id)
            }
        }

    }

}