package com.example.memeroll.presentation.auth.signUp

import com.example.memeroll.presentation.auth.signIn.SignInEvent

sealed interface SignUpEvent {

    data class EmailChange(val value: String): SignUpEvent
    data class NameChange(val value: String): SignUpEvent
    data class PasswordChange(val value: String): SignUpEvent
    data object SignUpClick: SignUpEvent

}