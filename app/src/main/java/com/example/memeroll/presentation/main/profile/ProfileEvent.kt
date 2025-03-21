package com.example.memeroll.presentation.main.profile

import android.net.Uri

sealed interface ProfileEvent {
    data class PostMeme(val uri: Uri): ProfileEvent
    data object SignOut: ProfileEvent
}