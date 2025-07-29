package com.example.u_study.data.repositories

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email

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
    ): RegisterResult {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            RegisterResult.Success
        } catch (_: AuthRestException) {
            RegisterResult.UserExisting
        } catch (e: Exception) {
            throw e
            RegisterResult.Error
        }
    }
}