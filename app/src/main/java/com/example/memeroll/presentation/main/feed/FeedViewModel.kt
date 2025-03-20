package com.example.memeroll.presentation.main.feed 
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepository
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.addAll
import javax.inject.Inject


@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedDatabaseRepositoryImpl,
    private val authRepository: AuthRepositoryImpl
) : ViewModel(){

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch{
            _state.update { it.copy(memeList = feedRepository.getFeedMemes(0)) }
            authRepository.isAuthenticated().collect{ sessionStatus ->
                Log.d("Authenticated", sessionStatus.toString())
                _state.update { it.copy(sessionStatus = sessionStatus) }
            }
        }
    }

    fun onEvent(event: FeedEvent){
        when(event) {
            FeedEvent.LikeClicked -> TODO()
            FeedEvent.SignOut -> {
                Log.d("FeedEvent", "Sign Out Clicked")
                viewModelScope.launch{
                    authRepository.signOut()
                }
            }

            is FeedEvent.LimitReached -> {
                viewModelScope.launch{
                    val list = state.value.memeList.toMutableList()
                    list.addAll(feedRepository.getFeedMemes(from = event.pageNumber.toLong() + 1L))
                    _state.update { it.copy(memeList = list.toList()) }
                }

            }
        }
    }

}