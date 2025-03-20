package com.example.memeroll.presentation.main.profile

sealed interface ProfileEvent {
    data object PostMeme: ProfileEvent
    data object SignOut: ProfileEvent
}