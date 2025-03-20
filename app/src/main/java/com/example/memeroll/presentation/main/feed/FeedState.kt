package com.example.memeroll.presentation.main.feed

import com.example.memeroll.authentication.AuthStatus
import com.example.memeroll.model.MemeDTO
import io.github.jan.supabase.auth.status.SessionStatus

data class FeedState(
    val sessionStatus: SessionStatus = SessionStatus.Initializing,
    val memeList: List<MemeDTO> = emptyList()
)