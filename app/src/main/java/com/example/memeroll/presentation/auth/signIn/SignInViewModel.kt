package com.example.memeroll.presentation.auth.signIn

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl
): ViewModel() {

    private val _state = MutableStateFlow(SignInState())

    private val _emailState = MutableStateFlow(_state.value.email)
    private val _passwordState = MutableStateFlow(_state.value.password)
    private val emailRegex = Regex("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}\$")
    val state = combine(_emailState, _passwordState, _state){ email, password, state ->

        val validEmail = email.matches(emailRegex)
        val validPassword = password.length > 5
        Log.d("SignInViewModel", "$email is valid email?: $validEmail")
        Log.d("SignInViewModel", "$password is valid password?: $validPassword")
        state.copy(email = email, password = password, validEmail = validEmail, validPassword = validPassword)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SignInState())

    init {

        Log.d("SignInViewModel", "init called")

        viewModelScope.launch{
            authRepository.isAuthenticated()
                .flowOn(Dispatchers.IO)
                .collect { sessionStatus ->
                    _state.update { it.copy(sessionStatus = sessionStatus) }
                }


        }
    }

    fun onEvent(event: SignInEvent){
        when(event){
            is SignInEvent.EmailChange -> {
                //_state.update { it.copy(email = event.value) }
                _emailState.update { event.value.trim() }
            }
            is SignInEvent.PasswordChange -> {
                //_state.update { it.copy(password = event.value) }
                _passwordState.update { event.value.trim() }
            }
            SignInEvent.SignInClick -> {
                _state.update { it.copy(errorText = "") }
                viewModelScope.launch {
                    if (!authRepository.signIn(state.value.email, state.value.password)){
                        _state.update { it.copy(errorText = "Invalid Credentials") }
                    }
                }
            }

        }
    }
}