package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.data.seeder.StepDataSeeder
import com.example.steptracker.domain.use_case.steps.GetDailyActivityUseCase
import com.example.steptracker.domain.use_case.steps.GetWeeklyStepsUseCase
import com.example.steptracker.domain.use_case.weather.GetCurrentWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class GoalItem(
    val label: String,
    val current: Float,
    val target: Float,
    val unit: String,
    val isGreen: Boolean = false,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val weatherTemp: String = "--°",
    val weatherCondition: String = "",
    val weatherMessage: String = "Fetching weather...",
    val isWalkSuitable: Boolean = false,
    val weatherIsLoading: Boolean = true,
    val weatherConditionCode: Int = 1000,
    val weatherIsDay: Boolean = true,
    val stepCount: Int = 0,
    val stepGoal: Int = 10000,
    val date: String = "",
    val distanceKm: Float = 0f,
    val caloriesBurned: Int = 0,
    val activeMinutes: Int = 0,
    val avgHeartRate: Int = 0,
    val weeklySteps: List<Int> = List(7) { 0 },
    val todayHourlySteps: List<Int> = List(24) { 0 },
    val goals: List<GoalItem> = emptyList(),
    val isAtLatestDay: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyActivity: GetDailyActivityUseCase,
    private val getWeeklySteps: GetWeeklyStepsUseCase,
    private val seeder: StepDataSeeder,
    private val getCurrentWeather: GetCurrentWeatherUseCase,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val _selectedDate = MutableStateFlow(today)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                seeder.seedRoomIfEmpty()
                seeder.seedFirestoreIfEmpty()
            } catch (_: Exception) { }
            loadActivity(today)
        }
    }

    fun loadWeather(lat: Double?, lon: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(weatherIsLoading = true) }
            getCurrentWeather(lat, lon)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            weatherIsLoading = false,
                            weatherTemp = "${result.weatherInfo.tempC.toInt()}°",
                            weatherCondition = result.weatherInfo.conditionText,
                            weatherMessage = result.message,
                            isWalkSuitable = result.isWalkSuitable,
                            weatherConditionCode = result.weatherInfo.conditionCode,
                            weatherIsDay = result.weatherInfo.isDay,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            weatherIsLoading = false,
                            weatherTemp = "--°",
                            weatherCondition = "Unavailable",
                            weatherMessage = "Could not load weather",
                            isWalkSuitable = false,
                        )
                    }
                }
        }
    }

    fun navigateDate(delta: Int) {
        val next = _selectedDate.value.plusDays(delta.toLong())
        if (next.isAfter(today)) return
        _selectedDate.update { next }
        viewModelScope.launch { loadActivity(next) }
    }

    private suspend fun loadActivity(date: LocalDate) {
        _uiState.update { it.copy(isLoading = true) }

        try {
            val activity = getDailyActivity(date)
            val weeklySteps = getWeeklySteps(date)

            val weeklyDistanceGoal = 35f
            val activeMinutesGoal = 60f

            _uiState.update {
                it.copy(
                    isLoading = false,
                    stepCount = activity.stepCount,
                    stepGoal = activity.stepGoal,
                    date = formatDate(date),
                    distanceKm = activity.distanceKm,
                    caloriesBurned = activity.caloriesBurned,
                    activeMinutes = activity.activeMinutes,
                    avgHeartRate = activity.avgHeartRate,
                    weeklySteps = weeklySteps,
                    todayHourlySteps = activity.hourlySteps,
                    goals = listOf(
                        GoalItem(
                            label = "Daily Steps",
                            current = activity.stepCount.toFloat(),
                            target = activity.stepGoal.toFloat(),
                            unit = "steps",
                        ),
                        GoalItem(
                            label = "Weekly Distance",
                            current = activity.weeklyDistanceKm,
                            target = weeklyDistanceGoal,
                            unit = "km",
                            isGreen = true,
                        ),
                        GoalItem(
                            label = "Active Minutes",
                            current = activity.activeMinutes.toFloat(),
                            target = activeMinutesGoal,
                            unit = "min",
                        ),
                    ),
                    isAtLatestDay = !date.isBefore(today),
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun formatDate(date: LocalDate): String {
        val today = LocalDate.now()
        return when (date) {
            today -> "Today, ${date.format(DateTimeFormatter.ofPattern("dd/MM/yy"))}"
            today.minusDays(1) -> "Yesterday, ${date.format(DateTimeFormatter.ofPattern("dd/MM/yy"))}"
            else -> date.format(DateTimeFormatter.ofPattern("EEE, dd/MM/yy"))
        }
    }
}
