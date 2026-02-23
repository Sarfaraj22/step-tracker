package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HourlyBreakdownItem(
    val hourLabel: String,
    val timeLabel: String,
    val steps: Int,
)

data class ActivityDayUiState(
    val totalSteps: Int = 10_480,
    val dailyAvg: Int = 873,
    // 12 data points — every 2 h from 02:00 to 24:00
    val hourlySteps: List<Int> = listOf(80, 120, 350, 650, 726, 1364, 1118, 1029, 445, 739, 460, 0),
    val bestDaySteps: Int = 1_364,
    val bestDayTime: String = "12:00",
    val currentStreak: Int = 0,
    val longestStreak: Int = 3,
    val dailyBreakdown: List<HourlyBreakdownItem> = listOf(
        HourlyBreakdownItem("22h", "22:00", 460),
        HourlyBreakdownItem("20h", "20:00", 739),
        HourlyBreakdownItem("18h", "18:00", 445),
        HourlyBreakdownItem("16h", "16:00", 1_029),
        HourlyBreakdownItem("14h", "14:00", 1_118),
        HourlyBreakdownItem("12h", "12:00", 1_364),
        HourlyBreakdownItem("10h", "10:00", 726),
    ),
)

data class WeeklyBreakdownItem(
    val dayLabel: String,
    val dateLabel: String,
    val steps: Int,
    val goalAchieved: Boolean,
)

data class MonthlyBreakdownItem(
    val dayLabel: String,
    val dateLabel: String,
    val steps: Int,
    val goalAchieved: Boolean,
)

data class ActivityMonthUiState(
    val totalSteps: Int = 207_769,
    val dailyAvg: Int = 6_925,
    val monthlySteps: List<Int> = listOf(
        4_210, 7_840, 6_415, 5_320, 3_890, 8_100, 9_230,
        6_750, 4_480, 7_620, 5_910, 8_250, 3_670, 6_130,
        7_450, 9_800, 5_560, 4_320, 7_980, 6_700, 8_450,
        3_210, 5_980, 10_972, 7_310, 4_870, 8_630, 6_415,
        8_568, 8_726,
    ),
    val bestDaySteps: Int = 10_972,
    val bestDayDate: String = "Sat, 1/24",
    val currentStreak: Int = 0,
    val longestStreak: Int = 2,
    val monthlyBreakdown: List<MonthlyBreakdownItem> = listOf(
        MonthlyBreakdownItem("Sun", "2/15", 6_415, goalAchieved = false),
        MonthlyBreakdownItem("Sat", "2/14", 8_568, goalAchieved = true),
        MonthlyBreakdownItem("Fri", "2/13", 4_771, goalAchieved = false),
        MonthlyBreakdownItem("Thu", "2/12", 6_690, goalAchieved = false),
        MonthlyBreakdownItem("Wed", "2/11", 3_182, goalAchieved = false),
        MonthlyBreakdownItem("Tue", "2/10", 8_726, goalAchieved = true),
        MonthlyBreakdownItem("Mon", "2/9",  6_708, goalAchieved = false),
    ),
)

data class ActivityWeekUiState(
    val totalSteps: Int = 43_401,
    val dailyAvg: Int = 6_200,
    // Mon → Sun
    val weeklySteps: List<Int> = listOf(3_787, 9_933, 6_971, 3_093, 3_240, 7_179, 9_198),
    val bestDaySteps: Int = 9_933,
    val bestDayDate: String = "Tue, 2/10",
    val currentStreak: Int = 1,
    val longestStreak: Int = 3,
    val weeklyBreakdown: List<WeeklyBreakdownItem> = listOf(
        WeeklyBreakdownItem("Sun", "2/15", 9_198, goalAchieved = true),
        WeeklyBreakdownItem("Sat", "2/14", 7_179, goalAchieved = false),
        WeeklyBreakdownItem("Fri", "2/13", 3_240, goalAchieved = false),
        WeeklyBreakdownItem("Thu", "2/12", 3_093, goalAchieved = false),
        WeeklyBreakdownItem("Wed", "2/11", 6_971, goalAchieved = false),
        WeeklyBreakdownItem("Tue", "2/10", 9_933, goalAchieved = true),
        WeeklyBreakdownItem("Mon", "2/9",  3_787, goalAchieved = false),
    ),
)

class ActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityDayUiState())
    val uiState: StateFlow<ActivityDayUiState> = _uiState.asStateFlow()

    private val _weekUiState = MutableStateFlow(ActivityWeekUiState())
    val weekUiState: StateFlow<ActivityWeekUiState> = _weekUiState.asStateFlow()

    private val _monthUiState = MutableStateFlow(ActivityMonthUiState())
    val monthUiState: StateFlow<ActivityMonthUiState> = _monthUiState.asStateFlow()
}
