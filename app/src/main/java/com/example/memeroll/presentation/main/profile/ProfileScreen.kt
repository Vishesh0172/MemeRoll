package com.example.memeroll.presentation.main.profile

import android.R.attr.padding
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.ui.theme.MemeRollTheme

@Composable
fun ProfileScreen(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    navigateToPostMeme:(Uri) -> Unit
) {

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            navigateToPostMeme(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


    Scaffold(
        topBar = { ProfileTopBar() },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
                shape = CircleShape) { Icon(Icons.Default.Add, contentDescription = null,)}
        },

        floatingActionButtonPosition = FabPosition.End
    ){ padding ->

        Column(modifier = Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(imageVector = Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(50.dp))
            Text(text = state.userName, fontSize = 30.sp)

            Spacer(modifier = Modifier.height(40.dp))

            UserPostsGrid(list = state.posts)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(modifier: Modifier = Modifier) {

    TopAppBar(
        title = { Text("MemeRoll") },
        actions = {
            IconButton(onClick = {}) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = null) }
        }
    )
}

@Composable
fun UserPostsGrid(modifier: Modifier = Modifier, list: List<MemeDTO>) {

    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = modifier.fillMaxSize()) {
        items(list){
            AsyncImage(
                modifier = Modifier.aspectRatio(1f).padding(2.dp).clip(RoundedCornerShape(10.dp)),
                model = it.imgUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }

}

@Preview
@Composable
fun ProfilePreview() {
    MemeRollTheme {
        ProfileScreen(
            state = ProfileState(userName = "Vishesh"),
            onEvent = {},
            navigateToPostMeme = {}
        )
    }
}