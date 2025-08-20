package com.example.u_study.ui.screens.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.ToDo
import com.example.u_study.data.repositories.ToDoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TodoState(
    val todos: List<ToDo> = emptyList(),
    val activeFilters: Set<TodoFilter> = emptySet(), //set di filtri attivi, inizialmente vuoto
    val isLoading: Boolean = true
)

enum class TodoFilter {
    ONGOING, COMPLETED
}

interface TodoActions {
    fun addTodo(content: String)
    fun removeTodo(id: Int)
    fun toggleComplete(id: Int, isCompleted: Boolean)
    fun toggleFilter(filter: TodoFilter)
}

class TodoViewModel(
    private val todoRepository: ToDoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TodoState())
    val state = _state.asStateFlow()

    init {
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val todos = todoRepository.getTodos().sortedBy { it.id } //ordinato per ID
            val filters = _state.value.activeFilters

            val todosFiltered = when {
                filters.isEmpty() || filters.size == 2 -> todos
                filters.contains(TodoFilter.ONGOING) -> todos.filter { !it.completed }
                filters.contains(TodoFilter.COMPLETED) -> todos.filter { it.completed }
                else -> todos
            }

            _state.update { it.copy(todos = todosFiltered, isLoading = false) }
        }
    }

    val actions = object : TodoActions {

        override fun addTodo(content: String) {
            viewModelScope.launch {
                todoRepository.addTodo(content)
                loadTodos() //ogni volta che aggiungiamo un elemento, ricarichiamo la lista
            }
        }

        override fun removeTodo(id: Int) {
            viewModelScope.launch {
                todoRepository.deleteTodo(id)
                loadTodos() // ricarichiamo la lista
            }
        }

        override fun toggleComplete(id: Int, isCompleted: Boolean) {
            viewModelScope.launch {
                todoRepository.updateTodo(id, isCompleted)
                loadTodos() // ricarichiamo la lista
            }
        }

        override fun toggleFilter(filter: TodoFilter) {
            val currentFilters = _state.value.activeFilters.toMutableSet()
            if (filter in currentFilters) {
                currentFilters.remove(filter) //rimosso se c'è già
            } else {
                currentFilters.add(filter) //aggiunto se non c'è ancora
            }
            _state.update { it.copy(activeFilters = currentFilters) }
            loadTodos()
        }
    }
}
