package com.example.u_study.ui.screens
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.FilterChipsRow
import com.example.u_study.ui.composables.NavigationBar

@Composable
fun ToDoScreen(navController: NavHostController) {
    val chipLabels = listOf("On Going", "Completed")

    Scaffold (
        topBar = {
            AppBar("To-Do List", navController)
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
                FilterChipsRow(items = chipLabels)
            }
            item {
                AddTodoField(
                    onSubmit = { /*TODO*/},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                TodoItem(
                    onToggle = { /*TODO*/},
                    onDelete = { /*TODO*/}
                )
            }
            item {
                TodoItem(
                    onToggle = { /*TODO*/},
                    onDelete = { /*TODO*/}
                )
            }
            /*items() {

            }*/
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
                .height(64.dp)
                .toggleable(
                    value = /*item.isComplete*/false,
                    onValueChange = { onToggle() },
                    role = Role.Checkbox
                )
                .padding(start = 8.dp)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = /*item.isComplete*/false, onCheckedChange = null)
            Text(
                /*item.content*/"ToDo1",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp).weight(1F)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Close, "Remove TODO")
            }
        }
    }
}