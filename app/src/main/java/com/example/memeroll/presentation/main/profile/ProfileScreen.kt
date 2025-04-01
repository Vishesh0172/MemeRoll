package com.example.memeroll.presentation.main.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.example.memeroll.R
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.presentation.main.components.DefaultProfilePicture
import com.example.memeroll.presentation.main.shared.SelectedType
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
    navigateToPostMeme: () -> Unit,
    navigateToAuth: () -> Unit
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

    var showFullMeme by remember { mutableStateOf(false) }

    LaunchedEffect(state.sessionStatus) {
        if (state.sessionStatus is SessionStatus.NotAuthenticated)
            navigateToAuth()
    }

    val selectedType = sharedState.selectedType


    Scaffold(
        topBar = { ProfileTopBar(onProfileEvent = onProfileEvent) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
                shape = CircleShape
            ) { Icon(Icons.Default.Add, contentDescription = null) }
        },

        floatingActionButtonPosition = FabPosition.End
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {

            UserPostsGrid(
                list = if (selectedType == SelectedType.POSTS ) sharedState.userPosts else sharedState.likedPosts,
                userName = sharedState.userName,
                numberOfPosts = sharedState.numberOfPosts,
                onMemeClicked = {
                    onSharedEvent(SharedEvent.ShowMeme(it))
                    showFullMeme = true
                },
                changeType = {
                    onSharedEvent(SharedEvent.ChangeType(it))
                },
                selectedTypeName = sharedState.selectedType.typeName
            )

            UploadingMemesList(
                list = sharedState.uploadingMemes,
                modifier = Modifier.align(Alignment.BottomCenter),
                onSharedEvent = onSharedEvent,
                onProfileEvent = onProfileEvent
            )

            if (showFullMeme && sharedState.selectedMeme != null)
                ExpandedMeme(
                    meme = sharedState.selectedMeme,
                    onDismiss = { showFullMeme = false },
                    onDeleteClicked = { id, url ->
                        onSharedEvent(SharedEvent.DeleteMeme(memeId = id, url = url))
                    }
                )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(modifier: Modifier = Modifier, onProfileEvent: (ProfileEvent) -> Unit) {

    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                "My Profile",
                fontFamily = FontFamily(Font(R.font.ubuntu_regular))
            )
        },
        actions = {

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Sign Out") },
                    onClick = { onProfileEvent(ProfileEvent.SignOut) })
            }

            IconButton(onClick = {
                showMenu = !showMenu
            }) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = null) }
        }
    )
}

@Composable
fun UserPostsGrid(
    modifier: Modifier = Modifier,
    list: List<MemeDTO>,
    userName: String,
    numberOfPosts: Int,
    onMemeClicked: (MemeDTO) -> Unit,
    changeType: (SelectedType) -> Unit,
    selectedTypeName: String
) {

    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = modifier.fillMaxSize()) {

        header {
            ProfileHeader(userName = userName, numberOfPosts = numberOfPosts, changeType = changeType, selectedTypeName = selectedTypeName)
        }

        items(list) {
            Box(modifier = Modifier
                .aspectRatio(1f)
                .padding(1.7.dp)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = { onMemeClicked(it) } )
                .animateItem()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(it.imgUrl)
                        .crossfade(true)
                        .scale(Scale.FILL).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

}

@Composable
fun ProfileHeader(modifier: Modifier = Modifier, userName: String, numberOfPosts: Int, changeType:(SelectedType) -> Unit, selectedTypeName: String) {

    var showMenu by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {

                if (userName.isNotEmpty()) {
                    DefaultProfilePicture(
                        userName = userName,
                        textSize = 60.sp,
                        modifier = Modifier.size(90.dp)
                    )
                }
                Text(text = "@$userName", fontSize = 17.sp)
            }

            //Text(numberOfPosts.toString(), style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(end = 25.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(selectedTypeName)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = { showMenu = !showMenu })
                )


            }
        }

        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {

            DropdownMenuItem(
                text = { Text("Posts") },
                onClick = {
                    showMenu = false
                    changeType(SelectedType.POSTS)
                }
            )
            DropdownMenuItem(
                text = { Text("Favorites") },
                onClick = {
                    showMenu = false
                    changeType(SelectedType.FAVORITES)
                }
            )

        }
    }


}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}


@Composable
fun UploadingMemesList(
    modifier: Modifier = Modifier,
    list: List<UploadingMeme>,
    onSharedEvent: (SharedEvent) -> Unit,
    onProfileEvent: (ProfileEvent) -> Unit
) {

    Log.d("SharedWork", "UploadingMemes Composable has list:$list ")

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        items(list) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .size(100.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .animateItem()
            ) {
                AsyncImage(
                    model = it.uri,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )

                when (it.status) {
                    UploadingStatus.SUCCESS -> {

                    }

                    UploadingStatus.RUNNING ->
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center)
                        )

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

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ExpandedMeme(modifier: Modifier = Modifier, meme: MemeDTO, onDismiss: () -> Unit, onDeleteClicked:(Int, String) -> Unit) {

    val previewHandler = AsyncImagePreviewHandler { ColorImage(Color.Magenta.toArgb()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp), contentAlignment = Alignment.Center
    ) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnClickOutside = true)
        ) {

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black),
                        model = meme.imgUrl,
                        contentDescription = null,
                        loading = { CircularProgressIndicator() }
                    )
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null
                        )
                        Text(
                            text = meme.likeCount.toString(),
                            modifier = Modifier.padding(start = 3.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            onDismiss()
                            onDeleteClicked(meme.id!!, meme.imgUrl)
                        }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun ProfilePreview() {
    MemeRollTheme {

        val selectedMeme = MemeDTO(likeCount = 1000.0)
        val userPosts = listOf<MemeDTO>(selectedMeme, selectedMeme, selectedMeme)
        val shareState = SharedState(
            selectedMeme = selectedMeme,
            userPosts = userPosts
        )

        ProfileScreen(
            state = ProfileState(userName = "Vishesh"),
            navigateToPostMeme = {},
            sharedState = shareState,
            onSharedEvent = {},
            onProfileEvent = {},
            navigateToAuth = {}
        )
    }
}

