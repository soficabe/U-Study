package com.example.u_study.ui.screens.libraries

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

data class LibrariesState(
    val libs: List<Library> = emptyList(),
    val searchQuery: String = "", //la barra della ricerca per ora sta cercando:
    val isAuthenticated: Boolean = false
)

interface LibrariesActions {
    fun addFavLib(libraryId: Int)
    fun removeFavLib(libraryId: Int)
    fun onSearchQueryChanged(query: String)
    fun refresh()
}

class LibrariesViewModel (private val libraryRepository: LibraryRepository, private val authRepository: AuthRepository): ViewModel() {

    private val _state = MutableStateFlow(LibrariesState())
    val state = _state.asStateFlow()

    init {
        loadLibraries()
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

    private var allLibraries: List<Library> = emptyList()

    private fun loadLibraries() {
        viewModelScope.launch {
            allLibraries = libraryRepository.getLibraries()

            val filteredList = if (_state.value.searchQuery.isBlank()) {
                libraryRepository.getLibraries()
            } else {
                libraryRepository.getLibraries().filter { library ->
                    library.city.startsWith(_state.value.searchQuery, ignoreCase = true) //per maiuscole - minuscole
                }
            }
            _state.update { it.copy(libs = filteredList) }

        }
    }

    val actions = object : LibrariesActions {

        override fun addFavLib(libraryId: Int) {
            viewModelScope.launch {
                libraryRepository.addFavourite(libraryId)
                loadLibraries() // ricarico lista per mostrare cuore pieno!!
            }
        }

        override fun removeFavLib(libraryId: Int) {
            viewModelScope.launch {
                libraryRepository.removeFavourite(libraryId)
                loadLibraries()
            }
        }

        override fun onSearchQueryChanged(query: String) {
            _state.update { it.copy(searchQuery = query) }
            loadLibraries()
        }

        override fun refresh() {
            loadLibraries()
        }
    }
}