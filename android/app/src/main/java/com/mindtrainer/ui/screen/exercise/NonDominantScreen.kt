package com.mindtrainer.ui.screen.exercise

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
fun NonDominantScreen(onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Non-Dominant Hand", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.onBackground)
        
        // Task selector
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MindTrainerTheme.colors.surfaceVariant)
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select Task", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                Text("Copywriting", fontSize = 16.sp)
                Text("Shape Tracing", fontSize = 16.sp)
                Text("Tap Sequence", fontSize = 16.sp)
            }
        }
        
        // Task-specific UI would go here
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MindTrainerTheme.colors.primary)
        ) {
            Text("Finish", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }
    }
}