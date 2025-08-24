package com.example.u_study.ui.screens.favLibraries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.repositories.LibraryRepository
import com.example.u_study.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

class FavLibrariesViewModel(private val libraryRepository: LibraryRepository,
                            private val userRepository: UserRepository
): ViewModel() {

    private val _state = MutableStateFlow(FavLibrariesState())
    val state = _state.asStateFlow()

    init {
        loadLibraries()
    }

    private fun loadLibraries() {
        viewModelScope.launch {

            val allLibs = libraryRepository.getFavouriteLibraries()

            val visitedIds = userRepository.getVisitedLibraries().first().map { it.libId }.toSet()

            val enrichedLibs = allLibs.map { lib ->
                lib.copy(isVisited = lib.id in visitedIds)
            }

            // Applica il filtro della barra di ricerca sulla lista finale
            val filteredList = if (_state.value.searchQuery.isBlank()) {
                enrichedLibs
            } else {
                enrichedLibs.filter { library ->
                    library.city.startsWith(_state.value.searchQuery, ignoreCase = true)
                }
            }

            _state.update { it.copy(favLibs = filteredList.sortedBy { it.city }) }

        }
    }

    val actions = object : FavLibrariesActions {
        override fun removeFavLib(libraryId: Int) {
            viewModelScope.launch {
                libraryRepository.removeFavourite(libraryId)
                loadLibraries() //ricarica la lista per mostrare il cuore vuoto
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