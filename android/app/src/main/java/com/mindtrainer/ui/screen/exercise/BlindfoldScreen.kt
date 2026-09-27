package com.mindtrainer.ui.screen.exercise

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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.unit.dp
import androidx.compose.unit.sp
import com.mindtrainer.ui.theme.MindTrainerTheme

@Composable
fun BlindfoldScreen(onComplete: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    val targetText = "The quick brown fox jumps over the lazy dog. This is a sample text for blindfold writing practice."
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Blindfold Writing", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.onBackground)
        
        // Target text (hidden during actual exercise)
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MindTrainerTheme.colors.surfaceVariant)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = targetText,
                    fontSize = 16.sp,
                    maxLines = 5,
                    overflow = androidx.compose.ui.text.overflow.TextOverflow.Ellipsis
                )
            }
        }
        
        Text("Close your eyes and type the text above", fontSize = 14.sp, color = MindTrainerTheme.colors.primary)
        
        // Input field
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Your input") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            visualTransformation = VisualTransformation.None
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MindTrainerTheme.colors.primary)
        ) {
            Text("Done", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }
    }
}