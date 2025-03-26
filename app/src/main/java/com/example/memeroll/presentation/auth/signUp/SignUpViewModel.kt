package com.example.memeroll.presentation.auth.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import com.example.memeroll.model.UserDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl
) : ViewModel(){

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

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
                _state.update { it.copy(email = event.value) }
            }
            is SignUpEvent.PasswordChange -> {
                _state.update { it.copy(password = event.value) }
            }

            is SignUpEvent.NameChange -> {
                _state.update { it.copy(name = event.value) }
            }

            SignUpEvent.SignUpClick -> {
                viewModelScope.launch{
                    if (authRepository.signUp(state.value.email, state.value.password, state.value.name)){
                        val currentUserId = authRepository.getCurrentUser()!!.id
                        userRepository.createUser(state.value.name, currentUserId)
                    }
                }
            }


        }
    }
}