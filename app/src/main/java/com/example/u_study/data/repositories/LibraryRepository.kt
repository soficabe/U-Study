package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.Library
import io.github.jan.supabase.postgrest.Postgrest

class LibraryRepository (private val postgrest: Postgrest) {

    private val libraryTable = postgrest.from("Library")


    suspend fun getLibraries(): List<Library> {
        return try {
            libraryTable.select().decodeList<Library>()
        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error to get libraries", e)
            emptyList() //se errore -> lista vuota
        }
    }

    /*suspend fun getLibraryById(id: Int): Library? {
        return try {
            libraryTable.select {
                filter {
                    Library::id eq id
                }
            }.singleOrNull<Library>() //recupera un solo elemento o null
        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error to get library by id", e)
            null
        }
    }*/

    //questa funzione sotto fa esattamente quello che farebbe quella sopra,
    //ma è un po' più brigosa. L'ho fatta al volo perché mi dava errore nell'import
    //di singleOrNull (che è un import di github, quindi puoi immaginare come sia complesso).
    //se riesci ad importarla tu, elimina pure questa sotto e lascia questa sopra :P

    suspend fun getLibraryById(id: Int): Library? {
        return try {
            val resultList = libraryTable.select {
                filter {
                    Library::id eq id
                }
            }.decodeList<Library>()

            if (resultList.size == 1) {
                resultList.first()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LibraryRepository", "Error to get library by id", e)
            null
        }
    }
}