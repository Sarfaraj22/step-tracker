package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.domain.use_case.steps.GetDailyActivityUseCase
import com.example.steptracker.domain.use_case.steps.UpdateDailyGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RecommendationAccent { YELLOW, GREEN, PINK }

data class RecommendationItem(
    val title: String,
    val subtitle: String,
    val accent: RecommendationAccent = RecommendationAccent.YELLOW,
    val showSetGoalButton: Boolean = false,
    val suggestedGoal: Int? = null,
)

data class GoalUiState(
    val dailyGoal: Int = 10000,
    val currentSteps: Int = 0,
    val selectedPreset: Int = 10000,
    val presets: List<Int> = listOf(5000, 8000, 10000),
    val age: String = "25",
    val weight: String = "70",
    val recommendations: List<RecommendationItem> = emptyList(),
    val isUpdatingGoal: Boolean = false,
    val updateError: String? = null,
) {
    val progress: Float
        get() = if (dailyGoal > 0) (currentSteps.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f

    val percentComplete: Int
        get() = (progress * 100).toInt()
}

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val getDailyActivity: GetDailyActivityUseCase,
    private val updateDailyGoalUseCase: UpdateDailyGoalUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GoalUiState(
            recommendations = defaultRecommendations(),
        ),
    )
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadCurrentGoal()
        }
    }

    private suspend fun loadCurrentGoal() {
        try {
            val today = LocalDate.now()
            val activity = getDailyActivity(today)
            val presets = uiState.value.presets
            val selectedPreset = presets.firstOrNull { it == activity.stepGoal } ?: activity.stepGoal

            _uiState.update {
                it.copy(
                    dailyGoal = activity.stepGoal,
                    currentSteps = activity.stepCount,
                    selectedPreset = selectedPreset,
                    recommendations = defaultRecommendations(activity.stepGoal),
                )
            }
        } catch (_: Exception) { }
    }

    fun selectPreset(steps: Int) {
        updateDailyGoal(steps)
    }

    fun applyRecommendedGoal(steps: Int) {
        updateDailyGoal(steps)
    }

    fun clearUpdateError() {
        _uiState.update { it.copy(updateError = null) }
    }

    fun updateDailyGoal(steps: Int) {
        if (steps <= 0) return
        val today = LocalDate.now()

        _uiState.update {
            it.copy(
                isUpdatingGoal = true,
                updateError = null,
            )
        }

        viewModelScope.launch {
            updateDailyGoalUseCase(today, steps)
                .onSuccess {
                    _uiState.update { current ->
                        val presets = current.presets
                        val selectedPreset = presets.firstOrNull { it == steps } ?: steps
                        current.copy(
                            isUpdatingGoal = false,
                            dailyGoal = steps,
                            selectedPreset = selectedPreset,
                            recommendations = defaultRecommendations(steps),
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isUpdatingGoal = false,
                            updateError = e.message ?: "Failed to update daily goal",
                        )
                    }
                }
        }
    }
}

private fun defaultRecommendations(currentGoal: Int = 10000): List<RecommendationItem> =
    listOf(
        RecommendationItem(
            title = "Average Adult",
            subtitle = "10,000 steps per day recommended",
            accent = RecommendationAccent.YELLOW,
            suggestedGoal = 10_000,
        ),
        RecommendationItem(
            title = "Gentle Start",
            subtitle = "8,000 steps per day as a sustainable baseline",
            accent = RecommendationAccent.GREEN,
            suggestedGoal = 8_000,
        ),
        RecommendationItem(
            title = "Adjusted For Progress",
            subtitle = "${currentGoal + 500} steps based on your recent activity",
            accent = RecommendationAccent.PINK,
            showSetGoalButton = true,
            suggestedGoal = currentGoal + 500,
        ),
    )
