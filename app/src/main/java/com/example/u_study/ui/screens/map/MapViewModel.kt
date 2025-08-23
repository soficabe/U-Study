package com.example.u_study.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.repositories.LibraryRepository
import com.example.u_study.data.repositories.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

class MapViewModel(
    private val libraryRepository: LibraryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val libraries: StateFlow<List<Library>> = flow {
        emit(libraryRepository.getLibraries())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val visitedLibraries: StateFlow<Set<Int>> = userRepository.visitedLibraries
        .map { it.map { v -> v.libId }.toSet() }
        .onEach { Log.d("MapViewModel", "visitedLibraries changed: $it") }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    fun markLibraryVisited(libraryId: Int) {
        viewModelScope.launch {
            Log.d("MapViewModel", "markLibraryVisited($libraryId)")
            userRepository.markLibraryVisited(libraryId)
        }
    }
}