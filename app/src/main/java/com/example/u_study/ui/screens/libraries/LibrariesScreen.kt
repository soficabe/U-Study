package com.example.u_study.ui.screens.libraries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.ListLibraryItem
import com.example.u_study.ui.composables.NavigationBar
import com.example.u_study.ui.screens.map.MapViewModel

@Composable
fun LibrariesScreen(state: LibrariesState, actions: LibrariesActions, navController: NavHostController, mapViewModel: MapViewModel) {

    LaunchedEffect(Unit) {
        actions.refresh()
    }

    Scaffold (
        topBar = {
            AppBar(stringResource(R.string.librariesListScreen_name), navController, isAuthenticated = state.isAuthenticated)
        },
        bottomBar = {
            NavigationBar(navController = navController, isAutheticated = state.isAuthenticated)
        }
    ) { contentPadding ->
        LazyColumn (
            modifier = Modifier
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = actions::onSearchQueryChanged,
                    label = { Text(stringResource(R.string.searchByCity)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    singleLine = true
                )
            }

            items(state.libs, key = { it.id }) { library ->
                ListLibraryItem(
                    library = library,
                    onClick = {
                        navController.navigate(UStudyRoute.LibraryDetailScreen(libraryId = library.id.toString()))
                    },
                    onFavouriteClick = {
                        if (library.isFavourite) {
                            actions.removeFavLib(library.id)
                        } else {
                            actions.addFavLib(library.id)
                        }
                    },
                    mapViewModel = mapViewModel
                )
            }
        }
    }
}