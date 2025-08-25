package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.FavLibrary
import com.example.u_study.data.database.entities.Library
import com.example.u_study.data.database.entities.VisitedLibrary
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LibraryRepository (private val postgrest: Postgrest, private val auth: Auth) {

    private val libraryTable = postgrest.from("Library")
    private val favLibraryTable = postgrest.from("FavLibrary")
    private val visitedTable = postgrest.from("VisitedLibrary")


    suspend fun getLibraries(): List<Library> {
        return try {
            var favouriteIds: Set<Int> = emptySet()

            //controllo se user è loggato. Se lo è scarichiamo i suoi preferiti
            if (auth.currentUserOrNull() != null) {
                favouriteIds = favLibraryTable.select {
                    filter {
                        FavLibrary::userId eq (auth.currentUserOrNull()?.id ?: "")
                    }
                }.decodeList<FavLibrary>().map { it.libId }.toSet()
            }

            //la lista di tutte le biblioteche si vede sempre
            val allLibraries = libraryTable.select().decodeList<Library>()

            //arricchisco i dati con i favorite
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

            //dettagli base della biblio
            val library = libraryTable.select {
                filter { Library::id eq id }
            }.decodeList<Library>().firstOrNull() ?: return null

            val userId = auth.currentUserOrNull()?.id
            if (userId == null) return library

            //la biblio è tra i preferiti?
            val favouriteEntry = favLibraryTable.select {
                filter {
                    FavLibrary::userId eq userId
                    FavLibrary::libId eq id
                }
            }.decodeList<FavLibrary>()

            //la biblio è stata visitata?
            val visitedEntry = visitedTable.select {
                filter {
                    VisitedLibrary::userId eq userId
                    VisitedLibrary::libId eq id
                }
            }.decodeList<VisitedLibrary>()


            library.copy(
                isFavourite = favouriteEntry.isNotEmpty(),
                isVisited = visitedEntry.isNotEmpty()
            )

        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error fetching library by id", e)
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