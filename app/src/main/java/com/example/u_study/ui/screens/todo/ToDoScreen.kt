package com.example.u_study.ui.screens.todo

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.data.database.entities.ToDo
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.FilterChipsRow
import com.example.u_study.ui.composables.NavigationBar

@Composable
fun ToDoScreen(state: TodoState, actions: TodoActions, navController: NavHostController) {
    //val chipLabels = listOf(stringResource(R.string.onGoing_chip), stringResource(R.string.completed_chip))

    val onGoingLabel = stringResource(R.string.onGoing_chip)
    val completedLabel = stringResource(R.string.completed_chip)

    Scaffold (
        topBar = {
            AppBar(stringResource(R.string.toDoScreen_name), navController)
        },
        bottomBar = {
            NavigationBar(navController = navController)
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            item {
                FilterChipsRow(
                    items = TodoFilter.entries.toList(), //lista di ogg. da mostrare
                    itemLabel = { filter ->
                        when(filter) {
                            TodoFilter.ONGOING -> onGoingLabel
                            TodoFilter.COMPLETED -> completedLabel
                        }
                    },
                    selectedItems = state.activeFilters, //insieme degli oggetti selezionati
                    onItemSelected = actions::toggleFilter //azione da chiamare al click
                )
            }
            item {
                AddTodoField(
                    onSubmit = { content -> actions.addTodo(content) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(state.todos, key = { it.id }) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { actions.toggleComplete(todo.id, !todo.completed) },
                    onDelete = { actions.removeTodo(todo.id) }
                )
            }
        }
    }
}

@Composable
fun AddTodoField(
    onSubmit: (content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf("") }

    OutlinedTextField(
        value = content,
        onValueChange = { content = it },
        label = { Text("TODO") },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = {
                if (content.isBlank()) return@IconButton
                onSubmit(content)
                content = ""
            }) {
                Icon(Icons.Outlined.Add, "Add TODO")
            }
        }
    )
}

@Composable
fun TodoItem(
    todo: ToDo,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .defaultMinSize(64.dp)
                //.height(64.dp)
                .toggleable(
                    value = todo.completed,
                    onValueChange = { onToggle() },
                    role = Role.Checkbox
                )
                .padding(start = 8.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = todo.completed, onCheckedChange = null)
            Text(
                todo.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp).weight(1F)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Close, "Remove TODO")
            }
        }
    }
}