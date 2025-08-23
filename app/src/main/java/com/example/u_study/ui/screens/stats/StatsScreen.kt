package com.example.u_study.ui.screens.stats

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.u_study.R
import com.example.u_study.ui.composables.AppBar
import com.example.u_study.ui.composables.NavigationBar
import com.example.u_study.ui.theme.ElectricBlue
import com.example.u_study.ui.theme.Orange

@Composable
fun StatsScreen(state: StatsState, navController: NavHostController) {
    val scrollState = rememberScrollState()

    Scaffold (
        topBar = {
            AppBar(stringResource(R.string.statsScreen_name), navController)
        },
        bottomBar = {
            NavigationBar(navController = navController)
        }
    ) { contentPadding ->
        Column (
            modifier = Modifier
                .padding(contentPadding)
                .padding(vertical = 48.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(text= stringResource(R.string.statsText), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsCard(icon = Icons.Outlined.Checklist, color = ElectricBlue, text = stringResource(R.string.tasksDone_text), statistic = state.numTasksDone)
                StatsCard(icon = Icons.Outlined.CollectionsBookmark, color = Orange, text = stringResource(R.string.visitedLibraries_text), statistic = state.numVisitedLibraries)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsCard(icon = Icons.Outlined.Timer, color = Orange, text = stringResource(R.string.completedStudySessions_text), statistic = state.numStudySessions)
                StatsCard(icon = Icons.Outlined.Schedule, color = ElectricBlue, text = stringResource(R.string.studyHours_text), statistic = state.numStudyHours)
            }
        }
    }
}

@Composable
fun StatsCard(icon: ImageVector, color: Color, text: String, statistic: Number ) {
    Card(
        modifier = Modifier
            .widthIn(min = 150.dp, max = 175.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statistic.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}