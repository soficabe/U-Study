package com.example.u_study.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.u_study.ui.screens.FavLibrariesScreen
import com.example.u_study.ui.screens.HomeScreen
import com.example.u_study.ui.screens.LibrariesScreen
import com.example.u_study.ui.screens.Login.LoginScreen
import com.example.u_study.ui.screens.Login.LoginViewModel
import com.example.u_study.ui.screens.ModifyUserScreen
import com.example.u_study.ui.screens.ProfileScreen
import com.example.u_study.ui.screens.RegisterScreen
import com.example.u_study.ui.screens.SettingsScreen
import com.example.u_study.ui.screens.stats.StatsScreen
import com.example.u_study.ui.screens.ToDoScreen
import kotlinx.serialization.Serializable

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
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = UStudyRoute.HomeScreen,
        modifier = modifier
    ) {
        composable<UStudyRoute.LoginScreen> {
            val loginViewModel = viewModel<LoginViewModel>()
            val loginState by loginViewModel.state.collectAsStateWithLifecycle()
            LoginScreen(loginState, loginViewModel.actions, navController)
        }

        composable<UStudyRoute.RegisterScreen> {
            RegisterScreen(navController)
        }

        composable<UStudyRoute.HomeScreen> {
            HomeScreen(navController)
        }

        composable<UStudyRoute.ProfileScreen> {
            ProfileScreen(navController)
        }

        composable<UStudyRoute.ModifyUserScreen> {
            ModifyUserScreen(navController)
        }

        composable<UStudyRoute.StatsScreen> {
            StatsScreen(navController)
        }

        composable<UStudyRoute.SettingsScreen> {
            SettingsScreen(navController)
        }

        composable<UStudyRoute.ToDoScreen> {
            ToDoScreen(navController)
        }

        composable<UStudyRoute.LibrariesScreen> {
            LibrariesScreen(navController)
        }
        composable<UStudyRoute.FavLibrariesScreen> {
            FavLibrariesScreen(navController)
        }
    }
}
