package com.example.u_study.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.repositories.ToDoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatsState(
    val numTasksDone: Number = 0,
    val numStudySessions: Number = 0,
    val numVisitedLibraries: Number = 0,
    val numStudyHours: Number = 0,
    val isLoading: Boolean = true
)

class StatsViewModel(
    private val toDoRepository: ToDoRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val todos = toDoRepository.getTodos().sortedBy { it.id } //ordinato per ID

            val completedTodos = todos.filter { it.completed }.size

            _state.update { it.copy(numTasksDone = completedTodos, isLoading = false) }
        }
    }
}