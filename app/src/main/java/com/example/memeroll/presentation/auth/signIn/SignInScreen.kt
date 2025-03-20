package com.example.memeroll.presentation.auth.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memeroll.presentation.auth.components.CustomEmailTextField
import com.example.memeroll.presentation.auth.components.CustomPasswordTextField
import com.example.memeroll.presentation.auth.signIn.SignInEvent.*
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun SignInScreen(modifier: Modifier = Modifier, onEvent: (SignInEvent) -> Unit, state: SignInState, authComplete:() -> Unit, navigateToSignUp:() -> Unit) {

    LaunchedEffect(state.sessionStatus) {
        if (state.sessionStatus is SessionStatus.Authenticated){
            authComplete()
        }
    }

    if (state.sessionStatus is SessionStatus.NotAuthenticated)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Sign In Now")

            CustomEmailTextField(
                modifier = Modifier
                    .height(100.dp)
                    .padding(20.dp),
                onEvent = {onEvent(EmailChange(it))},
                value = state.email
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomPasswordTextField(
                modifier = Modifier
                    .height(100.dp)
                    .padding(20.dp),
                onEvent = { onEvent(PasswordChange(it)) },
                value = state.password
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { onEvent(SignInClick) }) { Text("Sign In") }
            TextButton(onClick = {navigateToSignUp()}) { Text("or Sign Up") }

        }
    else
        CircularProgressIndicator()
}



@Preview
@Composable
fun SignInPreview(){
    Surface {
        SignInScreen(
            onEvent = {},
            state = SignInState(),
            authComplete = {},
            navigateToSignUp = {}
        )
    }
}
