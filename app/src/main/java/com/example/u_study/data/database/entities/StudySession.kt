package com.example.u_study.data.database.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudySession(
    @SerialName("id")
    val id: Int,

    @SerialName("user_id")
    val userId: String,

    @SerialName("startTime")
    val startTime: String,

    @SerialName("endTime")
    val endTime: String? = null,

    @SerialName("duration")
    val duration: Long? = null,

    @SerialName("date")
    val date: String? = null
)