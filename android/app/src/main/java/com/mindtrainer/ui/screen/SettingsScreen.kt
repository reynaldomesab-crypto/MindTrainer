package com.mindtrainer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindtrainer.ui.theme.MindTrainerTheme

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MindTrainerTheme.colors.onBackground)
        
        // TODO: Implement settings sections
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
    }
}