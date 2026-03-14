package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RecommendationAccent { YELLOW, GREEN, PINK }

data class RecommendationItem(
    val title: String,
    val subtitle: String,
    val accent: RecommendationAccent = RecommendationAccent.YELLOW,
    val showSetGoalButton: Boolean = false,
)

data class GoalUiState(
    val dailyGoal: Int = 10000,
    val currentSteps: Int = 8429,
    val selectedPreset: Int = 10000,
    val presets: List<Int> = listOf(5000, 8000, 10000),
    val age: String = "25",
    val weight: String = "70",
    val recommendations: List<RecommendationItem> = listOf(
        RecommendationItem(
            title = "Average Adult",
            subtitle = "10,000 steps per day recommended",
            accent = RecommendationAccent.YELLOW,
        ),
        RecommendationItem(
            title = "Your 7-Day Average",
            subtitle = "8,234 steps per day",
            accent = RecommendationAccent.GREEN,
        ),
        RecommendationItem(
            title = "Adjusted For Progress",
            subtitle = "9,100 steps based on your weekly average",
            accent = RecommendationAccent.PINK,
            showSetGoalButton = true,
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
