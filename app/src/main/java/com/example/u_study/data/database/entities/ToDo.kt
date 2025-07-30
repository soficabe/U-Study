package com.example.u_study.data.database.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToDo(
    @SerialName("id")
    val id: Int,

    @SerialName("user_id")
    val userId: String,

    @SerialName("content")
    val content: String,

    @SerialName("completed")
    val completed: Boolean,

    @SerialName("date")
    val date: String
)