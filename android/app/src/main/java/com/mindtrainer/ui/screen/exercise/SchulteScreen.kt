package com.mindtrainer.ui.screen.exercise

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.awaitPointerEventScope
import androidx.compose.ui.input.pointer.awaitFirstDown
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.OnGloballyPositionedModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindtrainer.di.hiltViewModel
import com.mindtrainer.ui.theme.MindTrainerTheme
import com.mindtrainer.ui.viewmodel.ExerciseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
@Composable
fun SchulteScreen(
    onComplete: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    var gridSize by remember { mutableStateOf(5) }
    var numbers by remember { mutableStateOf<IntArray>(intArrayOf()) }
    var targetNumber by remember { mutableStateOf(1) }
    var startTime by remember { mutableStateOf(0L) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var tappedSequence by remember { mutableStateOf<MutableList<Int>>(mutableListOf()) }
    var gridRect by remember { mutableStateOf(android.graphics.Rect()) }
    var cellSize by remember { mutableStateOf(0f) }
    var accuracy by remember { mutableStateOf(0f) }

    val uiState = viewModel.schulteUiState.collectAsStateWithLifecycle()

    // Initialize game
    androidx.compose.runtime.LaunchedEffect(Unit) {
        val daily = viewModel.getDailySchulte()
        gridSize = daily.size
        numbers = daily.numbers.toIntArray()
        cellSize = 1f / gridSize
    }

    // Timer
    androidx.compose.runtime.LaunchedEffect(isRunning) {
        if (isRunning && !isCompleted) {
            while (isRunning && !isCompleted) {
                val elapsed = SystemClock.uptimeMillis() - startTime
                elapsedTime = elapsed
                if (elapsed >= 30000) { // 30 second limit
                    finishGame()
                }
                kotlinx.coroutines.delay(16) // ~60fps
            }
        }
    }

    fun finishGame() {
        isRunning = false
        isCompleted = true
        
        val correct = tappedSequence.filterIndexed { index, num ->
            index < numbers.size && num == numbers[index]
        }.size
        accuracy = if (numbers.isNotEmpty()) correct.toFloat() / numbers.size else 0f
        
        val completionTime = SystemClock.uptimeMillis() - startTime
        viewModel.submitSchulte(
            seed = 0, // Would come from daily
            size = gridSize,
            timeLimitMs = 30000,
            completionTimeMs = completionTime,
            tappedSequence = tappedSequence,
            accuracy = accuracy
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Schulte Tables",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MindTrainerTheme.colors.onBackground
        )

        // Timer and target
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(
                text = "Time: ${(elapsedTime / 1000).toString().padStart(2, '0')}.${((elapsedTime % 1000) / 10).toString().padStart(2, '0')}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = if (elapsedTime > 25000) Color.Red else MindTrainerTheme.colors.primary
            )
            Text(
                text = "Find: $targetNumber",
                fontSize = 18.sp,
                color = MindTrainerTheme.colors.onSurfaceVariant
            )
        }

        // Grid Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .onGloballyPositioned { coords ->
                    gridRect = android.graphics.Rect(
                        0, 0, coords.size.width, coords.size.height
                    )
                    cellSize = coords.size.width.toFloat() / gridSize
                }
        ) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier.fillMaxSize(),
                onDraw = {
                    drawGrid(it)
                }
            )
            
            // Touch handling
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitFirstDown()
                        if (isRunning && !isCompleted) {
                            val x = event.position.x
                            val y = event.position.y
                            handleTap(x, y)
                        }
                    }
                }
            }
        }

        if (isCompleted) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MindTrainerTheme.colors.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Complete!", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = MindTrainerTheme.colors.primary)
                    Text("Time: ${String.format("%.2f", completionTime / 1000.0)}s", fontSize = 18.sp)
                    Text("Accuracy: ${String.format("%.0f", accuracy * 100)}%", fontSize = 18.sp)
                    Text("Score: ${viewModel.lastSchulteScore}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Button(
                onClick = { onComplete() },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MindTrainerTheme.colors.primary)
            ) {
                Text("Finish", fontWeight = FontWeight.Medium)
            }
        } else if (!isRunning) {
            Button(
                onClick = {
                    startTime = SystemClock.uptimeMillis()
                    isRunning = true
                    targetNumber = 1
                    tappedSequence.clear()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MindTrainerTheme.colors.primary)
            ) {
                Text("Start (30 seconds)", fontWeight = FontWeight.Medium)
            }
        }
    }

    fun handleTap(x: Float, y: Float) {
        val col = (x / cellSize).toInt().coerceIn(0, gridSize - 1)
        val row = (y / cellSize).toInt().coerceIn(0, gridSize - 1)
        val index = row * gridSize + col
        
        if (index < numbers.size) {
            val tappedNumber = numbers[index]
            tappedSequence.add(tappedNumber)
            
            if (tappedNumber == targetNumber) {
                targetNumber++
                if (targetNumber > numbers.size) {
                    finishGame()
                }
            }
        }
    }

    fun drawGrid(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val cellW = width / gridSize
        val cellH = height / gridSize
        
        val paint = Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        
        // Draw grid lines
        paint.color = Color.parseColor("#E0E4EB")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        
        for (i in 1 until gridSize) {
            canvas.drawLine(i * cellW, 0f, i * cellW, height.toFloat(), paint)
            canvas.drawLine(0f, i * cellH, width.toFloat(), i * cellH, paint)
        }
        
        // Draw numbers
        paint.style = Paint.Style.FILL
        paint.textSize = (min(cellW, cellH) * 0.6).coerceAtLeast(12f)
        
        for (i in numbers.indices) {
            val row = i / gridSize
            val col = i % gridSize
            val cx = (col + 0.5) * cellW
            val cy = (row + 0.5) * cellH + paint.textSize / 3
            
            val number = numbers[i]
            val isTapped = tappedSequence.contains(number)
            val isCurrentTarget = number == targetNumber
            
            // Color coding
            when {
                isTapped -> paint.color = Color.parseColor("#888888") // Subtle gray for tapped
                isCurrentTarget -> paint.color = MindTrainerTheme.colors.primary.toArgb()
                else -> paint.color = MindTrainerTheme.colors.onBackground.toArgb()
            }
            
            canvas.drawText(number.toString(), cx, cy, paint)
        }
    }
}