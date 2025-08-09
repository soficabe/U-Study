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
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton

@Composable
fun LibraryDetailScreen(state: LibraryDetailState,
                        actions: LibraryDetailActions,
                        navController: NavHostController) {

    Scaffold(
        topBar = { AppBar("Library Detail", navController) },
        bottomBar = { NavigationBar(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.library != null) {
                // se la libreria è trovata
                LibraryDetailCard(
                    library = state.library,
                    onFavouriteClick = { /* TODO: lalala */ },
                    onBackToListClick = { navController.popBackStack() },
                    onViewInMapClick = { /* TODO: naviga alla mappaaa */ }
                )
            } else {
                // se la libreria non è stata trovata :(((
                Text("Libreria non trovata.")
            }
        }
    }
}

@Composable
fun LibraryDetailCard(
    library: Library,
    onFavouriteClick: () -> Unit,
    onBackToListClick: () -> Unit,
    onViewInMapClick: () -> Unit
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
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.MoreVert, "More options")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            TextButton(onClick = onFavouriteClick) {
                Icon(Icons.Outlined.FavoriteBorder, "Add to favourites", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add to favourites")
            }
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Phone Number: ${library.phoneNumber}")
                Text("Email: ${library.email}")
                Text("Url: ${library.url}")
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onBackToListClick, modifier = Modifier.weight(1f)) {
                    Text("Back to List")
                }
                Button(onClick = onViewInMapClick, modifier = Modifier.weight(1f)) {
                    Text("View in Map")
                }
            }
        }
    }
}