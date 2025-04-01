package com.example.memeroll.presentation.auth.signUp

import io.github.jan.supabase.auth.status.SessionStatus

data class SignUpState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val validEmail: Boolean = false,
    val validPassword: Boolean = false,
    val validName: Boolean = false,
    val errorText: String = "",
    val sessionStatus: SessionStatus = SessionStatus.Initializing
)
