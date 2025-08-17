package com.example.u_study.ui.screens.favLibraries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.repositories.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavLibrariesState(
    val favLibs: List<Library> = emptyList(),
    val searchQuery: String = "" //la barra della ricerca per ora sta cercando:

)

interface FavLibrariesActions {
    fun removeFavLib(libraryId: Int)
    fun onSearchQueryChanged(query: String)
    fun refresh()

}

class FavLibrariesViewModel(private val libraryRepository: LibraryRepository): ViewModel() {

    private val _state = MutableStateFlow(FavLibrariesState())
    val state = _state.asStateFlow()

    private var allLibraries: List<Library> = emptyList()

    init {
        loadLibraries()
    }

    private fun loadLibraries() {
        viewModelScope.launch {
            allLibraries = libraryRepository.getFavouriteLibraries()

            val filteredList = if (_state.value.searchQuery.isBlank()) {
                libraryRepository.getFavouriteLibraries()
            } else {
                libraryRepository.getFavouriteLibraries().filter { library ->
                    library.city.startsWith(_state.value.searchQuery, ignoreCase = true) //per maiuscole - minuscole
                }
            }
            _state.update { it.copy(favLibs = filteredList) }

        }
    }

    val actions = object : FavLibrariesActions {
        override fun removeFavLib(libraryId: Int) {
            viewModelScope.launch {
                libraryRepository.removeFavourite(libraryId)
                loadLibraries() // Ricarica la lista per mostrare il cuore vuoto
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