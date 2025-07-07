package com.example.u_study.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.u_study.ui.theme.LightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String) {
    CenterAlignedTopAppBar(
        title = { Text(
            text = title,
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Menu, "Menu", tint = Color.Black)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors( //Così possiamo cambiare solo alcuni dei colori di default
            containerColor = LightBlue
        )
    )
}