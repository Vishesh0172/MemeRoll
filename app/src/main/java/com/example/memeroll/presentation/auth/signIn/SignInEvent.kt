package com.example.memeroll.presentation.auth.signIn

sealed interface SignInEvent {
    data class EmailChange(val value: String): SignInEvent
    data class PasswordChange(val value: String): SignInEvent
    data object SignInClick: SignInEvent
}