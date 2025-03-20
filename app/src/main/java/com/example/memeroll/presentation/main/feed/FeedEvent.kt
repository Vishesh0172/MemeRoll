package com.example.memeroll.presentation.main.feed


sealed interface FeedEvent{
    data object LikeClicked: FeedEvent
    data object SignOut: FeedEvent
    data class LimitReached(val pageNumber: Int): FeedEvent
}