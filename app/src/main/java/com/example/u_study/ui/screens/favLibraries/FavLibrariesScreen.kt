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
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.FilterChipsRow
import com.example.u_study.ui.composables.ListLibraryItem
import com.example.u_study.ui.composables.NavigationBar
import com.example.u_study.ui.screens.favLibraries.FavLibrariesState

@Composable
fun FavLibrariesScreen(state: FavLibrariesState, navController: NavHostController) {
    //val chipLabels = listOf("Bologna", "Cesena", "Cesenatico", "Faenza", "Forlì", "Imola", "Ozzano d'Emilia", "Ravenna", "Rimini")
    //val elems = (0..10).map { "Library $it" }

    var selectedCities by remember { mutableStateOf(emptySet<String>()) }

    Scaffold (
        topBar = {
            AppBar(stringResource(R.string.favoriteLibrariesScreen_name), navController)
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
                    itemLabel = { city -> city },
                    selectedItems = selectedCities,
                    onItemSelected = { city ->
                        val newSelection = selectedCities.toMutableSet()
                        if (city in newSelection) {
                            newSelection.remove(city)
                        } else {
                            newSelection.add(city)
                        }
                        selectedCities = newSelection
                    })
            }
            items(state.favLibs) {
                ListLibraryItem(it)
            }
        }
    }
}