package com.example.u_study.ui.screens.libraryDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.repositories.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LibraryDetailState(
    //che valori inserire? forse qualcosa sui preferiti
    val library: Library? = null
)

interface LibraryDetailActions {
    fun onFavouriteClick()
}

class LibraryDetailViewModel(
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryDetailState())
    val state = _state.asStateFlow()

    init {
        val libraryId: String? = savedStateHandle["libraryId"]

        if (libraryId != null) {
            loadLibraryDetails(libraryId.toInt())
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
            TODO("Not yet implemented")
        }

    }

}