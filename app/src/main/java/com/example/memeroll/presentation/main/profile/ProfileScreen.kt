package com.example.memeroll.presentation.main.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.presentation.main.shared.SharedEvent
import com.example.memeroll.presentation.main.shared.SharedState
import com.example.memeroll.presentation.main.shared.UploadingMeme
import com.example.memeroll.presentation.main.shared.UploadingStatus
import com.example.memeroll.ui.theme.MemeRollTheme
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun ProfileScreen(
    sharedState: SharedState,
    state: ProfileState,
    onProfileEvent: (ProfileEvent) -> Unit,
    onSharedEvent: (SharedEvent) -> Unit,
    navigateToPostMeme:() -> Unit,
    navigateToAuth:() -> Unit
) {

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            onSharedEvent(SharedEvent.UpdateUri(uri))
            navigateToPostMeme()
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    LaunchedEffect(state.sessionStatus) {
        if (state.sessionStatus is SessionStatus.NotAuthenticated)
            navigateToAuth()
    }


    Scaffold(
        topBar = { ProfileTopBar(onProfileEvent = onProfileEvent) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
                shape = CircleShape) { Icon(Icons.Default.Add, contentDescription = null)}
        },

        floatingActionButtonPosition = FabPosition.End
    ){ padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            UserPostsGrid(list = state.posts, userName = state.userName, numberOfPosts = state.numberOfPosts)
            UploadingMemesList(
                list = sharedState.uploadingMemes,
                modifier = Modifier.align(Alignment.BottomCenter),
                onSharedEvent = onSharedEvent
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(modifier: Modifier = Modifier, onProfileEvent:(ProfileEvent) -> Unit) {

    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = { Text("My Profile") },
        actions = {

            DropdownMenu(expanded = showMenu, onDismissRequest = {showMenu = false}) {
                DropdownMenuItem(text = {Text("Sign Out")}, onClick = {onProfileEvent(ProfileEvent.SignOut)})
            }

            IconButton(onClick = {showMenu = !showMenu}) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = null) }
        }
    )
}

@Composable
fun UserPostsGrid(modifier: Modifier = Modifier, list: List<MemeDTO>, userName: String, numberOfPosts: Int) {

    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = modifier.fillMaxSize()) {

        header {
            ProfileHeader(userName = userName, numberOfPosts = numberOfPosts)
        }

        items(list){
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(1.7.dp)
                    .clip(RoundedCornerShape(6.dp)),
                model = ImageRequest.Builder(LocalContext.current).data(it.imgUrl).crossfade(true)
                    .scale(Scale.FILL).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }

}

@Composable
fun ProfileHeader(modifier: Modifier = Modifier, userName: String, numberOfPosts: Int) {

    Column(
        modifier = modifier.fillMaxWidth().padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
               horizontalAlignment = Alignment.Start
            ) {
                Image(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Text(text = userName, fontSize = 20.sp)


            }

            Text(numberOfPosts.toString(), style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(end = 25.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        TabRow(0) {
            Icon(Icons.Default.Face, contentDescription = null)
            Icon(Icons.Default.FavoriteBorder, contentDescription = null)
        }
    }


}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
){
    item(span = {GridItemSpan(this.maxLineSpan)}, content = content)
}


@Composable
fun UploadingMemesList(modifier: Modifier = Modifier, list: List<UploadingMeme>, onSharedEvent: (SharedEvent) -> Unit) {

    Log.d("SharedWork", "UploadingMemes Composable has list:$list ")

    LazyRow(modifier = modifier
        .fillMaxWidth()
        .height(100.dp)) {
        items(list){
            Box(modifier = Modifier
                .aspectRatio(1f)
                .size(100.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))){
                AsyncImage(
                    model = it.uri,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )

                when(it.status) {
                    UploadingStatus.SUCCESS -> {

                    }
                    UploadingStatus.RUNNING ->
                        CircularProgressIndicator(modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.Center))
                    UploadingStatus.FAILURE ->
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .align(Alignment.Center)
                        )
                }

                IconButton(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd),
                    onClick = {
                        onSharedEvent(SharedEvent.CancelUpload(it.workId))
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                }
            }

        }
    }

}


@Preview
@Composable
fun ProfilePreview() {
    MemeRollTheme {
        ProfileScreen(
            state = ProfileState(userName = "Vishesh"),
            navigateToPostMeme = {},
            sharedState = TODO(),
            onSharedEvent = {},
            onProfileEvent = {},
            navigateToAuth = {}
        )
    }
}