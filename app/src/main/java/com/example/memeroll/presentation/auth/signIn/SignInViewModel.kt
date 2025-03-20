package com.example.memeroll.presentation.auth.signIn

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl
): ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    init {
        Log.d("Authentication", authRepository.toString())
        viewModelScope.launch{
            authRepository.isAuthenticated().collect{ sessionStatus ->
                _state.update { it.copy(sessionStatus = sessionStatus) }
            }
        }
    }

    fun onEvent(event: SignInEvent){
        when(event){
            is SignInEvent.EmailChange -> {
                _state.update { it.copy(email = event.value) }
            }
            is SignInEvent.PasswordChange -> {
                _state.update { it.copy(password = event.value) }
            }
            SignInEvent.SignInClick -> {
                viewModelScope.launch {
                    authRepository.signIn(state.value.email, state.value.password)
                }
            }

        }
    }
}