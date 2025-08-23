package com.example.u_study.ui.screens.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.data.models.Language
import com.example.u_study.data.models.Theme
import com.example.u_study.data.repositories.UpdatePasswordResult
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.NavigationBar
import kotlinx.coroutines.launch


/** Due tipi di "voci" disponibili:
 * - quelli con lo Switch (SettingsWithSwitch)
 * - quelli completamente cliccabili (SettingsClickable). Qui si può anche
 *   cambiare il colore del testo e dell'icona passando per input
 * Entrambi hanno le icone con un modifier particolare (ma sempre uguale) per
 * avere sfondo grigio di una certa dimensione
 *
 * Composable TextTitle per non dover tutte le volte
 * assegnare il padding e il bold al testo
 *
 */
@Composable
fun SettingsScreen (state: SettingsState, actions: SettingsActions, navController: NavHostController) {
    val scrollState = rememberScrollState()

    //cambio tema (scuro, chiaro, default)
    var showThemeDialog by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(state.theme) }
    val themeOptions = Theme.entries

    //cambio lingua
    var showLangDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //cambio password
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        actions.updatePasswordEvent.collect { result ->
            when (result) {
                is UpdatePasswordResult.Success -> {
                    Toast.makeText(context, R.string.updatePasswordSuccess, Toast.LENGTH_SHORT).show()
                    showPasswordDialog = false
                }
                is UpdatePasswordResult.Error -> {
                    val errorMessage = context.getString(result.messageResId)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold (
        topBar = { AppBar(stringResource(R.string.settingsScreen_name), navController, state.isAuthenticated) },
        bottomBar = { NavigationBar(navController = navController, isAutheticated = state.isAuthenticated) }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(scrollState)
        ) {

            TextTitle(stringResource(R.string.appearance))
            HorizontalDivider()
            SettingsClickable(stringResource(R.string.changeTheme), Icons.Filled.WbSunny, onClick = {showThemeDialog = true})

            HorizontalDivider()

            SettingsClickable(stringResource(R.string.changeLang), Icons.Filled.Language, onClick = {showLangDialog = true})

            if (state.isAuthenticated) {

                TextTitle(stringResource(R.string.privacy))

                HorizontalDivider()
                SettingsClickable(stringResource(R.string.changePassword), Icons.Filled.Lock, onClick = { showPasswordDialog = true })

                HorizontalDivider()
                SettingsClickable(
                    stringResource(R.string.logout),
                    Icons.AutoMirrored.Filled.Logout,
                    Color.Red,
                    onClick = {
                        actions.logout()

                        navController.navigate(UStudyRoute.HomeScreen) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    })
            }

        }
        if(showThemeDialog) {
            ThemeRadioOptionsDialog(
                title = stringResource(R.string.chooseTheme),
                options = themeOptions,
                selectedOption = selectedTheme,
                onOptionSelected = {
                    selectedTheme = it
                    showThemeDialog = false
                    actions.changeTheme(it)
                },
                onDismiss = { showThemeDialog = false })
        }

        if (showLangDialog) {
            LangRadioOptionsDialog(
                title = stringResource(R.string.chooseLang),
                options = Language.entries,
                selectedOption = state.lang,
                onOptionSelected = { selectedLanguage ->
                    showLangDialog = false
                    scope.launch {
                        actions.changeLang(selectedLanguage)
                        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                },
                onDismiss = { showLangDialog = false })
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text(stringResource(R.string.changePassword)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text(stringResource(R.string.newPassword)) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        "Change password visibility"
                                    )
                                }
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(stringResource(R.string.confirmNewPassword)) },
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        "Change confirm password visibility"
                                    )
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { actions.updatePassword(newPassword, confirmPassword) }) {
                        Text(stringResource(R.string.saveString))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPasswordDialog = false }) {
                        Text(stringResource(R.string.closeButton))
                    }
                }
            )
        }
    }
}


@Composable
fun TextTitle (text: String) {
    Text(text = text,
        modifier = Modifier.padding(vertical = 8.dp),
        fontWeight = FontWeight.Bold)
}

@Composable
fun SettingsWithSwitch (title: String, icon: ImageVector, checked : Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title,
            modifier = Modifier
                .size(32.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
                .padding(5.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(text = title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsClickable(title: String, icon: ImageVector, color: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = color,
            modifier = Modifier
                .size(32.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(10.dp))
                .padding(5.dp))
        Spacer(Modifier.width(16.dp))
        Text(title, color = color, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable
fun ThemeRadioOptionsDialog(
    title: String,
    options: List<Theme>,
    selectedOption: Theme,
    onOptionSelected: (Theme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(text = title) }, text = {
        Column {
            options.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOptionSelected(option) }) {
                    RadioButton(
                        selected = option == selectedOption,
                        onClick = { onOptionSelected(option) })
                    Text(text = stringResource(id = option.themeName))
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.closeButton))
        }
    })
}

@Composable
fun LangRadioOptionsDialog(
    title: String,
    options: List<Language>,
    selectedOption: Language,
    onOptionSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(text = title) }, text = {
        Column {
            options.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onOptionSelected(option) }) {
                    RadioButton(
                        selected = option == selectedOption,
                        onClick = { onOptionSelected(option) })
                    Text(text = stringResource(id = option.langName))
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.closeButton))
        }
    })
}