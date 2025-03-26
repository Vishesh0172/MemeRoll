package com.example.memeroll.presentation.main.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(

    userDataRepository: UserDataRepositoryImpl,
    private val authRepository: AuthRepositoryImpl,
    feedRepository: FeedDatabaseRepositoryImpl,

) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
    //private lateinit var user: UserDTO

    init {

        Log.d("ProfileViewModel", "init called")

        viewModelScope.launch {

            launch{
                authRepository.isAuthenticated().collect { sessionStatus ->
                    Log.d("Authenticated", sessionStatus.toString())
                    _state.update { it.copy(sessionStatus = sessionStatus) }
                }

            }

            val userDeferred = async(Dispatchers.IO){
                authRepository.getCurrentUser()?.let {
                    userDataRepository.getUserById(it.id)
                }
            }

            val user = userDeferred.await()

            user?.let {
                val userPosts = user.posts ?: emptyList<Int>()
                val userName = user.userName
                _state.update { it.copy(userName = userName, numberOfPosts = userPosts.size) }

                feedRepository.getMemesByIds(userPosts)
                    .flowOn(Dispatchers.IO)
                    .collect {
                        val userPostsList = state.value.posts.toMutableList()
                        userPostsList.addAll(it)
                        Log.d("UserPostFlow", "Collected ${it.size} Memes: $it")
                        _state.update { it.copy(posts = userPostsList) }
                    }
            }


        }

    }


    fun onEvent(event: ProfileEvent) {
        when(event){

            ProfileEvent.SignOut -> {
                viewModelScope.launch(Dispatchers.IO){
                    authRepository.signOut()
                }
            }

            else -> {}
        }
    }


}