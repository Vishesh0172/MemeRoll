package com.example.memeroll.presentation.main.shared

import android.net.Uri
import java.util.UUID

sealed interface SharedEvent {

    data object PostMeme: SharedEvent
    data class UpdateUri(val uri: Uri): SharedEvent
    data class CancelUpload(val workId: UUID): SharedEvent
}