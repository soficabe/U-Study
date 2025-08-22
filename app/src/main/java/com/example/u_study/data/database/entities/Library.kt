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

    @SerialName("phone_number")
    val phoneNumber: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("url")
    val url: String? = null,

    val isFavourite: Boolean = false,

    val isVisited: Boolean = false
)