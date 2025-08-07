package com.example.u_study.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.u_study.ui.screens.FavLibrariesScreen
import com.example.u_study.ui.screens.home.HomeScreen
import com.example.u_study.ui.screens.LibrariesScreen
import com.example.u_study.ui.screens.login.LoginScreen
import com.example.u_study.ui.screens.login.LoginViewModel
import com.example.u_study.ui.screens.modifyUser.ModifyUserScreen
import com.example.u_study.ui.screens.register.RegisterScreen
import com.example.u_study.ui.screens.settings.SettingsScreen
import com.example.u_study.ui.screens.stats.StatsScreen
import com.example.u_study.ui.screens.ToDoScreen
import com.example.u_study.ui.screens.favLibraries.FavLibrariesViewModel
import com.example.u_study.ui.screens.home.HomeViewModel
import com.example.u_study.ui.screens.libraries.LibrariesViewModel
import com.example.u_study.ui.screens.modifyUser.ModifyUserViewModel
import com.example.u_study.ui.screens.profile.ProfileScreen
import com.example.u_study.ui.screens.profile.ProfileViewModel
import com.example.u_study.ui.screens.register.RegisterViewModel
import com.example.u_study.ui.screens.settings.SettingsState
import com.example.u_study.ui.screens.settings.SettingsViewModel
import com.example.u_study.ui.screens.stats.StatsViewModel
import com.example.u_study.ui.screens.todo.TodoViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface UStudyRoute {
    @Serializable
    data object LoginScreen : UStudyRoute
    @Serializable
    data object RegisterScreen : UStudyRoute
    @Serializable
    data object HomeScreen : UStudyRoute
    @Serializable
    data object ProfileScreen : UStudyRoute
    @Serializable
    data object ModifyUserScreen : UStudyRoute
    @Serializable
    data object StatsScreen : UStudyRoute
    @Serializable
    data object SettingsScreen : UStudyRoute
    @Serializable
    data object ToDoScreen : UStudyRoute
    @Serializable
    data object LibrariesScreen : UStudyRoute
    @Serializable
    data object FavLibrariesScreen : UStudyRoute
}

@Composable
fun UStudyNavGraph(
    settingsViewModel: SettingsViewModel,
    settingsState: SettingsState,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = UStudyRoute.HomeScreen,
        modifier = modifier
    ) {
        composable<UStudyRoute.LoginScreen> {
            val loginViewModel = koinViewModel<LoginViewModel>()
            val loginState by loginViewModel.state.collectAsStateWithLifecycle()
            LoginScreen(loginState, loginViewModel.actions, navController)
        }

        composable<UStudyRoute.RegisterScreen> {
            val registerViewModel = koinViewModel<RegisterViewModel>()
            val registerState by registerViewModel.state.collectAsStateWithLifecycle()
            RegisterScreen(registerState, registerViewModel.actions, navController)
        }

        composable<UStudyRoute.HomeScreen> {
            val homeViewModel = koinViewModel<HomeViewModel>()
            val homeState by homeViewModel.state.collectAsStateWithLifecycle()
            HomeScreen(homeState, homeViewModel.actions, navController)
        }

        composable<UStudyRoute.ProfileScreen> {
            val profileViewModel = koinViewModel<ProfileViewModel>()
            val profileState by profileViewModel.state.collectAsStateWithLifecycle()
            ProfileScreen(profileState, navController)
        }

        composable<UStudyRoute.ModifyUserScreen> {
            val modifyUserViewModel = koinViewModel<ModifyUserViewModel>()
            val modifyUserState by modifyUserViewModel.state.collectAsStateWithLifecycle()
            ModifyUserScreen(modifyUserState, modifyUserViewModel.actions, navController)
        }

        composable<UStudyRoute.StatsScreen> {
            val statsViewModel = koinViewModel<StatsViewModel>()
            val statsState by statsViewModel.state.collectAsStateWithLifecycle()
            StatsScreen(statsState, navController)
        }

        composable<UStudyRoute.SettingsScreen> {
            SettingsScreen(settingsState, settingsViewModel.actions, navController)
        }

        composable<UStudyRoute.ToDoScreen> {
            val todoViewModel = koinViewModel<TodoViewModel>()
            val todoState by todoViewModel.state.collectAsStateWithLifecycle()
            ToDoScreen(todoState, todoViewModel.actions, navController)
        }

        composable<UStudyRoute.LibrariesScreen> {
            val librariesViewModel = koinViewModel<LibrariesViewModel>()
            val librariesState by librariesViewModel.state.collectAsStateWithLifecycle()
            LibrariesScreen(librariesState, navController)
        }
        composable<UStudyRoute.FavLibrariesScreen> {
            val favLibrariesViewModel = koinViewModel<FavLibrariesViewModel>()
            val favLibrariesState by favLibrariesViewModel.state.collectAsStateWithLifecycle()
            FavLibrariesScreen(favLibrariesState, navController)
        }
    }
}
