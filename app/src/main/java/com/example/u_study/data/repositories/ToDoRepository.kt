package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.ToDo
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
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



/*
Il TodoRepository è un "magazzino" specializzato che gestisce tutte le comunicazioni tra la tua app e la tabella
To Do su Supabase. Prende in input Postgrest, che è lo "strumento" specializzato di Supabase per parlare con il database.


Immagina il tuo TodoViewModel come un manager. Il manager non va di persona in magazzino a prendere gli oggetti;
chiama il magazziniere e gli dice cosa fare.

- Il TodoViewModel è il manager: Contiene la logica di alto livello ("voglio la lista dei To-Do",
"aggiungi questo nuovo To-Do").

- Il TodoRepository è il magazziniere: È l'esperto che sa esattamente dove si trova la tabella To Do e come
interagire con essa.

Questo approccio, chiamato Repository Pattern, serve a tenere il codice organizzato e flessibile. Se un domani
cambiassi database, dovresti solo aggiornare il "magazziniere" (TodoRepository), ma il "manager" (TodoViewModel)
non se ne accorgerebbe nemmeno.


Il tuo database su Supabase parla il linguaggio SQL, che è molto potente ma complesso. La tua app in Kotlin non
parla SQL direttamente. Serve un traduttore.

Postgrest è il traduttore ufficiale di Supabase. È uno strumento che prende semplici comandi dalla tua app
(come "prendi tutto dalla tabella To Do") e li traduce in complesse query SQL per il database. Poi prende la
risposta dal database e la ritraduce in un formato che la tua app Kotlin può capire (la data class To do).

Il TodoRepository ha bisogno dell'oggetto Postgrest perché è lo strumento operativo che usa per comunicare.
Quando scrivi postgrest.from("To Do"), stai dicendo al traduttore: "Ok, preparati, le prossime operazioni che
ti chiedo di fare saranno sulla tabella To Do".
 */
