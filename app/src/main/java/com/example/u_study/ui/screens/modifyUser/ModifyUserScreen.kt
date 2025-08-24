package com.example.u_study.ui.screens.modifyUser

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
import androidx.compose.runtime.remember
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
import com.example.u_study.utils.rememberProfileCameraLauncher
import com.example.u_study.utils.rememberProfileGalleryLauncher

@Composable
fun ModifyUserScreen(
    state: ModifyUserState,
    actions: ModifyUserActions,
    navController: NavHostController
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val profileUpdateSuccessText = stringResource(R.string.profile_update_success)


    // --- NUOVA GESTIONE CAMERA E GALLERIA CENTRALIZZATA ---
    val launchCamera = rememberProfileCameraLauncher(
        onPhotoReady = { uri -> actions.onProfileImageSelected(uri, context) },
        onError = actions::onImageError
    )
    val launchGallery = rememberProfileGalleryLauncher { uri ->
        actions.onProfileImageSelected(uri, context)
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
            snackbarHostState.showSnackbar(profileUpdateSuccessText)
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
            onCameraClick = launchCamera,
            onGalleryClick = launchGallery
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
                Text(stringResource(R.string.profile_loading), style = MaterialTheme.typography.bodyMedium)
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
                            stringResource(R.string.saving_text),
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
                            stringResource(R.string.image_loading),
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
                            stringResource(R.string.no_changes_to_be_saved),
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