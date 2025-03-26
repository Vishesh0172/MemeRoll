package com.example.memeroll.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemeDTO(

    @SerialName("id")
    val id: Int? = null,

    @SerialName("img_url")
    val imgUrl: String = "",

    @SerialName("like_count")
    var likeCount: Double = 0.0,

    @SerialName("user_name")
    val userName: String = ""
)
