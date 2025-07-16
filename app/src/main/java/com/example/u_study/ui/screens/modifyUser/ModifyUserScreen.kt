package com.example.u_study.ui.screens.modifyUser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.UStudyRoute
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.NavigationBar
import com.example.u_study.ui.composables.ProfileIcon
import com.example.u_study.ui.composables.SaveButton

@Composable
fun ModifyUserScreen(state: ModifyUserState, actions: ModifyUserActions, navController: NavHostController) {

    val scrollState = rememberScrollState()
    Scaffold(
        topBar = { AppBar(title = stringResource(R.string.editProfileScreen_name), navController) },
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

            //immagine
            ProfileIcon()

            Spacer(Modifier.height(32.dp))

            //column creata solo per allineare a sinistra
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                //first name

                Text(stringResource(R.string.firstName),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = actions::setFirstName,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))
                //last name

                Text(stringResource(R.string.lastName),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = actions::setLastName,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))
                //email

                Text(stringResource(R.string.email),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = actions::setEmail,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    singleLine = true
                )

            }

            Spacer(Modifier.height(32.dp))

            SaveButton(stringResource(R.string.saveChanges_button), onClick = { navController.navigate(UStudyRoute.ProfileScreen)})


        }
    }
}