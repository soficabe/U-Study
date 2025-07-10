package com.example.u_study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavGraph
import androidx.navigation.compose.rememberNavController
import com.example.u_study.ui.UStudyNavGraph
import com.example.u_study.ui.screens.HomeScreen
import com.example.u_study.ui.screens.SettingsScreen
import com.example.u_study.ui.theme.U_StudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            U_StudyTheme {
                val navController = rememberNavController()
                UStudyNavGraph(navController)
            }
        }
    }
}

