package com.example.memeroll.presentation.auth.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.memeroll.R

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

    var passwordVisible by remember { mutableStateOf(false) }
    val icon = if (passwordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24

    TextField(
        value = value,
        onValueChange = {onEvent(it)},
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.fillMaxSize(),
        placeholder = {Text("Password")},
        trailingIcon = {
            IconButton(onClick = {passwordVisible = !passwordVisible}) { Icon(painter = painterResource(icon), contentDescription = null) }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}


