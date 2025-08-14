package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface UpdateUserProfileResult {
    data object Success : UpdateUserProfileResult
    data object Error : UpdateUserProfileResult
}

class UserRepository(
    private val supabase: SupabaseClient
) {
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

    suspend fun updateUserProfile(
        userId: String,
        name: String? = null,
        surname: String? = null
    ): UpdateUserProfileResult {
        return withContext(Dispatchers.IO) {
            try {
                val updateData = mutableMapOf<String, String?>()
                name?.let { updateData["name"] = it }
                surname?.let { updateData["surname"] = it }

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