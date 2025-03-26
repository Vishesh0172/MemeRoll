package com.example.memeroll.presentation.main.feed 

import android.icu.text.CompactDecimalFormat
import android.util.Log
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.presentation.main.feed.FeedEvent.*
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    state: FeedState,
    onEvent: (FeedEvent) -> Unit,
    navigateToProfile:() -> Unit
){


    Box(modifier = modifier.fillMaxSize()) {

        val pagerState = rememberPagerState { state.memeMap.size }
        VerticalPager(
            state = pagerState,
        ) { index ->
            val memeList = state.memeMap.keys
            val meme = memeList.elementAt(index)
            if (index < pagerState.pageCount - 1){
                MemeComposable(meme = meme, onEvent = onEvent, liked = state.memeMap.getValue(meme))
            }
            else{
                MemeComposable(meme = meme, onEvent = onEvent, liked = state.memeMap.getValue(meme))
                onEvent(LimitReached(index))
           }
        }

        Text(text = "MemeRoll", modifier = Modifier.align(Alignment.TopStart).padding(start = 10.dp, top = 10.dp), style = MaterialTheme.typography.displayMedium)
        IconButton(onClick = {navigateToProfile()}, modifier = Modifier.align(Alignment.TopEnd).padding(15.dp)) {
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
        }


    }
}


@Composable
fun MemeComposable(modifier: Modifier = Modifier, meme: MemeDTO, onEvent: (FeedEvent) -> Unit, liked: Boolean) {

    var likedBoolean by remember { mutableStateOf(liked) }
    var likeCount by remember { mutableStateOf(meme.likeCount) }
    val likeColor by animateColorAsState(targetValue = if (likedBoolean) Color.Red else Color.White)

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

            IconButton(
                onClick =  {
                    likedBoolean = !likedBoolean
                    if (likedBoolean) {
                        likeCount = likeCount + 1
                        onEvent(Liked(meme.id!!))
                        Log.d("FeedScreen", "postId: ${meme.id}")
                    }else{
                        likeCount = likeCount - 1
                        onEvent(Unliked(meme.id!!))
                    }
                }
            ) {
                Icon(
                    tint = likeColor,
                    modifier = Modifier.size(100.dp).shadow(10.dp, CircleShape),
                    imageVector = if(likedBoolean) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }

            val likes = CompactDecimalFormat.getInstance(Locale.US, CompactDecimalFormat.CompactStyle.SHORT).format(likeCount)

            Text(
                text = likes,
                modifier = Modifier.shadow(10.dp)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 22.dp, start = 10.dp),
        ) {
            Text(text = "Posted by", fontStyle = FontStyle.Italic, fontSize = 10.sp)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )

                Text(text = meme.userName)

            }
        }


    }

}

@Preview
@Composable
fun FeedPreview(){
    val memeList = listOf<MemeDTO>(MemeDTO(likeCount = 1000.0, userName = "Vishesh"))
    Surface{
        FeedScreen(
            state = FeedState(memeMap = emptyMap()),
            onEvent = {},
            navigateToProfile = {}
        )
    }
}