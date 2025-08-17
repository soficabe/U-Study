package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.FavLibrary
import com.example.u_study.data.database.entities.Library
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LibraryRepository (private val postgrest: Postgrest, private val auth: Auth) {

    private val libraryTable = postgrest.from("Library")
    private val favLibraryTable = postgrest.from("FavLibrary")


    suspend fun getLibraries(): List<Library> {
        return try {
            //prendo gli id di tutte le biblioteche preferite dell'user
            val favouriteIds = favLibraryTable.select {
                filter {
                    //confronta la proprietà userId di FavLibrary con l'id dell'utente loggato.
                    FavLibrary::userId eq (auth.currentUserOrNull()?.id ?: "")
                }
            }.decodeList<FavLibrary>().map { it.libId }.toSet()

            val allLibraries = libraryTable.select().decodeList<Library>() //tutte le biblioteche

            //tra tutte le biblio, quelle il cui id è in lista vengono messe come preferite
            allLibraries.map { library ->
                library.copy(isFavourite = library.id in favouriteIds)
            }
        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error fetching libraries with favourites", e)
            emptyList()
        }
    }

    //easy: filtriamo la lista per tenere solo le biblio con il cuore pieno
    suspend fun getFavouriteLibraries(): List<Library> {
        val allLib = getLibraries()
        return allLib.filter { it.isFavourite }
    }

    suspend fun getLibraryById(id: Int): Library? {
        return try {
            val libraryResultList = libraryTable.select {
                filter {
                    Library::id eq id
                }
            }.decodeList<Library>() //recupero dettagli biblio
            if (libraryResultList.size != 1) {
                return null
            } //(controllo errore)

            val library = libraryResultList.first() //questo perché ho usato decodeList e non singleOrNull

            //controllo se è tra i preferiti dell'utente
            val userId = auth.currentUserOrNull()?.id

            if (userId == null) { //se non c'è utente loggato
                return library
            }

            //esiste una riga nella tabella FavLibrary per questo utente e questa libreria?
            val favouriteEntry = favLibraryTable.select {
                filter {
                    FavLibrary::userId eq userId
                    FavLibrary::libId eq id
                }
            }.decodeList<FavLibrary>()

            // favouriteEntry non vuota -> abbiamo corrispondenza
            library.copy(isFavourite = favouriteEntry.isNotEmpty())

        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error to get library by id", e)
            null
        }
    }

    suspend fun addFavourite(libraryId: Int) {
        val newFav = buildJsonObject {
            put("lib_id", libraryId)
            // user_id viene inserito automaticamente dal database grazie al default
        }
        favLibraryTable.insert(newFav)
    }

    suspend fun removeFavourite(libraryId: Int) {
        favLibraryTable.delete {
            filter {
                eq("lib_id", libraryId)
                eq("user_id", auth.currentUserOrNull()?.id ?: "")
            }
        }
    }


}