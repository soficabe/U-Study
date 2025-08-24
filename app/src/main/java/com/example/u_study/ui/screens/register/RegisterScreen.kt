package com.example.u_study.ui.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.data.repositories.RegisterResult
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.Logo
import com.example.u_study.ui.composables.SaveButton

/**
 * Schermata di registrazione per nuovi utenti.
 *
 * Implementa un form completo per la creazione account con:
 * - Campi per dati personali (nome, cognome, email)
 * - Campi password con conferma
 * - Checkbox per accettazione termini
 * - Validazione e gestione errori
 *
 * Segue il pattern State Hoisting per mantenere separazione tra UI e logica.
 *
 * @param state stato corrente del form di registrazione
 * @param actions interfaccia delle azioni disponibili per l'utente
 * @param navController controller per navigazione tra schermate
 */
@Composable
fun RegisterScreen(
    state: RegisterState,
    actions: RegisterActions,
    navController: NavHostController
) {
    // State locale per scrolling
    val scrollState = rememberScrollState()

    // State locali per visibilità password
    // Mantenuti separatamente per controllo indipendente
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // Effetto per gestire navigazione post-registrazione
    LaunchedEffect(state.registerResult) {
        when (state.registerResult) {
            RegisterResult.Start -> {
                // Stato iniziale - nessuna azione richiesta
            }
            RegisterResult.Success -> {
                // Navigazione alla home con rimozione dello stack di registrazione
                // Previene il ritorno alla registrazione con back button
                navController.navigate(UStudyRoute.HomeScreen) {
                        popUpTo(UStudyRoute.RegisterScreen) { inclusive = true }
                    }
                }
            RegisterResult.UserExisting -> {
                // Errore gestito tramite state.errorMessage nella UI
            }
            RegisterResult.Error -> {
                // Errore generico gestito tramite state.errorMessage
            }
        }
    }

    Surface (modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo dell'applicazione
            Logo()

            Spacer(Modifier.height(32.dp))

            // Form di registrazione raggruppato in Column separata
            Column(modifier = Modifier.padding(16.dp)) {
                // Campo Nome
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = actions::setFirstName,
                    label = { Text(stringResource(R.string.firstName)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Campo Cognome
                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = actions::setLastName,
                    label = { Text(stringResource(R.string.lastName)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Campo Email
                OutlinedTextField(
                    value = state.email,
                    onValueChange = actions::setEmail,
                    label = { Text(stringResource(R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // Campo Password con toggle visibilità
                OutlinedTextField(
                    value = state.password,
                    onValueChange = actions::setPassword,
                    label = { Text(stringResource(R.string.password)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    // Visual transformation per nascondere/mostrare password
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        // Toggle per visibilità password
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                "Change password visibility"
                            )
                        }
                    }
                )

                Spacer(Modifier.height(8.dp))

                // Campo Conferma Password con toggle visibilità separato
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = actions::setConfirmPassword,
                    label = { Text(stringResource(R.string.confirmPassword)) },
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(Modifier.height(16.dp))

                Spacer(Modifier.height(16.dp))

                // Visualizzazione messaggi di errore
                // Utilizza state.errorMessage che contiene l'ID della risorsa string
                if (state.errorMessage != null) {
                    Text(
                        text = stringResource(id = state.errorMessage),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Bottone per eseguire registrazione
                // Collega l'azione register() del ViewModel
                SaveButton(
                    text = stringResource(R.string.signUp_button),
                    onClick = { actions.register() })
            }

            // Link per navigare al login se l'utente ha già un account
            TextButton(onClick = { navController.navigate(UStudyRoute.LoginScreen) }) {
                Text(stringResource(R.string.haveAnAccount_text))
            }
        }
    }

}