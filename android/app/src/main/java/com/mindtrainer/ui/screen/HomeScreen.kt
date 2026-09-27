package com.mindtrainer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindtrainer.ui.theme.MindTrainerTheme

@Composable
fun HomeScreen(
    onExerciseClick: (String) -> Unit,
    onProgressClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = "Today",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = MindTrainerTheme.colors.onBackground
        )
        Text(
            text = "Your daily brain practice",
            fontSize = 16.sp,
            color = MindTrainerTheme.colors.onSurfaceVariant
        )

        // Exercise Cards
        ExerciseCard(
            title = "Schulte Tables",
            description = "Find numbers 1-25 in order. 30 seconds.",
            icon = "🔢",
            completed = false,
            onClick = { onExerciseClick("schulte") }
        )

        ExerciseCard(
            title = "Blindfold Writing",
            description = "Type with eyes closed. Focus on rhythm.",
            icon = "⌨️",
            completed = false,
            onClick = { onExerciseClick("blindfold") }
        )

        ExerciseCard(
            title = "Non-Dominant Hand",
            description = "Write, trace, tap with other hand.",
            icon = "✍️",
            completed = false,
            onClick = { onExerciseClick("nondominant") }
        )

        ExerciseCard(
            title = "Stroop Challenge",
            description = "Name the ink color, ignore the word.",
            icon = "🎨",
            completed = false,
            onClick = { onExerciseClick("stroop") }
        )

        // Footer actions
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onProgressClick,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MindTrainerTheme.colors.surfaceVariant
                )
            ) {
                Text("Progress", fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = onSettingsClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Settings", fontWeight = FontWeight.Medium)
            }
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ExerciseCard(
    title: String,
    description: String,
    icon: String,
    completed: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (completed) MindTrainerTheme.colors.surfaceVariant else MindTrainerTheme.colors.surface
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 32.sp)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text(text = description, fontSize = 14.sp, color = MindTrainerTheme.colors.onSurfaceVariant)
            }
            if (completed) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.CheckCircle,
                    contentDescription = "Completed",
                    tint = MindTrainerTheme.colors.primary
                )
            }
        }
    }
}