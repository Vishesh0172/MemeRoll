package com.example.memeroll.presentation.auth.signIn

import io.github.jan.supabase.auth.status.SessionStatus

data class SignInState(
    val email: String = "",
    val password: String = "",
    val sessionStatus: SessionStatus = SessionStatus.Initializing
)