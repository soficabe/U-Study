package com.example.u_study.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.UStudyRoute

/** è la NavigationBar (nella parte bassa dello schermo)
 *
 */
@Composable
fun NavigationBar(modifier: Modifier = Modifier, navController: NavHostController, isAutheticated: Boolean = true) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text(stringResource(R.string.home_nav)) },
            selected = false,
            onClick = { navController.navigate(UStudyRoute.HomeScreen) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.School, contentDescription = "Exams") },
            label = { Text(stringResource(R.string.examsScreen_name)) },
            selected = false,
            onClick = {
                if (isAutheticated)
                    navController.navigate(UStudyRoute.ExamsScreen)
                else
                    navController.navigate(UStudyRoute.LoginScreen)
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.CheckBox, contentDescription = "TO-DO List") },
            label = { Text(stringResource(R.string.toDo_nav)) },
            selected = false,
            onClick = {
                if(isAutheticated)
                    navController.navigate(UStudyRoute.ToDoScreen)
                else
                    navController.navigate(UStudyRoute.LoginScreen)
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Book, contentDescription = "Libraries List") },
            label = { Text(stringResource(R.string.librariesList_nav)) },
            selected = false,
            onClick = { navController.navigate(UStudyRoute.LibrariesScreen) }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Outlined.LocationOn, contentDescription = "Libraries Map") },
            label = { Text(stringResource(R.string.librariesMap_nav)) },
            selected = false,
            onClick = { navController.navigate(UStudyRoute.MapScreen) }
        )

    }
}

