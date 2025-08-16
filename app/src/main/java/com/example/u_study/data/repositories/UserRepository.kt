package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Sealed interface per rappresentare i possibili esiti dell'aggiornamento profilo utente.
 */
sealed interface UpdateUserProfileResult {
    data object Success : UpdateUserProfileResult
    data object Error : UpdateUserProfileResult
}

/**
 * Repository per la gestione dei dati utente nel database.
 *
 * Gestisce le operazioni CRUD sui profili utente attraverso Supabase Postgrest.
 * Separato da AuthRepository per mantenere la separazione delle responsabilità:
 * - AuthRepository: gestione autenticazione e sessioni
 * - UserRepository: gestione dati profilo nel database
 *
 * Tutte le operazioni vengono eseguite nel contesto IO per non bloccare il main thread.
 */
class UserRepository(
    private val supabase: SupabaseClient
) {

    /**
     * Recupera i dati di un utente specifico dal database.
     *
     * @param id ID univoco dell'utente (UUID da Supabase Auth)
     * @return User entity se trovato, null altrimenti
     */
    suspend fun getUser(id: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("User")
                    .select {
                        filter {
                            eq("id", id)
                        }
                    }.decodeSingleOrNull<User>()
            } catch (e: Exception) {
                Log.e("UserRepository", "Error fetching user: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Aggiorna il profilo di un utente esistente nel database.
     *
     * Supporta aggiornamenti parziali: vengono modificati solo i campi non-null.
     * Costruisce dinamicamente la query di update includendo solo i campi specificati.
     *
     * @param userId ID dell'utente da aggiornare
     * @param name nuovo nome (opzionale)
     * @param surname nuovo cognome (opzionale)
     * @return UpdateUserProfileResult indicante l'esito dell'operazione
     */
    suspend fun updateUserProfile(
        userId: String,
        name: String? = null,
        surname: String? = null
    ): UpdateUserProfileResult {
        return withContext(Dispatchers.IO) {
            try {
                // Costruzione dinamica dei campi da aggiornare
                val updateData = mutableMapOf<String, String?>()
                name?.let { updateData["name"] = it }
                surname?.let { updateData["surname"] = it }

                // Esecuzione update solo se ci sono campi da modificare
                if (updateData.isNotEmpty()) {
                    supabase.from("User")
                        .update(updateData) {
                            filter {
                                eq("id", userId)
                            }
                        }
                }

                UpdateUserProfileResult.Success
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating user profile: ${e.message}", e)
                UpdateUserProfileResult.Error
            }
        }
    }
}