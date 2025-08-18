package com.example.u_study.data.repositories

import android.util.Log
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface ImageUploadResult {
    data class Success(val imageUrl: String) : ImageUploadResult
    data class Error(val message: String) : ImageUploadResult
}

/**
 * Repository per la gestione delle immagini su Supabase Storage.
 */
class ImageRepository(
    private val storage: Storage
) {

    companion object {
        private const val PROFILE_IMAGES_BUCKET = "profile-images"
        private const val TAG = "ImageRepository"
    }

    /**
     * Carica un'immagine profilo su Supabase Storage.
     *
     * @param userId ID dell'utente
     * @param imageData ByteArray dell'immagine processata
     * @return ImageUploadResult con l'URL pubblico o errore
     */
    suspend fun uploadProfileImage(
        userId: String,
        imageData: ByteArray
    ): ImageUploadResult {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "profile_$userId.jpg"

                // Upload dell'immagine
                storage.from(PROFILE_IMAGES_BUCKET).upload(fileName, imageData) {
                    // Sovrascrivi se esiste già
                    upsert = true
                }

                // Ottieni l'URL pubblico
                val publicUrl = storage.from(PROFILE_IMAGES_BUCKET).publicUrl(fileName)

                Log.d(TAG, "Image uploaded successfully: $publicUrl")
                ImageUploadResult.Success(publicUrl)

            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image: ${e.message}", e)
                ImageUploadResult.Error("Errore durante l'upload dell'immagine: ${e.message}")
            }
        }
    }

    /**
     * Elimina un'immagine profilo da Supabase Storage.
     *
     * @param userId ID dell'utente
     * @return Boolean indicating success
     */
    suspend fun deleteProfileImage(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "profile_$userId.jpg"
                storage.from(PROFILE_IMAGES_BUCKET).delete(fileName)
                Log.d(TAG, "Image deleted successfully: $fileName")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting image: ${e.message}", e)
                false
            }
        }
    }

    /**
     * Ottieni l'URL pubblico dell'immagine profilo.
     *
     * @param userId ID dell'utente
     * @return URL pubblico dell'immagine o null se non esiste
     */
    fun getProfileImageUrl(userId: String): String? {
        return try {
            val fileName = "profile_$userId.jpg"
            storage.from(PROFILE_IMAGES_BUCKET).publicUrl(fileName)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting image URL: ${e.message}", e)
            null
        }
    }
}

