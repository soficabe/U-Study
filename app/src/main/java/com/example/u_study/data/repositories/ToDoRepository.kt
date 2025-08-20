package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.ToDo
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ToDoRepository (private val postgrest: Postgrest) {

    private val todoTable = postgrest.from("ToDo")

    /* recupera la lista dei to do dell'utente attualmente loggato. Visto che su supabase
    * user_id = auth.uid(), l'app riceverà solo i to do dell'utente loggato */
    suspend fun getTodos(): List<ToDo> {
        return todoTable.select().decodeList<ToDo>()
    }

    /* aggiunge nuovo to do su supabase */
    suspend fun addTodo(content: String) {
        try {
            val newTodo = buildJsonObject {
                put("content", content)
            }
            todoTable.insert(newTodo)
        } catch (e: Exception) {
            Log.e("TodoRepository", "Error adding todo", e)
        }
    }

    suspend fun updateTodo(id: Int, isCompleted: Boolean) {
        try {
            todoTable.update(
                {
                    set("completed", isCompleted)
                }
            ) { //blocco per query
                filter {
                    ToDo::id eq id
                }
            }
        } catch (e: Exception) {
            Log.e("TodoRepository", "Error updating todo", e)
        }
    }

    suspend fun deleteTodo(id: Int) {
        try {
            todoTable.delete {
                filter {
                    ToDo::id eq id
                }
            }
        } catch (e: Exception) {
            Log.e("TodoRepository", "Error deleting todo", e)
        }
    }

}
