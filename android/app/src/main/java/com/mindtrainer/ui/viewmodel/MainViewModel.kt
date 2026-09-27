package com.mindtrainer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.mindtrainer.domain.model.*
import com.mindtrainer.domain.repository.ProgressRepository
import com.mindtrainer.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    data class MainUiState(
        val isAuthenticated: Boolean = false,
        val user: UserProfile? = null,
        val todayExercises: Map<String, Boolean> = mapOf(
            "schulte" to false,
            "blindfold" to false,
            "nondominant" to false,
            "stroop" to false
        ),
        val currentStreak: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class UserProfile(
        val id: String,
        val username: String,
        val email: String,
        val avatarUrl: String?,
        val language: String,
        val currentStreak: Int,
        val longestStreak: Int
    )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = userRepository.getProfile()
            _uiState.update {
                when (result) {
                    is Result.Success -> {
                        val profile = result.data
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            user = UserProfile(
                                id = profile.id.toString(),
                                username = profile.username,
                                email = profile.email,
                                avatarUrl = profile.avatarUrl,
                                language = profile.language,
                                currentStreak = profile.stats.currentStreak,
                                longestStreak = profile.stats.longestStreak
                            ),
                            todayExercises = loadTodayExercises()
                        )
                    }
                    is Result.Error -> it.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun loadTodayExercises(): Map<String, Boolean> {
        // This would be loaded from the server or local storage
        // For now, return default values
        return mapOf(
            "schulte" to false,
            "blindfold" to false,
            "nondominant" to false,
            "stroop" to false
        )
    }

    fun onExerciseCompleted(exerciseType: String) {
        _uiState.update { state ->
            state.copy(
                todayExercises = state.todayExercises + (exerciseType to true)
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.update { state ->
                state.copy(
                    isAuthenticated = false,
                    user = null,
                    todayExercises = mapOf(
                        "schulte" to false,
                        "blindfold" to false,
                        "nondominant" to false,
                        "stroop" to false
                    )
                )
            }
        }
    }

    fun refreshProgress() {
        viewModelScope.launch {
            val result = progressRepository.getDashboard()
            _uiState.update {
                when (result) {
                    is Result.Success -> {
                        val dashboard = result.data
                        val todayExercises = dashboard.todayStatus.associateBy(
                            { it.exerciseType.lowercase() },
                            { it.completed }
                        )
                        it.copy(
                            todayExercises = todayExercises,
                            currentStreak = dashboard.streaks.current
                        )
                    }
                    is Result.Error -> it.copy(error = result.message)
                }
            }
        }
    }
}
