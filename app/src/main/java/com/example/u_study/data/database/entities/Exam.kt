package com.example.u_study.data.database.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Exam(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("cfu")
    val cfu: Int,

    @SerialName("date")
    val date: String,

    @SerialName("grade")
    val grade: Int? = null, //il voto può essere nullo

    @SerialName("user_id")
    val userId: String
)

