package com.example.u_study.ui.screens.libraries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.repositories.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LibrariesState(
    val libs: List<Library> = emptyList(),
    val searchQuery: String = "" //la barra della ricerca per ora sta cercando:
)

interface LibrariesActions {
    fun addFavLib(lib: String)
    fun removeFavLib(favLib: String)
    fun onSearchQueryChanged(query: String)
}

class LibrariesViewModel (private val libraryRepository: LibraryRepository): ViewModel() {

    private val _state = MutableStateFlow(LibrariesState())
    val state = _state.asStateFlow()

    init {
        loadLibraries()
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

        override fun addFavLib(lib: String) {
            TODO()
        }

        override fun removeFavLib(favLib: String) {
            TODO()
        }

        override fun onSearchQueryChanged(query: String) {
            _state.update { it.copy(searchQuery = query) }
            loadLibraries()
        }
    }
}