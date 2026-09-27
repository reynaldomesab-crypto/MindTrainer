package com.mindtrainer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindtrainer.ui.theme.MindTrainerTheme

@Composable
fun AuthScreen(onAuthenticated: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
        
        Text("MindTrainer", fontSize = 32.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.primary)
        Text(if (isLogin) "Welcome back" else "Create your account", fontSize = 16.sp, color = MindTrainerTheme.colors.onSurfaceVariant)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

        if (!isLogin) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = androidx.compose.material3.PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            onClick = { onAuthenticated() },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MindTrainerTheme.colors.primary)
        ) {
            Text(if (isLogin) "Log In" else "Create Account", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }

        Text(
            text = if (isLogin) "Don't have an account? Sign up" else "Already have an account? Log in",
            fontSize = 14.sp,
            color = MindTrainerTheme.colors.onSurfaceVariant
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
    }
}