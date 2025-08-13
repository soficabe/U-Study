package com.example.u_study.ui.screens.favLibraries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.repositories.LibraryRepository
import com.example.u_study.ui.screens.libraries.LibrariesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavLibrariesState(
    val favLibs: List<Library> = emptyList(),
    val searchQuery: String = "" //la barra della ricerca per ora sta cercando:

)

interface FavLibrariesActions {
    fun removeFavLib(favLib: String)
    fun onSearchQueryChanged(query: String)

}

class FavLibrariesViewModel(private val libraryRepository: LibraryRepository): ViewModel() {

    private val _state = MutableStateFlow(FavLibrariesState())
    val state = _state.asStateFlow()

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
            _state.update { it.copy(favLibs = filteredList) }

        }
    }

    val actions = object : FavLibrariesActions {
        override fun removeFavLib(favLib: String) {
            TODO()
        }

        override fun onSearchQueryChanged(query: String) {
            _state.update { it.copy(searchQuery = query) }
            loadLibraries()
        }
    }
}