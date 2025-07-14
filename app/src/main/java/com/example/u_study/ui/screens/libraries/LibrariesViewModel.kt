package com.example.u_study.ui.screens.libraries

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LibrariesState(
    val cities: List<String>,
    var libs: List<String>
)

interface LibrariesActions {
    fun addFavLib(lib: String)
    fun removeFavLib(favLib: String)
}

class LibrariesViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        LibrariesState(
            listOf("Bologna", "Cesena", "Cesenatico", "Faenza", "Forlì", "Imola", "Ozzano d'Emilia", "Ravenna", "Rimini"),
            listOf("1","2","3","4")
        )
    )
    val state = _state.asStateFlow()

    val actions = object : LibrariesActions {
        override fun addFavLib(lib: String) {
            TODO()
        }

        override fun removeFavLib(favLib: String) {
            TODO()
        }
    }
}