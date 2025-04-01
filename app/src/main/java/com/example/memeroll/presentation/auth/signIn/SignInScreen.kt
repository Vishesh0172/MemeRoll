package com.example.memeroll.presentation.auth.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memeroll.presentation.auth.components.CustomEmailTextField
import com.example.memeroll.presentation.auth.components.CustomPasswordTextField
import com.example.memeroll.presentation.auth.signIn.SignInEvent.*
import com.example.memeroll.ui.theme.MemeRollTheme
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
            modifier = modifier.fillMaxSize().padding(top = 30.dp, start = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {

            Text(text = "MemeRoll", style = MaterialTheme.typography.displayMedium)

            Text(text = "Sign In Now", fontSize = 45.sp, modifier = Modifier.padding(top = 36.dp))

            CustomEmailTextField(
                modifier = Modifier
                    .height(80.dp)
                    .padding(top = 30.dp, end = 24.dp),
                onEvent = {onEvent(EmailChange(it))},
                value = state.email
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomPasswordTextField(
                modifier = Modifier
                    .height(80.dp)
                    .padding(top = 24.dp, end = 24.dp),
                onEvent = { onEvent(PasswordChange(it)) },
                value = state.password
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { onEvent(SignInClick) }, enabled = state.validEmail && state.validPassword) { Text("Sign In") }
            Text(state.errorText, fontSize = 12.sp, color = Color.Red)
            TextButton(onClick = {navigateToSignUp()}) { Text("or Sign Up") }

        }
    else
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
}



@Preview
@Composable
fun SignInPreview(){
    MemeRollTheme {
        SignInScreen(
            onEvent = {},
            state = SignInState(sessionStatus = SessionStatus.NotAuthenticated(true)),
            authComplete = {},
            navigateToSignUp = {}
        )
    }
}
