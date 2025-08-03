package com.example.u_study

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.u_study.data.repositories.AuthRepository
import com.example.u_study.data.repositories.SettingsRepository
import com.example.u_study.data.repositories.UserRepository
import com.example.u_study.ui.screens.favLibraries.FavLibrariesViewModel
import com.example.u_study.ui.screens.home.HomeViewModel
import com.example.u_study.ui.screens.libraries.LibrariesViewModel
import com.example.u_study.ui.screens.login.LoginViewModel
import com.example.u_study.ui.screens.modifyUser.ModifyUserViewModel
import com.example.u_study.ui.screens.profile.ProfileViewModel
import com.example.u_study.ui.screens.register.RegisterViewModel
import com.example.u_study.ui.screens.settings.SettingsViewModel
import com.example.u_study.ui.screens.stats.StatsViewModel
import com.example.u_study.ui.screens.todo.TodoViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

//val Context.dataStore by preferencesDataStore("theme")
val Context.dataStoreLang by preferencesDataStore("language")

val appModule = module {
    //single { get<Context>().dataStore }

    single { get<Context>().dataStoreLang }

    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single { get<SupabaseClient>().auth }


    single { AuthRepository(get()) }
    single { UserRepository(get()) }
    single { SettingsRepository(get()) }

    viewModel { SettingsViewModel(get<SettingsRepository>(), get<AuthRepository>()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { FavLibrariesViewModel() }
    viewModel { LibrariesViewModel() }
    viewModel { ModifyUserViewModel() }
    viewModel { ProfileViewModel(get<AuthRepository>(), get<UserRepository>()) }
    viewModel { StatsViewModel() }
    viewModel { TodoViewModel() }
    viewModel { HomeViewModel(get<AuthRepository>(), get<UserRepository>()) }

}
