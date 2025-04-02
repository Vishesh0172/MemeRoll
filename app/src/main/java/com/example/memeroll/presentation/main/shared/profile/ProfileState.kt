package com.example.memeroll.presentation.main.shared.profile


import com.example.memeroll.model.MemeDTO
import io.github.jan.supabase.auth.status.SessionStatus

data class ProfileState(
    val userName: String = "",
    val posts: List<MemeDTO> = emptyList(),
    val likedPosts: List<MemeDTO> = emptyList(),
    val numberOfPosts: Int = 0,
    val sessionStatus: SessionStatus = SessionStatus.Initializing,
)