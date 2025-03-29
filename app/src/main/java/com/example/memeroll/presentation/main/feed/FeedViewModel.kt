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
import kotlinx.coroutines.async
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
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()
    private lateinit var userId: String
    private lateinit var likedPostsIds: List<Int>


    init {

        Log.d("FeedViewModel", "init called")

        viewModelScope.launch {

            val user = withContext(Dispatchers.IO) {
                authRepository.getCurrentUser()
            }

            user?.let {
                userId = it.id
                likedPostsIds = withContext(Dispatchers.IO) {
                    userRepository.getUserById(it.id).likedPosts ?: emptyList()
                }
                val memeMap = withContext(Dispatchers.IO) { getMemeMap(0) }
                _state.update { it.copy(memeMap = memeMap) }

            }

        }
    }


    fun onEvent(event: FeedEvent) {

        viewModelScope.launch {
            when (event) {
                is FeedEvent.Liked -> {

                    async(Dispatchers.IO) { feedRepository.likeMeme(postId = event.postId) }
                    async(Dispatchers.IO) { userRepository.addToLikedPosts(postId = event.postId, userId = userId) }

                    val updateMemeMap = getUpdatedMemeMap(event.postId, liked = true)
                    _state.update { it.copy(memeMap = updateMemeMap) }

                }

                is FeedEvent.Unliked -> {


                    async(Dispatchers.IO) { feedRepository.unlikeMeme(postId = event.postId) }
                    async(Dispatchers.IO) { userRepository.removeFromLikedPosts(postId = event.postId, userId = userId) }

                    val updateMemeMap = getUpdatedMemeMap(event.postId, liked = false)
                    _state.update { it.copy(memeMap = updateMemeMap) }

                }

                is FeedEvent.LimitReached -> {

                    Log.d("FeedViewModel","Limit Reached Called")
                    val memeMap = state.value.memeMap.toMutableMap()
                    val memesFromNetwork =
                        withContext(Dispatchers.IO) { getMemeMap(from = event.pageNumber.toLong() + 1L) }
                    memeMap.putAll(memesFromNetwork)
                    _state.update { it.copy(memeMap = memeMap) }

                }


            }
        }
    }


    private fun getUpdatedMemeMap(postId: Int, liked: Boolean): Map<MemeDTO, Boolean> {

        val memeMap = state.value.memeMap.toMutableMap()
        val meme = memeMap.keys.find { it.id == postId }

        meme?.let {

            val newMeme = it.copy()
            if (liked)
                meme.likeCount = meme.likeCount + 1L
            else
                meme.likeCount = meme.likeCount - 1L

            memeMap[meme] = liked
        }

        return memeMap.toMap()
    }


    private suspend fun getMemeMap(from: Long): Map<MemeDTO, Boolean> {

        val memeMap = mutableMapOf<MemeDTO, Boolean>()

        val meme = feedRepository.getFeedMeme(from = from)?.let {
            if (it.isNotEmpty())
                it.first()
            else
                null
        }

        meme?.let {
            val liked = it.id in likedPostsIds
            memeMap.put(it, liked)
        }
        return memeMap
    }


}