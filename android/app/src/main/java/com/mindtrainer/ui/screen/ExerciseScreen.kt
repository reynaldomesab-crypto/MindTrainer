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
fun ExerciseScreen(
    exerciseType: String,
    onComplete: () -> Unit
) {
    val (title, description) = when (exerciseType) {
        "schulte" -> "Schulte Tables" to "Find numbers 1-25 in order. 30 seconds."
        "blindfold" -> "Blindfold Writing" to "Type with eyes closed. Focus on rhythm."
        "nondominant" -> "Non-Dominant Hand" to "Write, trace, tap with other hand."
        "stroop" -> "Stroop Challenge" to "Name the ink color, ignore the word."
        else -> exerciseType to "Exercise"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
        
        Text(title, fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.onBackground)
        Text(description, fontSize = 16.sp, color = MindTrainerTheme.colors.onSurfaceVariant)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(24.dp))
        
        Button(
            onClick = { /* TODO: Start exercise */ },
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MindTrainerTheme.colors.primary)
        ) {
            Text("Start Exercise", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
    }
}