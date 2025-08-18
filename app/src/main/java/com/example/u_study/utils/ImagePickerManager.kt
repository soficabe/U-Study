package com.example.u_study.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Manager per gestire la selezione e cattura di immagini.
 * Utilizza i nuovi Activity Result APIs per gestire camera e photo picker.
 */
class ImagePickerManager(
    private val context: Context,
    private val onImageSelected: (Uri) -> Unit,
    private val onError: (String) -> Unit
) {

    private var imageUri: Uri? = null
    private var takePictureLauncher: ActivityResultLauncher<Uri>? = null
    private var pickImageLauncher: ActivityResultLauncher<String>? = null

    /**
     * Inizializza i launcher per camera e photo picker.
     * Deve essere chiamato nell'onCreate dell'Activity.
     */
    fun initializeLaunchers(activity: ComponentActivity) {
        // Launcher per catturare foto con camera
        takePictureLauncher = activity.registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            // Fix: usa una variabile locale per evitare smart cast issues
            val currentUri = imageUri
            if (success && currentUri != null) {
                onImageSelected(currentUri)
            } else {
                onError("Errore durante la cattura della foto")
            }
        }

        // Launcher per selezionare immagine dalla galleria
        pickImageLauncher = activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                onImageSelected(uri)
            } else {
                onError("Nessuna immagine selezionata")
            }
        }
    }

    /**
     * Avvia la camera per scattare una foto.
     */
    fun takePicture() {
        try {
            imageUri = createImageUri()
            // Fix: usa una variabile locale per evitare smart cast issues
            val currentUri = imageUri
            if (currentUri != null) {
                takePictureLauncher?.launch(currentUri)
            } else {
                onError("Impossibile creare URI per la foto")
            }
        } catch (e: Exception) {
            onError("Impossibile avviare la camera: ${e.message}")
        }
    }

    /**
     * Avvia il photo picker per selezionare un'immagine.
     */
    fun pickFromGallery() {
        pickImageLauncher?.launch("image/*")
    }

    /**
     * Crea un URI temporaneo per salvare la foto scattata.
     */
    private fun createImageUri(): Uri {
        val imageFile = File(
            context.externalCacheDir,
            "profile_images_temp"
        ).apply {
            mkdirs()
        }

        val file = File(imageFile, "profile_${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    /**
     * Ridimensiona e comprime l'immagine per l'upload.
     */
    suspend fun processImage(uri: Uri, maxWidth: Int = 512, quality: Int = 80): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalBitmap == null) return@withContext null

                // Calcola le nuove dimensioni mantenendo le proporzioni
                val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
                val newWidth = if (originalBitmap.width > originalBitmap.height) {
                    maxWidth
                } else {
                    (maxWidth * ratio).toInt()
                }
                val newHeight = if (originalBitmap.height > originalBitmap.width) {
                    maxWidth
                } else {
                    (maxWidth / ratio).toInt()
                }

                // Ridimensiona
                val resizedBitmap = Bitmap.createScaledBitmap(
                    originalBitmap,
                    newWidth,
                    newHeight,
                    true
                )

                // Comprimi
                val outputStream = java.io.ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                val result = outputStream.toByteArray()

                // Cleanup
                originalBitmap.recycle()
                resizedBitmap.recycle()
                outputStream.close()

                result
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Pulisce i file temporanei dalla cache.
     */
    fun cleanupTempFiles() {
        try {
            val tempDir = File(context.externalCacheDir, "profile_images_temp")
            if (tempDir.exists()) {
                tempDir.listFiles()?.forEach { file ->
                    if (file.name.startsWith("profile_") && file.name.endsWith(".jpg")) {
                        file.delete()
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}

/**
 * Composable helper per creare un ImagePickerManager.
 */
@Composable
fun rememberImagePickerManager(
    onImageSelected: (Uri) -> Unit,
    onError: (String) -> Unit
): ImagePickerManager {
    val context = LocalContext.current
    return remember {
        ImagePickerManager(context, onImageSelected, onError)
    }
}