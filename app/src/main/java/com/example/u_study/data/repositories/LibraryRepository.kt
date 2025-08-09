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
}