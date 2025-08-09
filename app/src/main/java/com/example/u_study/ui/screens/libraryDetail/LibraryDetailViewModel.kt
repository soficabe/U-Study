package com.example.u_study.ui.screens.libraryDetail

import androidx.lifecycle.ViewModel
import com.example.u_study.data.database.entities.Library
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LibraryDetailState(
    //che valori inserire?
    val library: Library? = null
)

interface LibraryDetailActions {
    fun onFavouriteClick()
}

class LibraryDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(LibraryDetailState())
    val state = _state.asStateFlow()

    val actions = object : LibraryDetailActions {
        override fun onFavouriteClick() {
            TODO("Not yet implemented")
        }

    }

}