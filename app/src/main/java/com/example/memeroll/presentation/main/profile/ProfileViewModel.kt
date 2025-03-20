package com.example.memeroll.presentation.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    userDataRepository: UserDataRepositoryImpl,
    authRepository: AuthRepositoryImpl
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch{
            val user = userDataRepository.getUserById(authRepository.getCurrentUser()!!.id)
            val userName = user.userName
            val userPosts = userDataRepository.getUserPosts(memeIds = user.posts ?: emptyList<Int>())

            _state.update { it.copy(userName = userName, posts = userPosts) }
        }

    }

    fun onEvent(event: ProfileEvent) {

    }

}