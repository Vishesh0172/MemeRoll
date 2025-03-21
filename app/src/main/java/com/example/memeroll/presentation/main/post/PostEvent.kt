package com.example.memeroll.presentation.main.post

import com.example.memeroll.model.MemeDTO

sealed interface PostEvent {
    data object PostMeme: PostEvent
}