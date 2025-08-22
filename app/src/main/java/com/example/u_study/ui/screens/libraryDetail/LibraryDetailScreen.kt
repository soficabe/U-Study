package com.example.u_study.ui.screens.libraryDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.data.database.entities.Library
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.NavigationBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.u_study.R

@Composable
fun LibraryDetailScreen(state: LibraryDetailState,
                        actions: LibraryDetailActions,
                        navController: NavHostController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { AppBar(stringResource(R.string.libraryDetailScreen_name), navController, isAuthenticated = state.isAuthenticated) },
        bottomBar = { NavigationBar(navController = navController, isAutheticated = state.isAuthenticated) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.library != null) {
                // se la libreria è trovata
                LibraryDetailCard(
                    library = state.library,
                    onFavouriteClick = actions::onFavouriteClick,
                    onBackToListClick = { navController.popBackStack() },
                    onViewInMapClick = { navController.navigate("map_screen/${state.library.id}") },
                    stateLib = state
                )
            } else {
                // se la libreria non è stata trovata :(((
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.libraryNotFound))
                }
            }
        }
    }
}

@Composable
fun LibraryDetailCard(
    library: Library,
    onFavouriteClick: () -> Unit,
    onBackToListClick: () -> Unit,
    onViewInMapClick: () -> Unit,
    stateLib: LibraryDetailState
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Book, "Library", modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(library.name, style = MaterialTheme.typography.titleLarge)
                    Text(library.city, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            TextButton(onClick = onFavouriteClick, enabled = stateLib.isAuthenticated) {
                Icon(
                    imageVector = if (library.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favourite",
                    modifier = Modifier.size(20.dp),
                    tint = if (library.isFavourite) Color.Red else LocalContentColor.current
                )
                Spacer(Modifier.width(8.dp))
                Text(text = if (library.isFavourite) stringResource(R.string.removeFavoriteLib) else stringResource(R.string.addFavoriteLib))
            }
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val notAvailable = stringResource(R.string.notAvailable)

                Text("${stringResource(R.string.phoneNumber)} ${library.phoneNumber ?: notAvailable}")
                Text("${stringResource(R.string.emailDetail)} ${library.email ?: notAvailable}")
                Text("${stringResource(R.string.url)} ${library.url ?: notAvailable}")
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onBackToListClick, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.backToList))
                }
                Button(onClick = onViewInMapClick, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.viewInMap))
                }
            }
        }
    }
}