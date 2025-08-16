package com.example.u_study

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.u_study.data.models.Theme
import com.example.u_study.ui.UStudyNavGraph
import com.example.u_study.ui.screens.settings.SettingsViewModel
import com.example.u_study.ui.theme.U_StudyTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.android.ext.android.inject
import androidx.lifecycle.lifecycleScope
import com.example.u_study.data.models.Language
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.Auth
import java.util.Locale

/**
 * Activity principale dell'app.
 *
 * Responsabilità principali:
 * 1. Inizializzazione del tema e della lingua dell'app
 * 2. Gestione dei deep link per l'autenticazione OAuth con Supabase
 * 3. Configurazione dell'interfaccia utente con Jetpack Compose
 * 4. Setup della navigazione principale dell'app
 *
 * Integrazione con Supabase:
 * - Gestisce i callback OAuth tramite deep link (schema: app://supabase.com)
 * - Utilizza PKCE (Proof Key for Code Exchange) per sicurezza OAuth
 *
 * Pattern utilizzati:
 * - Dependency Injection con Koin
 * - MVVM con ViewModels
 * - Reactive UI con Compose e StateFlow
 */
class MainActivity : ComponentActivity() {
    // ===== DEPENDENCY INJECTION =====

    /**
     * Client di autenticazione Supabase iniettato tramite Koin.
     * Utilizzato per gestire il flusso OAuth e l'exchange dei codici di autorizzazione.
     */
    private val auth: Auth by inject()

    // ===== LIFECYCLE METHODS =====

    /**
     * Inizializzazione dell'Activity.
     *
     * Flow di esecuzione:
     * 1. Gestisce eventuali deep link dall'intent iniziale (OAuth callback)
     * 2. Abilita edge-to-edge display per UI moderna
     * 3. Configura l'interfaccia Compose con tema e lingua dinamici
     * 4. Inizializza il sistema di navigazione
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gestisce il deep link OAuth se presente nell'intent di apertura
        lifecycleScope.launch {
            handleDeepLink(intent)
        }

        // Abilita layout edge-to-edge per esperienza utente moderna
        enableEdgeToEdge()

        // ===== UI COMPOSITION =====

        /**
         * Configurazione dell'interfaccia utente Compose
         *
         * Responsabilità:
         * 1. Osservazione dello stato delle impostazioni (tema, lingua)
         * 2. Applicazione del tema dinamico basato sulle preferenze utente
         * 3. Configurazione del contesto linguistico
         * 4. Inizializzazione della navigazione
         */
        setContent {
            // ===== STATE MANAGEMENT =====
            val settingsViewModel = koinViewModel<SettingsViewModel>()
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

            LanguageProvider(language = settingsState.lang) {
                U_StudyTheme(
                    // Determina se utilizzare il tema scuro basato sulle impostazioni utente.
                    darkTheme = when (settingsState.theme) {
                        Theme.Light -> false
                        Theme.Dark -> true
                        Theme.System -> isSystemInDarkTheme()
                    }
                ) {
                    // ===== NAVIGATION SETUP =====
                    val navController = rememberNavController()
                    UStudyNavGraph(
                        settingsViewModel,
                        settingsState,
                        navController
                    )
                }
            }
        }
    }

    /**
     * Gestisce i nuovi intent quando l'Activity è già in esecuzione.
     *
     * Scenario tipico: l'utente torna dall'autenticazione OAuth esterna
     * e l'app riceve un deep link di callback.
     *
     * @param intent Il nuovo intent ricevuto
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Gestisce il deep link OAuth per callback quando l'app è già attiva
        lifecycleScope.launch {
            handleDeepLink(intent)
        }
    }

    // ===== OAUTH DEEP LINK HANDLING =====

    /**
     * Gestisce i deep link OAuth per l'autenticazione con Supabase.
     *
     * Flow OAuth:
     * 1. L'utente viene reindirizzato al provider OAuth esterno
     * 2. Dopo l'autenticazione, viene reindirizzato a: app://supabase.com?code=XXXX
     * 3. Questo metodo estrae il codice e lo scambia con un token di sessione
     *
     * @param intent L'intent contenente il deep link (può essere null)
     *
     * Formato deep link atteso: app://supabase.com?code=<authorization_code>
     */
    private suspend fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            // Verifica che sia un deep link OAuth valido
            if (uri.scheme == "app" && uri.host == "supabase.com") {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    try {
                        // Exchange del codice di autorizzazione con una sessione valida
                        auth.exchangeCodeForSession(code)
                        Log.d("MainActivity", "OAuth login successful")
                    } catch (e: Exception) {
                        // Log dell'errore senza crash dell'app
                        Log.e("MainActivity", "OAuth callback failed", e)
                    }
                }
            }
        }
    }

}

// ===== LANGUAGE PROVIDER COMPOSABLE =====

/**
 * Composable che fornisce il contesto linguistico a tutti i componenti figli.
 *
 * Utilizza il pattern Provider per propagare le impostazioni di localizzazione
 * attraverso l'albero dei Composable, permettendo la traduzione dinamica
 * dell'interfaccia utente.
 *
 * @param language La lingua selezionata dall'utente
 * @param content Il contenuto Composable che riceverà il contesto localizzato
 *
 * Implementazione:
 * 1. Crea un Locale dalla lingua selezionata
 * 2. Aggiorna la configurazione del sistema
 * 3. Crea un nuovo contesto con la configurazione linguistica
 * 4. Fornisce il contesto ai Composable figli tramite CompositionLocalProvider
 */
@Composable
fun LanguageProvider(
    language: Language,
    content: @Composable () -> Unit
) {
    // Creazione del Locale dalla lingua selezionata
    val locale = Locale(language.code)

    // Ottenimento della configurazione corrente del sistema
    val configuration = LocalConfiguration.current

    // Aggiornamento della configurazione con il nuovo Locale
    configuration.setLocale(locale)

    // Creazione di un nuovo contesto con la configurazione linguistica
    val context = LocalContext.current.createConfigurationContext(configuration)

    // Fornisce il contesto localizzato con la lingua corretta a tutti i Composable figli
    // Questo permetterà la traduzione automatica di stringhe e risorse
    CompositionLocalProvider(LocalContext provides context) {
        content()
    }
}