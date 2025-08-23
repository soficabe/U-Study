package com.example.u_study.data.repositories

import android.util.Log
import com.example.u_study.data.database.entities.Exam
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExamRepository(private val postgrest: Postgrest) {

    private val examsTable = postgrest.from("Exams")

    suspend fun getExams(): List<Exam> {
        return try {
            examsTable.select().decodeList()
        } catch (e: Exception) {
            Log.e("ExamRepository", "Error fetching exams", e)
            emptyList()
        }
    }

    suspend fun addExam(name: String, cfu: Int, date: LocalDate, grade: Int? = null) {
        try {
            val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

            val newExam = buildJsonObject {
                put("name", name)
                put("cfu", cfu)
                put("date", formattedDate)
                grade?.let { put("grade", it) } //aggiungo solo se non nullo

            }
            examsTable.insert(newExam)

        } catch (e: Exception) {
            Log.e("ExamDebug", "DATABASE (Repository): Inserimento FALLITO con eccezione", e)
        }

    }

    suspend fun updateExam(id: Int, name: String, cfu: Int, date: LocalDate, grade: Int?) {
        val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        examsTable.update({
            set("name", name)
            set("cfu", cfu)
            set("date", formattedDate)
            set("grade", grade) //se null viene rimosso
        }) {
            filter { Exam::id eq id }
        }
    }

    suspend fun deleteExam(id: Int) {
        examsTable.delete {
            filter { Exam::id eq id }
        }
    }
}
