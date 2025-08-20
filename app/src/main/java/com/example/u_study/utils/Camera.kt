package com.example.u_study.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.delay

/**
 * Utility composable per la gestione della foto profilo tramite camera.
 * Incapsula la logica di file temporaneo, FileProvider e retry asincrono.
 *
 * @param onPhotoReady callback da chiamare con l'Uri della foto scattata (pronta per essere letta)
 * @return funzione launchCamera()
 */
@Composable
fun rememberProfileCameraLauncher(
    onPhotoReady: (Uri) -> Unit,
    onError: ((String) -> Unit)? = null
): () -> Unit {
    val context = LocalContext.current

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        photoUri?.let { uri ->
            if (success) {
                pendingCameraUri = uri
            } else {
                onError?.invoke("Errore: la foto non è stata salvata correttamente. Riprova.")
            }
        }
    }

    // Retry asincrono per assicurarsi che il file sia pronto
    LaunchedEffect(pendingCameraUri) {
        pendingCameraUri?.let { uri ->
            var retries = 5
            var found = false
            while (retries > 0 && !found) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        inputStream.close()
                        found = true
                    } else {
                        delay(200)
                        retries--
                    }
                } catch (e: Exception) {
                    delay(200)
                    retries--
                }
            }
            if (found) {
                onPhotoReady(uri)
            } else {
                onError?.invoke("Errore: la foto non è stata salvata correttamente. Riprova.")
            }
            pendingCameraUri = null
        }
    }

    val launchCamera = {
        val imageFile = File.createTempFile("tmp_image", ".jpg", context.externalCacheDir)
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
        photoUri = uri
        cameraLauncher.launch(uri)
    }

    return launchCamera
}