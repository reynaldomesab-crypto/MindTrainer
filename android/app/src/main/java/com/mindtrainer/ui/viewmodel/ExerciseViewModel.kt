package com.mindtrainer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.repository.ExerciseRepository
import com.mindtrainer.domain.repository.ProgressRepository
import com.mindtrainer.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository
) : ViewModel() {

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

    var lastSchulteScore = 0

    fun getDailySchulte(size: Int = 5) {
        viewModelScope.launch {
            _schulteUiState.update { it.copy(isLoading = true, error = null) }
            val result = exerciseRepository.getSchulteDaily(size)
            _schulteUiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, daily = result.data)
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
        }
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
            _schulteUiState.update { it.copy(isLoading = true, error = null) }
            val config = SchulteTableConfig(
                seed = seed,
                size = size,
                timeLimitMs = timeLimitMs.toInt(),
                showNumbers = true
            )
            val request = SchulteSubmitRequest(
                config = config,
                completionTimeMs = completionTimeMs,
                tappedSequence = tappedSequence,
                accuracy = accuracy.toDouble()
            )
            val result = exerciseRepository.submitSchulte(request)
            _schulteUiState.update {
                when (result) {
                    is Result.Success -> {
                        lastSchulteScore = result.data.score
                        it.copy(isLoading = false, lastScore = result.data.score, lastAccuracy = result.data.accuracy.toFloat())
                    }
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
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

    fun getDailyBlindfold(language: String = "en") {
        viewModelScope.launch {
            _blindfoldUiState.update { it.copy(isLoading = true, error = null) }
            val result = exerciseRepository.getBlindfoldDaily(language)
            _blindfoldUiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, daily = result.data)
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
        }
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
            _blindfoldUiState.update { it.copy(isLoading = true, error = null) }
            val request = BlindfoldSubmitRequest(
                textId = textId,
                actualText = actualText,
                keyboardLayout = keyboardLayout,
                keyDistances = keyDistances.map { it.toDouble() },
                rmsError = rmsError.toDouble(),
                timeMs = timeMs
            )
            val result = exerciseRepository.submitBlindfold(request)
            _blindfoldUiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, lastScore = result.data.score, lastRmsError = result.data.rmsError.toFloat())
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
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

    fun getDailyNonDom(language: String = "en") {
        viewModelScope.launch {
            _nonDomUiState.update { it.copy(isLoading = true, error = null) }
            val result = exerciseRepository.getNonDomDaily(language)
            _nonDomUiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, daily = result.data)
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun submitNonDom(
        taskType: String,
        contentId: String,
        handUsed: String,
        metrics: Map<String, Float>
    ) {
        viewModelScope.launch {
            _nonDomUiState.update { it.copy(isLoading = true, error = null) }
            val request = NonDomSubmitRequest(
                taskType = taskType,
                contentId = contentId,
                handUsed = handUsed,
                metrics = metrics.mapValues { (_, v) -> v.toDouble() }
            )
            val result = exerciseRepository.submitNonDom(request)
            _nonDomUiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, lastScore = result.data.score)
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
        }
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

    fun getDailyStroop(mode: String = "CLASSIC", language: String = "en") {
        viewModelScope.launch {
            _stroopUiState.update { it.copy(isLoading = true, error = null) }
            val result = exerciseRepository.getStroopDaily(mode, language)
            _stroopUiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, daily = result.data)
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun submitStroop(
        mode: String,
        trials: List<StroopTrialRequest>
    ) {
        viewModelScope.launch {
            _stroopUiState.update { it.copy(isLoading = true, error = null) }
            val request = StroopSubmitRequest(
                mode = mode,
                trials = trials
            )
            val result = exerciseRepository.submitStroop(request)
            _stroopUiState.update {
                when (result) {
                    is Result.Success -> it.copy(isLoading = false, lastScore = result.data.compositeScore)
                    is Result.Error -> it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}
