package com.example.memeroll.presentation.auth.signUp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.memeroll.presentation.auth.components.CustomEmailTextField
import com.example.memeroll.presentation.auth.components.CustomPasswordTextField
import com.example.memeroll.presentation.auth.signUp.SignUpEvent.*
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, onEvent:(SignUpEvent) -> Unit, state: SignUpState, authComplete:() -> Unit) {

    LaunchedEffect(state.sessionStatus) {
        if (state.sessionStatus is SessionStatus.Authenticated){
            authComplete()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Sign Up Now")

        TextField(
            value = state.name,
            onValueChange = {onEvent(NameChange(it))},
            shape = RoundedCornerShape(15.dp),
            modifier = modifier.height(100.dp).fillMaxWidth().padding(20.dp),
            placeholder = {Text("Name")}
        )


        CustomEmailTextField(
            modifier = Modifier.height(100.dp).padding(20.dp),
            onEvent = { onEvent(EmailChange(it)) },
            value = state.email
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomPasswordTextField(
            modifier = Modifier.height(100.dp).padding(20.dp),
            onEvent = {onEvent(PasswordChange(it))},
            value = state.password
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onEvent(SignUpClick) }) { Text("Sign Up") }

    }
    
}