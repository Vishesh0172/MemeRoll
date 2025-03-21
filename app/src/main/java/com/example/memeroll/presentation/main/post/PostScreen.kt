package com.example.memeroll.presentation.main.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.memeroll.ui.theme.MemeRollTheme

@Composable
fun PostScreen(
    modifier: Modifier = Modifier,
    state: PostState,
    onEvent: (PostEvent) -> Unit,
) {

    Box(modifier = modifier.fillMaxSize().background(Color.Black)){

        AsyncImage(
            modifier = Modifier.fillMaxSize().align(Alignment.Center),
            model = state.uri,
            contentDescription = null
        )

        ElevatedButton(
            onClick = { onEvent(PostEvent.PostMeme) },
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
            onEvent = {}
        )
    }
}