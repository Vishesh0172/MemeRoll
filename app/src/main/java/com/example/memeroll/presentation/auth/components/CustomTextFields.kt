package com.example.memeroll.presentation.auth.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CustomEmailTextField(modifier: Modifier = Modifier, onEvent:(String) -> Unit, value: String) {
    TextField(
        value = value,
        onValueChange = {onEvent(it)},
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.fillMaxSize(),
        placeholder = {Text("E-mail")}
    )
}

@Composable
fun CustomPasswordTextField(modifier: Modifier = Modifier, onEvent:(String) -> Unit, value: String) {
    TextField(
        value = value,
        onValueChange = {onEvent(it)},
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.fillMaxSize(),
        placeholder = {Text("Password")},
        visualTransformation = PasswordVisualTransformation()
    )
}


