package com.example.u_study.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Timer
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

/* è la NavigationBar (nella parte bassa dello schermo)
 * ancora non munita di nessuna azione. Presenta solo le
 * 4 icone. Tutti i selected sono a false. Gli onclick
 * sono vuoti
 */
@Composable
fun NavigationBar(modifier: Modifier = Modifier, navController: NavHostController) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Timer, contentDescription = "Study Session") },
            label = { Text(stringResource(R.string.sessions_nav)) },
            selected = false,
            onClick = { navController.navigate(UStudyRoute.HomeScreen) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.CheckBox, contentDescription = "TO-DO List") },
            label = { Text(stringResource(R.string.toDo_nav)) },
            selected = false,
            onClick = { navController.navigate(UStudyRoute.ToDoScreen) }
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
            onClick = { navController.navigate(UStudyRoute.FavLibrariesScreen) }
        )

    }
}

