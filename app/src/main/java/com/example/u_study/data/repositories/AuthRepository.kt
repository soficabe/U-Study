package com.example.u_study.data.repositories

import android.util.Log
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

sealed interface RegisterResult {
    data object Start : RegisterResult
    data object Success : RegisterResult
    data object UserExisting : RegisterResult
    data object Error : RegisterResult
}

sealed interface LoginResult {
    data object Success : LoginResult
    data object InvalidCredentials : LoginResult
    data object Error : LoginResult
    data object Start : LoginResult

}

sealed interface UpdatePasswordResult {
    data object Success : UpdatePasswordResult
    data class Error(val message: String?) : UpdatePasswordResult
}

class AuthRepository (
    private val auth: Auth
) {
    val user: UserInfo?
        get() = (auth.sessionStatus.value as? SessionStatus.Authenticated)?.session?.user

    val sessionStatus: StateFlow<SessionStatus> = auth.sessionStatus //mi serve per il logout

    suspend fun getUser(): UserInfo {
        return auth.retrieveUserForCurrentSession(true)
    }


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

    suspend fun signIn(email: String, password: String): LoginResult {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            LoginResult.Success
        } catch (_: AuthRestException) {
            LoginResult.InvalidCredentials
        } catch (_: Exception) {
            LoginResult.Error
        }
    }

    suspend fun signInWithGoogle(): Boolean {
        return try {
            auth.signInWith(Google)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun updatePassword(newPassword: String): UpdatePasswordResult {
        return try {
            auth.updateUser {
                password = newPassword
            }
            UpdatePasswordResult.Success
        } catch (e: Exception) {
            Log.e("AuthRepository", "Update password failed", e)
            UpdatePasswordResult.Error(e.message)
        }
    }
}