package com.example.memeroll.presentation.main.post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.StorageRepository
import com.example.memeroll.data.StorageRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.model.UserDTO
import com.example.memeroll.presentation.navigation.PostRoute
import com.example.memeroll.work.MemeUploaderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(

) : ViewModel() {

    private val _state = MutableStateFlow(PostState())
    val state = _state.asStateFlow()


    fun onEvent(event: PostEvent) {
        when(event){
            PostEvent.PostMeme -> TODO()
        }

    }

}