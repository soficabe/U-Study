package com.example.u_study

import android.app.Application
import android.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.osmdroid.config.Configuration

/**
 * Application class principale dell'app.
 *
 * Si occupa dell'inizializzazione globale dell'applicazione,
 * in particolare della configurazione del sistema di Dependency Injection (Koin).
 *
 * Questa classe viene istanziata automaticamente da Android all'avvio dell'app
 * e rimane in vita per tutta la durata dell'applicazione.
 */
class UStudyApplication : Application() {
    /**
     * Metodo chiamato alla creazione dell'applicazione.
     *
     * Configura e avvia il framework Koin per la dependency injection,
     * fornendo il contesto Android e caricando i moduli di dipendenze.
     */
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID


        // Inizializzazione del framework Koin per la dependency injection
        startKoin {
            // Abilita il logging di Koin per debug e troubleshooting
            androidLogger()

            // Fornisce il contesto Android a Koin per l'accesso alle risorse di sistema
            androidContext(this@UStudyApplication)

            // Carica il modulo principale contenente tutte le definizioni delle dipendenze
            modules(appModule)
        }
    }
}