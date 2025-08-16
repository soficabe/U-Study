package com.example.u_study.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.data.repositories.LoginResult
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.Logo
import com.example.u_study.ui.composables.SaveButton

/**
 * Schermata di login dell'applicazione.
 *
 * Implementa il pattern State Hoisting: riceve state e actions come parametri
 * invece di creare direttamente il ViewModel. Questo migliora testabilità
 * e separazione delle responsabilità.
 *
 * @param state stato corrente della schermata di login
 * @param actions interfaccia delle azioni disponibili per l'utente
 * @param navController controller per la navigazione tra schermate
 */
@Composable
fun LoginScreen(
    state: LoginState,
    actions: LoginActions,
    navController: NavHostController
) {
    // State locale per lo scrolling della schermata
    val scrollState = rememberScrollState()

    // State locale per la visibilità della password
    // rememberSaveable preserva il valore durante configuration changes
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Effetto che reagisce ai cambiamenti del risultato del login
    // Gestisce la navigazione basata sull'esito dell'autenticazione
    LaunchedEffect(state.loginResult) {
        when (state.loginResult) {
            LoginResult.Start -> {
                // Stato iniziale - nessuna azione richiesta
            }
            LoginResult.Success -> {
                // Naviga alla home e rimuove login dallo stack
                // popUpTo con inclusive = true previene il ritorno al login con back button
                navController.navigate(UStudyRoute.HomeScreen) {
                    popUpTo(UStudyRoute.LoginScreen) { inclusive = true }
                }
            }
            LoginResult.InvalidCredentials -> {
                // Errore gestito tramite state.errorMessageLog nella UI
            }
            LoginResult.Error -> {
                // Errore generico gestito tramite state.errorMessageLog
            }
        }
    }

    Surface (modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(scrollState), // Scrolling per gestire tastiera virtuale
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo dell'app
            Logo()

            Spacer(Modifier.height(32.dp))

            // Titolo della schermata
            Text(
                stringResource(R.string.loginText),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(32.dp))

            // Campo email
            OutlinedTextField(
                value = state.email,
                onValueChange = actions::setEmail,
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // Campo password con toggle visibilità
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    // Icona toggle per visibilità password
                    val image = if (passwordVisible)
                        Icons.Outlined.Visibility
                    else
                        Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible)
                                "Hide password"
                            else
                                "Show password"
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Visualizzazione errori
            // Usa state.errorMessageLog che contiene l'ID della risorsa string
            if (state.errorMessageLog != null) {
                Text(
                    text = stringResource(id = state.errorMessageLog),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Bottone login principale
            SaveButton(
                text = if (state.isLoggingIn)
                    stringResource(R.string.signing_in)
                else
                    stringResource(R.string.signIn_button),
                enabled = !state.isLoggingIn,
                onClick = { actions.login() }
            )

            // Link per navigare alla registrazione
            TextButton(
                onClick = { navController.navigate(UStudyRoute.RegisterScreen) }
            ) {
                Text(stringResource(R.string.dontHaveAccount_text))
            }

            // Separatore visivo "Or"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
            ) {
                // Linea sinistra
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )

                Text(
                    text = stringResource(R.string.or_separator),
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                // Linea destra
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }

            // Bottone Google Sign In
            GoogleSignInButton (
                onClick = { actions.loginWithGoogle() }
            )

            // Opzione per accedere senza login (guest mode)
            TextButton(
                onClick = { navController.navigate(UStudyRoute.HomeScreen) },
                modifier = Modifier.padding(vertical = 8.dp)
                ) {
                Text(stringResource(R.string.withoutLogging_text))
            }

        }
    }
}

/**
 * Componente riutilizzabile per il bottone di login con Google.
 *
 * Implementa il design standard Google con icona e testo.
 * Separato in componente dedicato per riutilizzabilità e testing.
 *
 * @param onClick callback invocato quando l'utente preme il bottone
 */
@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icona Google
        Image(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Sign In With Google",
            color = Color.DarkGray,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
