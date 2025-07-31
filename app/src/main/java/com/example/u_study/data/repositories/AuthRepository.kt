package com.example.u_study.data.repositories

import android.util.Log
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

sealed interface RegisterResult {
    data object Success : RegisterResult
    data object UserExisting : RegisterResult
    data object Error : RegisterResult
}

class AuthRepository (
    private val auth: Auth
) {
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        surname: String
    ): RegisterResult {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", JsonPrimitive(name))
                    put("surname", JsonPrimitive(surname))
                }
            }
            RegisterResult.Success
        } catch (_: AuthRestException) {
            RegisterResult.UserExisting
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign up failed", e)
            RegisterResult.Error
        }
    }

}