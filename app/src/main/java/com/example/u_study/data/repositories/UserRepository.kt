package com.example.u_study.data.repositories

import kotlin.uuid.Uuid

class UserRepository(
) {
    suspend fun getUser(id: String): Profile? {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from(Tables.PROFILES).select {
                    filter {
                        Profile::id eq id
                    }
                }.decodeSingleOrNull<Profile>()
            } catch (e: Exception) {
                Log.i("TAG", "Error fetching user: ${e.message}")
                null
            }
        }
    }
}