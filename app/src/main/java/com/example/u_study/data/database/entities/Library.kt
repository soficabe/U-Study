package com.example.u_study.data.database.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Library(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("city")
    val city: String,

    @SerialName("address")
    val address: String,

    @SerialName("latitude")
    val latitude: Float,

    @SerialName("longitude")
    val longitude: Float,

    @SerialName("timetable")
    val timetable: String? = null,

    @SerialName("image")
    val image: String? = null
)