package com.example.u_study.data.database.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavLibrary(
    @SerialName("lib_id")
    val libId: Int,

    @SerialName("user_id")
    val userId: String,
)