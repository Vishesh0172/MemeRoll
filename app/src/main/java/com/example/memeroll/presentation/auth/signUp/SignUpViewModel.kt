package com.example.memeroll.presentation.auth.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.UserDataRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl
) : ViewModel(){



    private val _state = MutableStateFlow(SignUpState())

    private val _emailState = MutableStateFlow(_state.value.email)
    private val _nameState = MutableStateFlow(_state.value.name)
    private val _passwordState = MutableStateFlow(_state.value.password)
    private val emailRegex = Regex("^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}\$")

    val state = combine(_emailState, _passwordState, _nameState, _state){ email, password, name, state ->

        val validEmail = email.matches(emailRegex)
        val validPassword = password.length > 5
        val validName = name.length > 1 && name.length < 15
        state.copy(
            email = email,
            password = password,
            name = name,
            validEmail = validEmail,
            validPassword = validPassword,
            validName = validName
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SignUpState())

    init {
        viewModelScope.launch{
            authRepository.isAuthenticated().collect{ sessionStatus ->
                _state.update { it.copy(sessionStatus = sessionStatus) }
            }
        }
    }

    fun onEvent(event: SignUpEvent){
        when(event){
            is SignUpEvent.EmailChange -> {
                _emailState.update { event.value.trim() }
            }
            is SignUpEvent.PasswordChange -> {
               _passwordState.update { event.value.trim() }
            }

            is SignUpEvent.NameChange -> {
               _nameState.update { event.value.trim() }
            }

            SignUpEvent.SignUpClick -> {
                viewModelScope.launch{
                    if (authRepository.signUp(state.value.email, state.value.password, state.value.name)){
                        val currentUserId = authRepository.getCurrentUser()!!.id
                        userRepository.createUser(state.value.name, currentUserId)
                    }else{
                        _state.update { it.copy(errorText = "Some Error Occurred") }
                    }
                }
            }


        }
    }
}