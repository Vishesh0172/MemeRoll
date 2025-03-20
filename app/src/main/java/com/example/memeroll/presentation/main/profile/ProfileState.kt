package com.example.memeroll.presentation.main.profile

import com.example.memeroll.model.MemeDTO

data class ProfileState(
    val userName: String = "",
    val posts: List<MemeDTO> = emptyList()
)