package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GoalItem(
    val label: String,
    val current: Float,
    val target: Float,
    val unit: String,
    val isGreen: Boolean = false,
)

data class HomeUiState(
    val stepCount: Int = 8429,
    val stepGoal: Int = 10000,
    val date: String = "Today, 15/02/26",
    val distanceKm: Float = 5.2f,
    val caloriesBurned: Int = 342,
    val activeMinutes: Int = 42,
    val avgHeartRate: Int = 124,
    val weeklySteps: List<Int> = listOf(7200, 9500, 6100, 8800, 11200, 5400, 8429),
    val todayHourlySteps: List<Int> = listOf(120, 450, 900, 650, 1100, 800),
    val goals: List<GoalItem> = listOf(
        GoalItem("Daily Steps", 8429f, 10000f, "steps"),
        GoalItem("Weekly Distance", 28.4f, 35f, "km", isGreen = true),
        GoalItem("Active Minutes", 42f, 60f, "min"),
    ),
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
