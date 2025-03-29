package com.example.memeroll.presentation.main.shared

import android.net.Uri
import com.example.memeroll.model.MemeDTO
import java.util.UUID

sealed interface SharedEvent {

    data object PostMeme: SharedEvent
    data class UpdateUri(val uri: Uri): SharedEvent
    data class CancelUpload(val workId: UUID): SharedEvent
    data class DeleteMeme(val memeId: Int, val url: String): SharedEvent
    data class ShowMeme(val meme: MemeDTO): SharedEvent
}