package com.mindtrainer.ui.screen.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindtrainer.ui.theme.MindTrainerTheme

@Composable
fun StroopScreen(onComplete: () -> Unit) {
    // Sample stimulus
    val word = "RED"
    val inkColor = Color(0xFF2980B9) // Blue
    val colorNames = listOf("RED", "BLUE", "GREEN", "YELLOW")
    val colorValues = listOf(
        Color(0xFFC0392B), // Red
        Color(0xFF2980B9), // Blue
        Color(0xFF27AE60), // Green
        Color(0xFFF39C12)  // Yellow
    )
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Stroop Challenge", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.onBackground)
        Text("Tap the button matching the INK COLOR", fontSize = 16.sp, color = MindTrainerTheme.colors.primary)
        
        // Stimulus
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MindTrainerTheme.colors.surfaceVariant)
        ) {
            Text(
                text = word,
                fontSize = 72.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = inkColor,
                textAlign = androidx.compose.ui.text.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp)
                    .align(Alignment.Center)
            )
        }
        
        // Color buttons
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colorNames.forEachIndexed { index, name ->
                Button(
                    onClick = { /* Handle response */ },
                    modifier = Modifier.weight(1f).height(60.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = colorValues[index]
                    )
                ) {
                    Text(name, color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, fontSize = 16.sp)
                }
            }
        }
        
        // Timer
        Text("Time: 30.0s", fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.primary)
        
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