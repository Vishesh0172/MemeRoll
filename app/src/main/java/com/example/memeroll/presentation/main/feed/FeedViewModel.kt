package com.example.memeroll.presentation.main.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.UserDataRepositoryImpl
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

                _state.update { it.copy(likedPostsIds = likedPostsIds) }
                val memes = withContext(Dispatchers.IO) { feedRepository.getFeedMeme(0) ?: emptyList() }
                _state.update { it.copy(memeList = memes) }

            }

        }
    }


    fun onEvent(event: FeedEvent) {

        viewModelScope.launch {
            when (event) {
                is FeedEvent.Liked -> {

                    async(Dispatchers.IO) { feedRepository.likeMeme(postId = event.postId) }
                    async(Dispatchers.IO) { userRepository.addToLikedPosts(postId = event.postId, userId = userId) }

                    val memeList = state.value.memeList.toMutableList()
                    val likedList = state.value.likedPostsIds.toMutableList()
                    memeList.let { list ->
                        val meme = list.find { it.id == event.postId }
                        val index = list.indexOf(meme)
                        list[index] = meme!!.copy(likeCount = meme.likeCount + 1)
                        likedList.add(event.postId)
                        _state.update { it.copy(memeList = list, likedPostsIds = likedList) }
                    }


                }

                is FeedEvent.Unliked -> {


                    async(Dispatchers.IO) { feedRepository.unlikeMeme(postId = event.postId) }
                    async(Dispatchers.IO) { userRepository.removeFromLikedPosts(postId = event.postId, userId = userId) }

                    val memeList = state.value.memeList.toMutableList()
                    val likedList = state.value.likedPostsIds.toMutableList()
                    memeList.let { list ->
                        val meme = list.find { it.id == event.postId }
                        val index = list.indexOf(meme)
                        list[index] = meme!!.copy(likeCount = meme.likeCount - 1)
                        likedList.remove(event.postId)
                        _state.update { it.copy(memeList = list, likedPostsIds = likedList) }
                    }


                }

                is FeedEvent.LimitReached -> {

                    Log.d("FeedViewModel","Limit Reached Called")

                    val memeList = state.value.memeList.toMutableList()
                    val nextMeme = getNextMeme(event.pageNumber.toLong())
                    nextMeme?.let {
                        memeList.add(it)
                    }
                    _state.update { it.copy(memeList = memeList) }

                }

                FeedEvent.Refreshed -> {

                    _state.update { it.copy(isRefreshing = true) }
                    val newMemeList = state.value.memeList.toMutableList()
                    newMemeList.clear()
                    newMemeList.addAll(feedRepository.getFeedMeme(0) ?: emptyList())
                    _state.update { it.copy(memeList = newMemeList, isRefreshing = false) }

                }
            }
        }
    }




    private suspend fun getNextMeme(pageNumber: Long): MemeDTO?{

        val meme = feedRepository.getFeedMeme(from = pageNumber + 1L)?.let {
            if (it.isNotEmpty())
                it.first()
            else
                null
        }

        return meme
    }


}