package com.example.u_study.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

/**
 * Utility composable per la gestione della foto profilo tramite galleria.
 * Incapsula la logica di GetContent.
 *
 * @param onImageSelected callback da chiamare con l'Uri dell'immagine selezionata
 * @return funzione launchGallery()
 */
@Composable
fun rememberProfileGalleryLauncher(
    onImageSelected: (Uri) -> Unit
): () -> Unit {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    val launchGallery = {
        galleryLauncher.launch("image/*")
    }

    return launchGallery
}