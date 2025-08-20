package com.example.u_study

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.ImageRepository
import com.example.u_study.data.repositories.LibraryRepository
import com.example.u_study.data.repositories.SettingsRepository
import com.example.u_study.data.repositories.ToDoRepository
import com.example.u_study.data.repositories.UserRepository
import com.example.u_study.ui.screens.favLibraries.FavLibrariesViewModel
import com.example.u_study.ui.screens.home.HomeViewModel
import com.example.u_study.ui.screens.libraries.LibrariesViewModel
import com.example.u_study.ui.screens.libraryDetail.LibraryDetailViewModel
import com.example.u_study.ui.screens.login.LoginViewModel
import com.example.u_study.ui.screens.modifyUser.ModifyUserViewModel
import com.example.u_study.ui.screens.profile.ProfileViewModel
import com.example.u_study.ui.screens.register.RegisterViewModel
import com.example.u_study.ui.screens.settings.SettingsViewModel
import com.example.u_study.ui.screens.stats.StatsViewModel
import com.example.u_study.ui.screens.todo.TodoViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * Extension property per creare un DataStore per le preferenze dell'app.
 *
 * Utilizza il pattern delegate per creare lazy un DataStore associato al Context.
 * Il DataStore viene utilizzato per persistere le impostazioni utente in modo asincrono.
 */
val Context.dataStore by preferencesDataStore("preferences")

/**
 * Modulo principale di Koin contenente tutte le definizioni delle dipendenze dell'applicazione.
 *
 * Organizzazione delle dipendenze:
 * 1. Infrastructure Layer (DataStore, Supabase Client)
 * 2. Data Layer (Repository implementations)
 * 3. Presentation Layer (ViewModels)
 *
 * Utilizza il pattern Singleton per condividere istanze globali
 * e il pattern Factory per ViewModels (lifecycle-aware).
 */
val appModule = module {

    // ===== INFRASTRUCTURE LAYER =====

    /**
     * Fornisce il DataStore per la persistenza delle preferenze locali.
     * Singleton: una sola istanza per tutta l'app.
     */
    single { get<Context>().dataStore }

    /**
     * Client principale Supabase configurato con tutti i moduli necessari.
     *
     * Configurazione:
     * - Postgrest: per operazioni CRUD sul database
     * - Auth: autenticazione con flow PKCE per sicurezza OAuth
     * - ComposeAuth: integrazione auth con Jetpack Compose
     * - Storage: gestione file e media
     */
    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)

            install(Auth) {
                flowType = FlowType.PKCE  // Proof Key for Code Exchange - più sicuro
                scheme = "app"            // Deep link scheme per redirect
                host = "supabase.com"     // Host per callback OAuth
            }

            install(ComposeAuth)

            install(Storage)
        }
    }

    // Componenti Supabase estratti dal client principale
    single { get<SupabaseClient>().auth }
    single { get<SupabaseClient>().composeAuth }
    single { get<SupabaseClient>().postgrest }
    single { get<SupabaseClient>().storage }


    // ===== DATA LAYER - REPOSITORIES =====

    /**
     * Repository per gestione autenticazione e sessioni utente.
     * Dipende da: Auth client
     */
    single { AuthRepository(get()) }

    /**
     * Repository per operazioni sui dati utente.
     * Dipende da: Postgrest client
     */
    single { UserRepository(get()) }

    /**
     * Repository per gestione impostazioni applicazione.
     * Dipende da: DataStore per persistenza locale
     */
    single { SettingsRepository(get()) }

    /**
     * Repository per gestione todo.
     * Dipende da: Postgrest client
     */
    single { ToDoRepository(get()) }

    /**
     * Repository per gestione librerie e contenuti di studio.
     * Dipende da: Postgrest client
     */
    single { LibraryRepository(get(), get()) }

    /**
     * Repository per gestione immagini su Supabase Storage.
     * Dipende da: Storage client
     */
    single { ImageRepository(get()) }

    // ===== PRESENTATION LAYER - VIEWMODELS =====

    /**
     * ViewModels configurati come factory per supportare il lifecycle di Compose.
     * Ogni ViewModel riceve le dipendenze necessarie dal container Koin.
     */

    // Settings - gestione preferenze utente
    viewModel { SettingsViewModel(get<SettingsRepository>(), get<AuthRepository>()) }

    // Authentication flow
    viewModel { RegisterViewModel(get()) }
    viewModel { LoginViewModel(get()) }

    // Library management
    viewModel { FavLibrariesViewModel(get()) }
    viewModel { LibrariesViewModel(get(), get()) }
    viewModel { LibraryDetailViewModel(get(), get(), get()) }

    // User management
    viewModel { ModifyUserViewModel( get<AuthRepository>(), get<UserRepository>(), get<ImageRepository>()) }
    viewModel { ProfileViewModel(get<AuthRepository>(), get<UserRepository>()) }

    // Core app features
    viewModel { HomeViewModel(get<AuthRepository>(), get<UserRepository>()) }
    viewModel { TodoViewModel(get()) }
    viewModel { StatsViewModel() } // No dependencies - local computation only
}