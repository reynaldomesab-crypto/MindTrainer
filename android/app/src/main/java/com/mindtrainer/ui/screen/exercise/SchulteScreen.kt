package com.mindtrainer.ui.screen.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
fun SchulteScreen(onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Schulte Tables", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.onBackground)
        Text("Find numbers 1-25 in ascending order", fontSize = 16.sp, color = MindTrainerTheme.colors.onSurfaceVariant)
        
        // TODO: Implement 5x5 grid with Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .background(MindTrainerTheme.colors.surfaceVariant)
        ) {
            Text("Grid Canvas Here", color = MindTrainerTheme.colors.onSurfaceVariant)
        }
        
        Text("Time: 30.00s", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.primary)
        Text("Find: 1", fontSize = 18.sp, color = MindTrainerTheme.colors.onSurfaceVariant)
        
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