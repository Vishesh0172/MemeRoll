package com.example.memeroll.presentation.main.post

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.memeroll.presentation.main.shared.SharedEvent
import com.example.memeroll.presentation.main.shared.SharedState
import com.example.memeroll.ui.theme.MemeRollTheme

@Composable
fun PostScreen(
    modifier: Modifier = Modifier,
    sharedState: SharedState,
    state: PostState,
    onPostEvent: (PostEvent) -> Unit,
    onSharedEvent: (SharedEvent) -> Unit,
    navigateBack:() -> Unit
) {

    LaunchedEffect(sharedState.workIds) {
        if (sharedState.postUriString.isEmpty()) {
            Log.d("SharedWork", "PostScreen: Navigating back to ProfileScreen with workIds: ${sharedState.workIds}")
            navigateBack()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)){

        AsyncImage(
            modifier = Modifier.fillMaxSize().align(Alignment.Center),
            model = android.net.Uri.parse(sharedState.postUriString),
            contentDescription = null
        )

        ElevatedButton(
            onClick = { onSharedEvent(SharedEvent.PostMeme) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(25.dp)
        ) { Text("Done") }
    }
}

@Preview
@Composable
fun PostPreview() {
    MemeRollTheme {
        PostScreen(
            state = PostState(),
            onSharedEvent = {},
            onPostEvent = {},
            navigateBack = {},
            modifier = TODO(),
            sharedState = TODO(),
        )
    }
}