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
    val isLoading: Boolean = true
)

interface TodoActions {
    fun addTodo(content: String)
    fun removeTodo(id: Int)
    fun toggleComplete(id: Int, isCompleted: Boolean)
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
            _state.update { it.copy(todos = todos, isLoading = false) }
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
    }
}
