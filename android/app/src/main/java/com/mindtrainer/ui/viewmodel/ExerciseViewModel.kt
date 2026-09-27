package com.mindtrainer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor() : ViewModel() {

    // Schulte
    private val _schulteUiState = MutableStateFlow(SchulteUiState())
    val schulteUiState = _schulteUiState.asStateFlow()

    data class SchulteUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val daily: SchulteDaily? = null,
        val lastScore: Int = 0,
        val lastAccuracy: Float = 0f
    )

    data class SchulteDaily(
        val seed: Long,
        val size: Int,
        val numbers: List<Int>,
        val timeLimitMs: Long
    )

    var lastSchulteScore = 0

    fun getDailySchulte(): SchulteDaily {
        // TODO: Call repository
        return SchulteDaily(
            seed = 1001,
            size = 5,
            numbers = (1..25).toList().shuffled(),
            timeLimitMs = 30000
        )
    }

    fun submitSchulte(
        seed: Long,
        size: Int,
        timeLimitMs: Long,
        completionTimeMs: Long,
        tappedSequence: List<Int>,
        accuracy: Float
    ) {
        viewModelScope.launch {
            _schulteUiState.update { it.copy(isLoading = true) }
            // TODO: Call repository
            val score = (1000 * accuracy * (1 - completionTimeMs / 30000f)).toInt()
            lastSchulteScore = score
            _schulteUiState.update { it.copy(isLoading = false, lastScore = score, lastAccuracy = accuracy) }
        }
    }

    // Blindfold
    private val _blindfoldUiState = MutableStateFlow(BlindfoldUiState())
    val blindfoldUiState = _blindfoldUiState.asStateFlow()

    data class BlindfoldUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val daily: BlindfoldDaily? = null,
        val lastScore: Int = 0,
        val lastRmsError: Float = 0f
    )

    data class BlindfoldDaily(
        val id: String,
        val text: String,
        val tier: Int,
        val charCount: Int
    )

    fun getDailyBlindfold(): BlindfoldDaily {
        return BlindfoldDaily(
            id = "en_bf_001",
            text = "The quick brown fox jumps over the lazy dog. This pangram contains every letter of the alphabet.",
            tier = 1,
            charCount = 94
        )
    }

    fun submitBlindfold(
        textId: String,
        actualText: String,
        keyboardLayout: String,
        keyDistances: List<Float>,
        rmsError: Float,
        timeMs: Long
    ) {
        viewModelScope.launch {
            // TODO: Call repository
        }
    }

    // Non-Dominant
    private val _nonDomUiState = MutableStateFlow(NonDomUiState())
    val nonDomUiState = _nonDomUiState.asStateFlow()

    data class NonDomUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val daily: NonDomDaily? = null,
        val lastScore: Int = 0
    )

    data class NonDomDaily(
        val copywriting: BlindfoldDaily,
        val tracing: TracingPath,
        val tapping: TapSequence
    )

    data class TracingPath(
        val id: String,
        val type: String,
        val points: List<Point>,
        val targetTimeMs: Long,
        val difficulty: Float
    )

    data class Point(val x: Float, val y: Float)

    data class TapSequence(
        val id: String,
        val gridSize: Int,
        val targets: List<Int>,
        val count: Int,
        val timeLimitMs: Long
    )

    fun getDailyNonDom(): NonDomDaily {
        return NonDomDaily(
            copywriting = BlindfoldDaily("en_nd_001", "Sample text for non-dominant hand practice.", 1, 50),
            tracing = TracingPath("path_01", "SPIRAL", listOf(), 8000, 0.3f),
            tapping = TapSequence("seq_01", 3, listOf(0, 4, 8, 2, 6), 5, 15000)
        )
    }

    // Stroop
    private val _stroopUiState = MutableStateFlow(StroopUiState())
    val stroopUiState = _stroopUiState.asStateFlow()

    data class StroopUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val daily: StroopDaily? = null,
        val lastScore: Int = 0
    )

    data class StroopDaily(
        val mode: String,
        val config: StroopConfig,
        val stimuli: List<StroopStimulus>
    )

    data class StroopConfig(
        val colorCount: Int,
        val incongruentRatio: Float,
        val stimulusDurationMs: Int,
        val switchFrequency: Float,
        val sequenceLength: Int
    )

    data class StroopStimulus(
        val word: String,
        val inkColor: String,
        val condition: String,
        val position: String,
        val taskType: String?,
        val isSwitchTrial: Boolean,
        val isTarget: Boolean,
        val stimulusDurationMs: Int
    )

    fun getDailyStroop(mode: String = "CLASSIC"): StroopDaily {
        return StroopDaily(
            mode = mode,
            config = StroopConfig(4, 0.5f, 1000, 0.3f, 3),
            stimuli = listOf()
        )
    }
}