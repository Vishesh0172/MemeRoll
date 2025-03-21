package com.example.memeroll.presentation.main.feed 

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.presentation.main.feed.FeedEvent.*
import io.github.jan.supabase.auth.status.SessionStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    state: FeedState,
    onEvent: (FeedEvent) -> Unit,
    navigateToAuth: () -> Unit,
    navigateToProfile:() -> Unit
){

    LaunchedEffect(state.sessionStatus) {
        if (state.sessionStatus is SessionStatus.NotAuthenticated)
            navigateToAuth()
    }

    Box(modifier = modifier.fillMaxSize()) {

        val pagerState = rememberPagerState { state.memeList.size }
        VerticalPager(
            state = pagerState,
        ) { index ->
            if (index < pagerState.pageCount - 1){
                MemeComposable(meme = state.memeList[index]) { }
            }
            else{
                MemeComposable(meme = state.memeList[index]) { }
                onEvent(LimitReached(index))
           }
        }

        Text(text = "MemeRoll", modifier = Modifier.align(Alignment.TopStart).padding(start = 10.dp, top = 10.dp))
        IconButton(onClick = {navigateToProfile()}, modifier = Modifier.align(Alignment.TopEnd).padding(15.dp)) {
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
        }

        Button(onClick = {onEvent(SignOut)}, modifier = Modifier.padding(40.dp)) { Text("Sign Out")}

    }
}


@Composable
fun MemeComposable(modifier: Modifier = Modifier, meme: MemeDTO, onEvent: (FeedEvent) -> Unit) {

    Box(modifier = modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center){

        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current).data(meme.imgUrl).build(),
            contentDescription = null,
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick =  {}) {
                Icon(
                    modifier = Modifier.size(100.dp),
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }

            Text(meme.likeCount.toString())
        }

        Row (modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 22.dp, start = 10.dp), verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Text(text = meme.userName)
        }



    }

}

@Preview
@Composable
fun FeedPreview(){
    val memeList = listOf<MemeDTO>(MemeDTO(likeCount = 1000.0, userName = "Vishesh"))
    Surface{
        FeedScreen(
            state = FeedState(memeList = memeList),
            onEvent = {},
            navigateToAuth = {},
            navigateToProfile = {}
        )
    }
}