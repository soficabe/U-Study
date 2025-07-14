package com.example.u_study.ui.screens.todo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class TodoState(val todos: List<String>)

interface TodoActions {
    fun addTodo(todo: String)
    fun removeTodo(todo: String)
    fun toggleComplete(todo: String)
}

class TodoViewModel : ViewModel() {
    val state = MutableStateFlow(
        TodoState(
            listOf("1", "2", "3")
        )
    )

    val actions = object : TodoActions {
        override fun addTodo(todo: String) {
            TODO("Not yet implemented")
        }

        override fun removeTodo(todo: String) {
            TODO("Not yet implemented")
        }

        override fun toggleComplete(todo: String) {
            TODO("Not yet implemented")
        }
    }
}
