package com.example.memeroll.presentation.main.post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.StorageRepository
import com.example.memeroll.data.StorageRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.model.UserDTO
import com.example.memeroll.presentation.navigation.PostRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageRepository: StorageRepositoryImpl,
    private val authRepositoryImpl: AuthRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl,
    private val feedRepository: FeedDatabaseRepositoryImpl
) : ViewModel() {

    private val _state = MutableStateFlow(PostState())
    val state = _state.asStateFlow()
    private val uriString = savedStateHandle.toRoute<PostRoute>().uriString
    private val uri = Uri.parse(uriString)
    private lateinit var user: UserDTO

    init {
        viewModelScope.launch {
            _state.update { it.copy(uri = uri) }
            user = UserDTO(
                userId = authRepositoryImpl.getCurrentUser()!!.id,
                userName = userRepository.getUserById(authRepositoryImpl.getCurrentUser()!!.id).userName,
            )
        }
    }

    fun onEvent(event: PostEvent) {
        when(event){
            is PostEvent.PostMeme -> {
                viewModelScope.launch {
                    val imgUrl = storageRepository.addImage(uri = uri, userId = user.userId)
                    if (imgUrl !=null) {
                        val meme = MemeDTO(imgUrl = imgUrl, userName = user.userName)
                        val postId = feedRepository.addMeme(meme)
                        if (postId != null)
                            userRepository.addUserPost(userId = user.userId, postId = postId)
                    }
                    Log.d(this@PostViewModel.toString(), "imgUrl is: $imgUrl")

                }
            }
        }

    }

}