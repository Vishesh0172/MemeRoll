package com.example.memeroll.presentation.auth.signUp

import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val sessionStatus: SessionStatus = SessionStatus.Initializing
)
