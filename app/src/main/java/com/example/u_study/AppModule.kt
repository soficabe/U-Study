package com.example.u_study

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.u_study.data.repositories.SettingsRepository
import com.example.u_study.ui.screens.settings.SettingsViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")
val Context.dataStoreLang by preferencesDataStore("language")

val appModule = module {
    single { get<Context>().dataStore }

    single { get<Context>().dataStoreLang }

    single {
        createSupabaseClient(
            supabaseUrl = "https://bvtcaudkknuxsfkmaqtm.supabase.co/",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJ2dGNhdWRra251eHNma21hcXRtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTI1MDExMjAsImV4cCI6MjA2ODA3NzEyMH0.tbcWeir-yj6m0MxMlJ4cPTU6mfDJfgB_PazwFelzjQ8"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single { SettingsRepository(get()) }

    viewModel { SettingsViewModel(get()) }
}
