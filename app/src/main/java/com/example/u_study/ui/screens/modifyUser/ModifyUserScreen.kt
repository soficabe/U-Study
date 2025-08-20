package com.example.u_study.ui.screens.modifyUser

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.ImagePickerDialog
import com.example.u_study.ui.composables.NavigationBar
import com.example.u_study.ui.composables.ProfileIcon
import com.example.u_study.ui.composables.SaveButton
import java.io.File
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay

@Composable
fun ModifyUserScreen(
    state: ModifyUserState,
    actions: ModifyUserActions,
    navController: NavHostController
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Launcher per la galleria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            actions.onProfileImageSelected(uri, context)
        }
    }

    // Stato per gestire la photoUri e il retry asincrono camera
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        photoUri?.let { uri ->
            if (success) {
                // Invece di LaunchedEffect qui, aggiorna lo stato:
                pendingCameraUri = uri
            }
        }
    }

    // Effetto di retry su pendingCameraUri
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
                actions.onProfileImageSelected(uri, context)
            } else {
                actions.onImageError("Errore: la foto non è stata salvata correttamente. Riprova.")
            }
            // Reset
            pendingCameraUri = null
        }
    }

    // Gestione dei messaggi di errore e successo
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            actions.clearMessages()
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            snackbarHostState.showSnackbar("Profilo aggiornato con successo!")
            actions.clearMessages()
            navController.navigate(UStudyRoute.ProfileScreen) {
                popUpTo(UStudyRoute.ModifyUserScreen) { inclusive = true }
            }
        }
    }

    // Dialog per selezione immagine
    if (state.showImagePicker) {
        ImagePickerDialog(
            onDismiss = actions::hideImagePicker,
            onCameraClick = {
                // Crea file solo al click camera
                val imageFile = File.createTempFile("tmp_image", ".jpg", context.externalCacheDir)
                val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", imageFile)
                photoUri = uri
                cameraLauncher.launch(uri)
            },
            onGalleryClick = {
                galleryLauncher.launch("image/*")
            }
        )
    }

    Scaffold(
        topBar = { AppBar(title = stringResource(R.string.editProfileScreen_name), navController) },
        bottomBar = { NavigationBar(navController = navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(100.dp))
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Caricamento profilo...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(24.dp))

                Box(contentAlignment = Alignment.Center) {
                    ProfileIcon(
                        imageUrl = state.imageUrl,
                        isClickable = true,
                        onClick = actions::showImagePicker
                    )

                    if (state.isUploadingImage) {
                        Box(
                            modifier = Modifier.matchParentSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.tap_to_change_profile_image),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        stringResource(R.string.firstName),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.firstName,
                        onValueChange = actions::setFirstName,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving && !state.isUploadingImage,
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        stringResource(R.string.lastName),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.lastName,
                        onValueChange = actions::setLastName,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving && !state.isUploadingImage,
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        stringResource(R.string.email),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = actions::setEmail,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isSaving && !state.isUploadingImage,
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(32.dp))

                if (state.isSaving) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Salvando...",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (state.isUploadingImage) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Caricamento immagine...",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    SaveButton(
                        text = stringResource(R.string.saveChanges_button),
                        enabled = state.hasChanges && !state.isSaving && !state.isUploadingImage,
                        onClick = actions::saveChanges
                    )

                    if (!state.hasChanges) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Nessuna modifica da salvare",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}