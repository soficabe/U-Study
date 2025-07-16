package com.example.u_study

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.u_study.data.repositories.SettingsRepository
import com.example.u_study.ui.screens.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")
val Context.dataStoreLang by preferencesDataStore("language")

val appModule = module {
    single { get<Context>().dataStore }

    single { get<Context>().dataStoreLang }

    single { SettingsRepository(get()) }

    viewModel { SettingsViewModel(get()) }
}