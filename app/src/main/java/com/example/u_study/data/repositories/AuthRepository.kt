package com.example.u_study.data.repositories

import android.util.Log
import androidx.annotation.StringRes
import com.example.u_study.R
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * Sealed interface per rappresentare i possibili esiti della registrazione.
 * Utilizza sealed interface per garantire type safety e exhaustive when.
 */
sealed interface RegisterResult {
    data object Start : RegisterResult
    data object Success : RegisterResult
    data object UserExisting : RegisterResult
    data object Error : RegisterResult
}

/**
 * Sealed interface per rappresentare i possibili esiti del login.
 */
sealed interface LoginResult {
    data object Start : LoginResult
    data object Success : LoginResult
    data object InvalidCredentials : LoginResult
    data object Error : LoginResult
}

/**
 * Sealed interface per rappresentare i possibili esiti dell'aggiornamento password.
 * Include riferimento a risorse string per messaggi localizzati.
 */
sealed interface UpdatePasswordResult {
    data object Success : UpdatePasswordResult
    data class Error(@StringRes val messageResId: Int) : UpdatePasswordResult
}

/**
 * Sealed interface per rappresentare i possibili esiti dell'aggiornamento dati utente.
 */
sealed interface UpdateUserResult {
    data object Success : UpdateUserResult
    data object EmailAlreadyExists : UpdateUserResult
    data class Error(@StringRes val messageResId: Int) : UpdateUserResult
}

/**
 * Repository per la gestione dell'autenticazione utente.
 *
 * Fornisce un'astrazione delle operazioni di autenticazione Supabase,
 * gestendo registrazione, login, aggiornamento profili e sessioni.
 * Implementa il pattern Repository per disaccoppiare la logica di business
 * dalle specifiche implementazioni di Supabase Auth.
 */
class AuthRepository (
    private val auth: Auth
) {

    /**
     * Utente attualmente autenticato.
     * Restituisce null se nessuna sessione è attiva.
     */
    val user: UserInfo?
        get() = (auth.sessionStatus.value as? SessionStatus.Authenticated)?.session?.user

    /**
     * StateFlow reattivo dello stato della sessione.
     * Permette di osservare cambiamenti nello stato di autenticazione.
     */
    val sessionStatus: StateFlow<SessionStatus> = auth.sessionStatus

    /**
     * Recupera le informazioni dell'utente dalla sessione corrente.
     *
     * @param updateSession se true, aggiorna la sessione dal server
     * @return informazioni utente complete
     * @throws Exception se non c'è una sessione attiva
     */
    suspend fun getUser(): UserInfo {
        return auth.retrieveUserForCurrentSession(true)
    }

    /**
     * Registra un nuovo utente con email/password.
     *
     * @param email indirizzo email dell'utente
     * @param password password in chiaro
     * @param name nome dell'utente
     * @param surname cognome dell'utente
     * @return RegisterResult indicante l'esito dell'operazione
     */
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
                // Metadati aggiuntivi salvati nel profilo auth
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

    /**
     * Autentica un utente esistente con email/password.
     *
     * @param email indirizzo email
     * @param password password dell'utente
     * @return LoginResult indicante l'esito del login
     */
    suspend fun signIn(email: String, password: String): LoginResult {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            LoginResult.Success
        } catch (_: AuthRestException) {
            // AuthRestException per credenziali non valide
            LoginResult.InvalidCredentials
        } catch (_: Exception) {
            // Altri errori (rete, configurazione, ecc.)
            LoginResult.Error
        }
    }

    /**
     * Autentica un utente tramite Google OAuth.
     *
     * @return LoginResult indicante l'esito del login
     */
    suspend fun signInWithGoogle(): LoginResult {
        return try {
            auth.signInWith(Google)
            LoginResult.Success
        } catch (e: Exception) {
            LoginResult.Error
        }
    }

    /**
     * Termina la sessione dell'utente corrente.
     * Rimuove i token e pulisce lo stato di autenticazione.
     */
    suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Aggiorna la password dell'utente autenticato.
     *
     * @param newPassword nuova password in chiaro
     * @return UpdatePasswordResult indicante l'esito dell'operazione
     */
    suspend fun updatePassword(newPassword: String): UpdatePasswordResult {
        return try {
            auth.updateUser {
                password = newPassword
            }
            UpdatePasswordResult.Success
        } catch (e: Exception) {
            Log.e("AuthRepository", "Update password failed", e)
            UpdatePasswordResult.Error(R.string.errorString)
        }
    }

    /**
     * Aggiorna l'indirizzo email dell'utente autenticato.
     *
     * Gestisce il caso specifico di email già esistente nel sistema.
     *
     * @param email nuovo indirizzo email
     * @return UpdateUserResult indicante l'esito dell'operazione
     */
    suspend fun updateUserEmail(email: String): UpdateUserResult {
        return try {
            // Aggiorna solo l'email nell'auth di Supabase
            auth.updateUser {
                this.email = email
            }
            UpdateUserResult.Success
        } catch (e: AuthRestException) {
            Log.e("AuthRepository", "Update user email failed - Auth exception", e)
            // Controllo specifico per email già esistente
            if (e.message?.contains("email", ignoreCase = true) == true &&
                e.message?.contains("already", ignoreCase = true) == true) {
                UpdateUserResult.EmailAlreadyExists
            } else {
                UpdateUserResult.Error(R.string.errorString)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Update user email failed", e)
            UpdateUserResult.Error(R.string.errorString)
        }
    }
}