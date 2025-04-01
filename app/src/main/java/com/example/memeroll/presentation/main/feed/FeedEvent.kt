package com.example.memeroll.presentation.main.feed


sealed interface FeedEvent{
    data class Liked(val postId: Int): FeedEvent
    data class Unliked(val postId: Int): FeedEvent
    data class LimitReached(val pageNumber: Int): FeedEvent
    data object Refreshed: FeedEvent
}