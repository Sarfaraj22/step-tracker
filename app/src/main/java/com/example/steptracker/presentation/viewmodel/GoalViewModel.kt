package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RecommendationItem(
    val title: String,
    val subtitle: String,
    val isYellow: Boolean = true,
)

data class GoalUiState(
    val dailyGoal: Int = 10000,
    val currentSteps: Int = 8429,
    val selectedPreset: Int = 10000,
    val presets: List<Int> = listOf(5000, 8000, 10000),
    val recommendations: List<RecommendationItem> = listOf(
        RecommendationItem(
            title = "Average Adult",
            subtitle = "10,000 steps per day recommended",
            isYellow = true,
        ),
        RecommendationItem(
            title = "Your 7-Day Average",
            subtitle = "8,234 steps per day",
            isYellow = false,
        ),
    ),
) {
    val progress: Float
        get() = (currentSteps.toFloat() / dailyGoal).coerceIn(0f, 1f)

    val percentComplete: Int
        get() = (progress * 100).toInt()
}

class GoalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    fun selectPreset(steps: Int) {
        _uiState.value = _uiState.value.copy(
            selectedPreset = steps,
            dailyGoal = steps,
        )
    }
}
