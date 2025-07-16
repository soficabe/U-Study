package com.example.u_study.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.data.models.Theme
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.NavigationBar


/* Da migliorare e completare. Ci sono due tipi di "voci" disponibili:
 * - quelli con lo Switch (SettingsWithSwitch)
 * - quelli completamente cliccabili (SettingsClickable). Qui si può anche
 *   cambiare il colore del testo e dell'icona passando per input
 * Entrambi hanno le icone con un modifier particolare (ma sempre uguale) per
 * avere sfondo grigio di una certa dimensione
 *
 * In più, ho creato anche il Composable TextTitle per non dover tutte le volte
 * assegnare il padding e il bold al testo
 * FINEEEEEEEE poi quando sceglieremo bene cosa mettere, avremo i composable pronti
 */
@Composable
fun SettingsScreen (state: SettingsState, actions: SettingsActions, navController: NavHostController) {
    val scrollState = rememberScrollState()

    var pushNotifications by rememberSaveable { mutableStateOf(true) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(state.theme) }
    val themeOptions = Theme.entries

    Scaffold (
        topBar = { AppBar(stringResource(R.string.settingsScreen_name), navController) },
        bottomBar = { NavigationBar(navController = navController) }
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(scrollState)
        ) {

            TextTitle(stringResource(R.string.loginAlerts))
            HorizontalDivider()
            SettingsWithSwitch(stringResource(R.string.sendPushNotifications), Icons.Filled.Notifications,
                pushNotifications, onCheckedChange = { pushNotifications = it } )
            HorizontalDivider()
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable { showThemeDialog = true }
                .padding(16.dp)) {
                Text(
                    text = stringResource(R.string.changeTheme),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = Bold
                )
            }
            HorizontalDivider()
            SettingsClickable(stringResource(R.string.logout), Icons.AutoMirrored.Filled.Logout, Color.Red, onClick = {})
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