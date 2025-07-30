package com.example.u_study.data.database.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("surname")
    val surname: String,

    @SerialName("email")
    val email: String,

    @SerialName("image")
    val image: String? = null
)