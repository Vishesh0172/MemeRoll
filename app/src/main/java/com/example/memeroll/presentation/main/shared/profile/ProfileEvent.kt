package com.example.memeroll.presentation.main.shared.profile


sealed interface ProfileEvent {

    data object SignOut: ProfileEvent
    data class AddMemeToPosts(val memeId: Int): ProfileEvent

}