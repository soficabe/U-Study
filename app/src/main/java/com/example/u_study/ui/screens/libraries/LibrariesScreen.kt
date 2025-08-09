package com.example.u_study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.FilterChipsRow
import com.example.u_study.ui.composables.ListLibraryItem
import com.example.u_study.ui.composables.NavigationBar
import com.example.u_study.ui.screens.libraries.LibrariesActions
import com.example.u_study.ui.screens.libraries.LibrariesState

@Composable
fun LibrariesScreen(state: LibrariesState, actions: LibrariesActions, navController: NavHostController) {

    var selectedCities by remember { mutableStateOf(emptySet<String>()) }

    Scaffold (
        topBar = {
            AppBar(stringResource(R.string.librariesListScreen_name), navController)
        },
        bottomBar = {
            NavigationBar(navController = navController)
        }
    ) { contentPadding ->
        LazyColumn (
            modifier = Modifier
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item{
                FilterChipsRow(items = state.cities,
                    itemLabel = { city -> city }, //items è già una lista di string quindi la label è l'item stesso
                    selectedItems = selectedCities,
                    onItemSelected = { city ->
                        //fatto pre aggiungere/rimuovere un chip dalla selezione.
                        val newSelection = selectedCities.toMutableSet()
                        if (city in newSelection) {
                            newSelection.remove(city)
                        } else {
                            newSelection.add(city)
                        }
                        selectedCities = newSelection
                    })
            }
            items(state.libs, key = { it.id }) { library ->
                ListLibraryItem(
                    library = library,
                    onClick = {
                        navController.navigate(UStudyRoute.LibraryDetailScreen(libraryId = library.id.toString()))
                    }
                )
            }
        }
    }
}
