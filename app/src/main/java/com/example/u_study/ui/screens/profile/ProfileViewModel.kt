package com.example.u_study.ui.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileState(
    val firstName: String = "Sofia",
    val lastName: String = "Bianchi",
    val email: String = "sofiabianchi@gmail.com",
    val numTasksDone: Number = 0,
    val numStudySessions: Number = 0,
    val numVisitedLibraries: Number = 0,
    val numStudyHours: Number = 0
)

class ProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
}