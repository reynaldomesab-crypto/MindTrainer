package com.mindtrainer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

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
            // TODO: Load from repository
            // Simulate loading
            try {
                Thread.sleep(500)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true, // For demo
                        user = UserProfile(
                            id = "demo-user",
                            username = "demo",
                            email = "demo@mindtrainer.app",
                            avatarUrl = null,
                            language = "en",
                            currentStreak = 7,
                            longestStreak = 14
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onExerciseCompleted(exerciseType: String) {
        _uiState.update { state ->
            state.copy(
                todayExercises = state.todayExercises + (exerciseType to true)
            )
        }
    }

    fun logout() {
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