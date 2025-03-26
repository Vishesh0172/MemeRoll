package com.example.memeroll.presentation.main.feed 
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import com.example.memeroll.model.MemeDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedDatabaseRepositoryImpl,
    private val authRepository: AuthRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl
) : ViewModel(){

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()
    private lateinit var userId : String


    init {

        Log.d("FeedViewModel", "init called")

        viewModelScope.launch{

//            launch{
//                authRepository.isAuthenticated()
//                    .flowOn(Dispatchers.IO)
//                    .collect{ sessionStatus ->
//                    Log.d("Authenticated", sessionStatus.toString())
//                    _state.update { it.copy(sessionStatus = sessionStatus) }
//                }
//            }

            val user = withContext(Dispatchers.IO){
                authRepository.getCurrentUser()
            }

            user?.let {
                userId  = it.id
                val memeMap = withContext(Dispatchers.IO){ getMemeMap(0) }
                _state.update { it.copy(memeMap = memeMap) }

            }

        }
    }

    fun onEvent(event: FeedEvent){
        Log.d("FeedViewModel", "Event Received: $event")
        when(event) {

            is FeedEvent.Liked -> {
                viewModelScope.launch {
                    Log.d("FeedViewModel", "onEvent for Like Clicked called")
                    feedRepository.likeMeme(postId = event.postId)
                    userRepository.addToLikedPosts(postId = event.postId, userId = userId)
                    val memeMap = state.value.memeMap.toMutableMap()
                    val meme = memeMap.keys.find { it.id == event.postId }!!
                    meme.likeCount = meme.likeCount + 1L
                    Log.d("FeedViewModel", meme.likeCount.toString())
                    memeMap[meme] = true
                    _state.update { it.copy(
                        memeMap = memeMap
                    ) }
                }
            }

            is FeedEvent.Unliked -> {
                viewModelScope.launch {
                    Log.d("FeedViewModel", "onEvent for Unliked called")
                    feedRepository.unlikeMeme(postId = event.postId)
                    userRepository.removeFromLikedPosts(postId = event.postId, userId = userId)
                    val memeMap = state.value.memeMap.toMutableMap()
                    val meme = memeMap.keys.find { it.id == event.postId }!!
                    meme.likeCount = meme.likeCount - 1L
                    Log.d("FeedViewModel", meme.likeCount.toString())
                    memeMap[meme] = false
                    _state.update { it.copy(
                        memeMap = memeMap
                    ) }
                }
            }



            is FeedEvent.LimitReached -> {
                viewModelScope.launch{
                    Log.d("FeedViewModel", "onEvent for LimitReached called")
                    val memeMap = state.value.memeMap.toMutableMap()
                    val memesFromNetwork = withContext(Dispatchers.IO){ getMemeMap(from = event.pageNumber.toLong() + 1L) }
                    memeMap.putAll(memesFromNetwork)
                    _state.update { it.copy(memeMap = memeMap) }
                }

            }


        }
    }

    private suspend fun getMemeMap(from: Long): Map<MemeDTO, Boolean>{

        val memeMap = mutableMapOf<MemeDTO, Boolean>()
        val memeList = feedRepository.getFeedMemes(from = from)
        for (meme in memeList){
            val liked = userRepository.checkIfLiked(meme.id!!, userId)
            memeMap.put(meme, liked)
        }
        return memeMap
    }


}