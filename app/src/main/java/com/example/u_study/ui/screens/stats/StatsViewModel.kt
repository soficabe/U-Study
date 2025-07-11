package com.example.u_study.ui.screens.stats

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StatsState(
    val numTasksDone: Number = 0,
    val numStudySessions: Number = 0,
    val numVisitedLibraries: Number = 0,
    val numStudyHours: Number = 0
)

class StatsViewModel : ViewModel() {
    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()
}