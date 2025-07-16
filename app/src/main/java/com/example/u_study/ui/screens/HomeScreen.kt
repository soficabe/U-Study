package com.example.u_study.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.theme.LightBlue
import com.example.u_study.ui.theme.Yellow

@Composable
fun HomeScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            AppBar(stringResource(R.string.homeScreen_name), navController)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.greeting) + "Sofia 👋", //da cambiare con nome del profilo
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FeatureButton(icon = Icons.Outlined.Timer, text = stringResource(R.string.studySession_button), onClick = { navController.navigate(UStudyRoute.LoginScreen) })
                FeatureButton(icon = Icons.Outlined.CheckBox, text = stringResource(R.string.toDo_button), onClick = { navController.navigate(UStudyRoute.ToDoScreen) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FeatureButton(icon = Icons.Outlined.Book, text = stringResource(R.string.librariesList_button), onClick = { navController.navigate(UStudyRoute.LibrariesScreen)})
                FeatureButton(icon = Icons.Outlined.LocationOn, text = stringResource(R.string.librariesMap_button), onClick = {})
            }

            Spacer(modifier = Modifier.height(24.dp))

            LongButton(icon = Icons.Filled.FavoriteBorder, text = stringResource(R.string.favoriteLibraries_button), { navController.navigate(UStudyRoute.FavLibrariesScreen) })
            Spacer(modifier = Modifier.height(16.dp))
            LongButton(icon = Icons.AutoMirrored.Outlined.ShowChart, text = stringResource(R.string.stats_button), { navController.navigate(UStudyRoute.StatsScreen) })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Yellow),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(48.dp), tint = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
        }
    }
}

@Composable
fun LongButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightBlue),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = Color.Black)
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Open", tint = Color.Black)
        }
    }
}
