package com.example.u_study.data.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.storage.Storage
import java.util.UUID

class ImageRepository(
    private val storage: Storage
) {
    suspend fun uploadProfileImage(userId: String, uri: Uri, context: Context): String? {
        Log.d("PROFILE_PHOTO", ">>> IN uploadProfileImage con URI: $uri")
        return try {
            val bucket = "profile-images"
            val extension = ".jpg"
            val fileName = "${userId}_${UUID.randomUUID()}$extension"
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("PROFILE_PHOTO", "openInputStream returned null for $uri")
                return null
            }
            val bytes = inputStream.readBytes()
            Log.d("PROFILE_PHOTO", "Read bytes from URI: $uri, size=${bytes.size}")
            if (bytes.isEmpty()) {
                Log.e("PROFILE_PHOTO", "Image bytes are empty for URI: $uri")
                return null
            }
            storage.from(bucket).upload(fileName, bytes) {
                upsert = true
            }
            storage.from(bucket).publicUrl(fileName)
        } catch (e: Exception) {
            Log.e("PROFILE_PHOTO", "Exception during upload: ${e.message}", e)
            null
        }
    }
}