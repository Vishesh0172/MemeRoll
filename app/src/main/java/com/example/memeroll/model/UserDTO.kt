package com.example.memeroll.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(

    @SerialName("user_id")
    val userId: String = "",

    @SerialName("user_name")
    val userName: String = "",

    @SerialName("user_posts")
    val posts: List<Int>? = emptyList<Int>()

)
