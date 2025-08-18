package com.example.u_study.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.u_study.ui.composables.NavigationBar
import com.example.u_study.ui.composables.ProfileIcon
import com.example.u_study.ui.composables.SaveButton

@Composable
fun ProfileScreen(state: ProfileState, navController: NavHostController) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = { AppBar(title = stringResource(R.string.profileScreen_name), navController) },
        bottomBar = { NavigationBar(navController = navController) }
    ) {
            innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Immagine profilo aggiornata per supportare le URL
            ProfileIcon(
                imageUrl = state.user?.image
            )

            Spacer(Modifier.height(16.dp))

            // Nome cognome
            Text(
                text = "${state.user?.name ?: ""} ${state.user?.surname ?: ""}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // Bottoni
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                ProfileButton(
                    icon = Icons.Outlined.Favorite,
                    text = stringResource(R.string.favoriteLibraries_button),
                    onClick = { navController.navigate(UStudyRoute.FavLibrariesScreen) }
                )
                ProfileButton(
                    icon = Icons.AutoMirrored.Outlined.ShowChart,
                    text = stringResource(R.string.stats_button),
                    onClick = { navController.navigate(UStudyRoute.StatsScreen) }
                )
            }

            Spacer(Modifier.height(32.dp))

            // Mail
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    stringResource(R.string.email),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.user?.email ?: "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(Modifier.height(32.dp))

            SaveButton(
                text = stringResource(R.string.editProfile_button),
                onClick = { navController.navigate(UStudyRoute.ModifyUserScreen) }
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)), // Blu
        modifier = Modifier.width(150.dp).height(80.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            Spacer(Modifier.height(4.dp))
            Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}