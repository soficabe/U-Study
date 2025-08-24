package com.example.u_study.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.repositories.ExamRepository
import com.example.u_study.data.repositories.ToDoRepository
import com.example.u_study.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class StatsState(
    val numTasksDone: Number = 0,
    val numExams: Number = 0,
    val gpa: Number = 0, //media
    val numVisitedLibraries: Number = 0,
    val isLoading: Boolean = true
)

class StatsViewModel(
    private val toDoRepository: ToDoRepository,
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            //ToDos
            val todos = toDoRepository.getTodos().sortedBy { it.id } //ordinato per ID
            val completedTodos = todos.filter { it.completed }.size

            //VisitedLibraries
            val visitedLibs = userRepository.getVisitedLibraries().first().size

            //Exams
            val doneExams = examRepository.getExams().filter{ LocalDate.parse(it.date) <= LocalDate.now() }
            val nExams = doneExams.size

            val examsWithGrade = doneExams.filter { it.grade != null }

            val average = if (examsWithGrade.isNotEmpty()) {
                val totalWeightedGrades = examsWithGrade.sumOf { (it.grade!! * it.cfu).toDouble() }
                val totalCredits = examsWithGrade.sumOf { it.cfu.toDouble() }
                val rawAverage = totalWeightedGrades / totalCredits
                (rawAverage * 100).toInt() / 100.0
                //String.format("%.2f", totalWeightedGrades / totalCredits).toDouble()
            } else {
                0.0
            }

            _state.update { it.copy(numTasksDone = completedTodos, numVisitedLibraries = visitedLibs, numExams = nExams, gpa = average, isLoading = false) }
        }
    }
}