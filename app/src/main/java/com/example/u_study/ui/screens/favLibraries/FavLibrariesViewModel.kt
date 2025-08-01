package com.example.u_study.ui.screens.favLibraries

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FavLibrariesState(
    val cities: List<String>,
    var favLibs: List<String>
)

interface FavLibrariesActions {
    fun removeFavLib(favLib: String)
}

class FavLibrariesViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        FavLibrariesState(
            listOf("Bologna", "Cesena", "Cesenatico", "Faenza", "Forlì", "Imola", "Ozzano d'Emilia", "Ravenna", "Rimini"),
            listOf("Lib 1","Lib 2","Lib 3","Lib 4")
        )
    )
    val state = _state.asStateFlow()

    val actions = object : FavLibrariesActions {
        override fun removeFavLib(favLib: String) {
            TODO()
        }
    }
}