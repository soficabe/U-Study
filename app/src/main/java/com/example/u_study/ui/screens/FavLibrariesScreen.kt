package com.example.u_study.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.FilterChipsRow
import com.example.u_study.ui.composables.NavigationBar

@Composable
fun FavLibrariesScreen() {
    val chipLabels = listOf("Bologna", "Cesena", "Cesenatico", "Faenza", "Forlì", "Imola", "Ozzano d'Emilia", "Ravenna", "Rimini")

    Scaffold (
        topBar = {
            AppBar("Favorite Libraries")
        },
        bottomBar = {
            NavigationBar()
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
        ) {
            FilterChipsRow(items = chipLabels)
        }
    }
}