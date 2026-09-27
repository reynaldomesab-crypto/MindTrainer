package com.mindtrainer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindtrainer.ui.theme.MindTrainerTheme

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
        
        Text("Welcome to MindTrainer", fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.onBackground)
        Text("We'll help you set up your daily brain practice", fontSize = 16.sp, color = MindTrainerTheme.colors.onSurfaceVariant)
        
        // Steps would go here
        
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MindTrainerTheme.colors.primary)
        ) {
            Text("Get Started", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
    }
}