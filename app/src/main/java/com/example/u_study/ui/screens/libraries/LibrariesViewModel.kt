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
    val cities: List<String> = emptyList()
)

interface LibrariesActions {
    fun addFavLib(lib: String)
    fun removeFavLib(favLib: String)
}

class LibrariesViewModel (private val libraryRepository: LibraryRepository): ViewModel() {

    private val _state = MutableStateFlow(LibrariesState())
    val state = _state.asStateFlow()

    init {
        loadLibraries()
    }

    private fun loadLibraries() {
        viewModelScope.launch {
            val loadedLibs = libraryRepository.getLibraries()

            //se vogliamo le città da supabase già ordinate (ci potrebbero servire magari per la barra di ricerca?):
            val uniqueCities = loadedLibs.map { it.city }.distinct().sorted()

            _state.update {
                it.copy(
                    libs = loadedLibs,
                    cities = uniqueCities
                )
            }
        }
    }


    val actions = object : LibrariesActions {

        override fun addFavLib(lib: String) {
            TODO()
        }

        override fun removeFavLib(favLib: String) {
            TODO()
        }
    }
}