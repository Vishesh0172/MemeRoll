package com.example.memeroll.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemeDTO(

    @SerialName("img_url")
    val imgUrl: String = "",

    @SerialName("like_count")
    val likeCount: Double = 0.0,

    @SerialName("user_name")
    val userName: String = ""
)
