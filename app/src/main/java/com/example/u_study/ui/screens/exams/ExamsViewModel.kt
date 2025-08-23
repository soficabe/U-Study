package com.example.u_study.ui.screens.exams

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.u_study.data.database.entities.Exam
import com.example.u_study.data.repositories.ExamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate


data class ExamsState(
    val upcomingExams: List<Exam> = emptyList(),
    val completedExams: List<Exam> = emptyList(),
    val isLoading: Boolean = false
)

interface ExamsActions {
    fun addExam(name: String, cfu: Int, date: LocalDate, grade: Int? = null)
    fun deleteExam(id: Int)
    fun updateExam(id: Int, name: String, cfu: Int, date: LocalDate, grade: Int? = null)
}

class ExamsViewModel(private val examRepository: ExamRepository) : ViewModel() {

    private val _state = MutableStateFlow(ExamsState())
    val state = _state.asStateFlow()

    init {
        loadExams()
    }

    private fun loadExams() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val allExams = examRepository.getExams()

            // divido la lista in base a quelli che hanno voto e quelli che non ce l'hanno
            _state.update {
                it.copy(
                    isLoading = false,
                    upcomingExams = allExams.filter { exam -> exam.grade == null },
                    completedExams = allExams.filter { exam -> exam.grade != null }
                )
            }
        }
    }

    val actions = object : ExamsActions {
        override fun addExam(name: String, cfu: Int, date: LocalDate, grade: Int?) {
            viewModelScope.launch {
                examRepository.addExam(name, cfu, date, grade)
                loadExams()
            }
        }

        override fun deleteExam(id: Int) {
            viewModelScope.launch {
                examRepository.deleteExam(id)
                loadExams()
            }
        }

        override fun updateExam(id: Int, name: String, cfu: Int, date: LocalDate, grade: Int?) {
            viewModelScope.launch {
                examRepository.updateExam(id, name, cfu, date, grade)
                loadExams()
            }
        }
    }
}