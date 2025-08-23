package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.User
import com.example.u_study.data.database.entities.VisitedLibrary
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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
 */
class UserRepository(
    private val supabase: SupabaseClient
) {
    // patch: aggiungi uno StateFlow che puoi aggiornare dopo ogni insert!
    private val _visitedLibraries = MutableStateFlow<List<VisitedLibrary>>(emptyList())
    val visitedLibraries = _visitedLibraries.asStateFlow()

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
     * Ora supporta anche l'aggiornamento dell'URL dell'immagine profilo.
     *
     * @param userId ID dell'utente da aggiornare
     * @param name nuovo nome (opzionale)
     * @param surname nuovo cognome (opzionale)
     * @param imageUrl nuovo URL dell'immagine profilo (opzionale)
     * @return UpdateUserProfileResult indicante l'esito dell'operazione
     */
    suspend fun updateUserProfile(
        userId: String,
        name: String? = null,
        surname: String? = null,
        imageUrl: String? = null
    ): UpdateUserProfileResult {
        return withContext(Dispatchers.IO) {
            try {
                // Costruzione dinamica dei campi da aggiornare
                val updateData = mutableMapOf<String, String?>()
                name?.let { updateData["name"] = it }
                surname?.let { updateData["surname"] = it }
                imageUrl?.let { updateData["image"] = it }

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

    /**
     * Restituisce tutte le biblioteche visitate dall'utente loggato (reactive Flow).
     */
    fun getVisitedLibraries(): Flow<List<VisitedLibrary>> = flow {
        val list = supabase.from("VisitedLibrary")
            .select()
            .decodeList<VisitedLibrary>()
        emit(list)
    }

    /**
     * Marca una biblioteca come visitata per l'utente loggato (no-op se già esiste).
     */
    suspend fun markLibraryVisited(libraryId: Int) {
        withContext(Dispatchers.IO) {
            try {
                // Controlla se già visitata
                val exists = supabase.from("VisitedLibrary")
                    .select {
                        filter { eq("lib_id", libraryId) }
                    }
                    .decodeList<VisitedLibrary>()
                    .isNotEmpty()

                if (!exists) {
                    val data = mapOf("lib_id" to libraryId)
                    supabase.from("VisitedLibrary").insert(data)
                    Log.d("UserRepository", "Inserted visited library $libraryId")
                } else {
                    Log.d("UserRepository", "Library $libraryId already marked as visited")
                }
                // Aggiorna la lista delle visitate
                val updatedResults = supabase.from("VisitedLibrary")
                    .select()
                    .decodeList<VisitedLibrary>()
                _visitedLibraries.value = updatedResults
            } catch (e: Exception) {
                Log.e("UserRepository", "Error marking library as visited: ${e.message}", e)
            }
        }
    }
}