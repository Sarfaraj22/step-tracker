package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.domain.model.DailyActivity
import com.example.steptracker.domain.model.StepRecord
import com.example.steptracker.domain.use_case.steps.GetDailyActivityUseCase
import com.example.steptracker.domain.use_case.steps.GetMonthlyStepsUseCase
import com.example.steptracker.domain.use_case.steps.GetWeeklyStepsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class HourlyBreakdownItem(
    val hourLabel: String,
    val timeLabel: String,
    val steps: Int,
)

data class ActivityDayUiState(
    val totalSteps: Int = 0,
    val dailyAvg: Int = 0,
    // 12 data points — every 2 h from 00:00 to 24:00
    val hourlySteps: List<Int> = List(12) { 0 },
    val bestDaySteps: Int = 0,
    val bestDayTime: String = "--:--",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val dailyBreakdown: List<HourlyBreakdownItem> = emptyList(),
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
    val totalSteps: Int = 0,
    val dailyAvg: Int = 0,
    val monthlySteps: List<Int> = List(30) { 0 },
    val bestDaySteps: Int = 0,
    val bestDayDate: String = "",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val monthlyBreakdown: List<MonthlyBreakdownItem> = emptyList(),
)

data class ActivityWeekUiState(
    val totalSteps: Int = 0,
    val dailyAvg: Int = 0,
    val weeklySteps: List<Int> = List(7) { 0 },
    val bestDaySteps: Int = 0,
    val bestDayDate: String = "",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val weeklyBreakdown: List<WeeklyBreakdownItem> = emptyList(),
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getDailyActivity: GetDailyActivityUseCase,
    private val getWeeklySteps: GetWeeklyStepsUseCase,
    private val getMonthlySteps: GetMonthlyStepsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDayUiState())
    val uiState: StateFlow<ActivityDayUiState> = _uiState.asStateFlow()

    private val _weekUiState = MutableStateFlow(ActivityWeekUiState())
    val weekUiState: StateFlow<ActivityWeekUiState> = _weekUiState.asStateFlow()

    private val _monthUiState = MutableStateFlow(ActivityMonthUiState())
    val monthUiState: StateFlow<ActivityMonthUiState> = _monthUiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadToday()
        }
    }

    private suspend fun loadToday() {
        val today = LocalDate.now()

        try {
            val dailyActivity = getDailyActivity(today)
            val weeklySteps = getWeeklySteps(today)
            val monthlyRecords = getMonthlySteps(today.year, today.monthValue).getOrElse { emptyList() }

            val (currentStreak, longestStreak) = computeStreaks(monthlyRecords, dailyActivity.stepGoal)

            _uiState.value = buildDayUiState(
                date = today,
                activity = dailyActivity,
                weeklySteps = weeklySteps,
                monthlyRecords = monthlyRecords,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
            )

            _weekUiState.value = buildWeekUiState(
                endDate = today,
                weeklySteps = weeklySteps,
                stepGoal = dailyActivity.stepGoal,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
            )

            _monthUiState.value = buildMonthUiState(
                referenceDate = today,
                monthlyRecords = monthlyRecords,
                defaultGoal = dailyActivity.stepGoal,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
            )
        } catch (_: Exception) { }
    }

    private fun buildDayUiState(
        date: LocalDate,
        activity: DailyActivity,
        weeklySteps: List<Int>,
        monthlyRecords: List<StepRecord>,
        currentStreak: Int,
        longestStreak: Int,
    ): ActivityDayUiState {
        val totalSteps = activity.stepCount
        val dailyAvg = if (weeklySteps.isNotEmpty()) {
            (weeklySteps.sum().toFloat() / weeklySteps.size).roundToInt()
        } else {
            totalSteps
        }

        val hourlySteps24 = activity.hourlySteps.let { source ->
            if (source.size == 24) {
                source
            } else {
                List(24) { index -> source.getOrElse(index) { 0 } }
            }
        }

        val hourlySteps12 = (0 until 12).map { bucket ->
            val i0 = bucket * 2
            val i1 = i0 + 1
            hourlySteps24.getOrElse(i0) { 0 } + hourlySteps24.getOrElse(i1) { 0 }
        }

        val bestHourIndex = hourlySteps24.indices.maxByOrNull { hourlySteps24[it] } ?: 0
        val bestDaySteps = hourlySteps24.getOrElse(bestHourIndex) { 0 }
        val bestDayTime = String.format("%02d:00", bestHourIndex)

        val breakdownItems = hourlySteps24.mapIndexed { hour, steps ->
            HourlyBreakdownItem(
                hourLabel = "${hour}h",
                timeLabel = String.format("%02d:00", hour),
                steps = steps,
            )
        }

        val dailyBreakdown = breakdownItems
            .sortedByDescending { it.steps }
            .take(7)
            .sortedBy { it.timeLabel }

        return ActivityDayUiState(
            totalSteps = totalSteps,
            dailyAvg = dailyAvg,
            hourlySteps = hourlySteps12,
            bestDaySteps = bestDaySteps,
            bestDayTime = bestDayTime,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            dailyBreakdown = dailyBreakdown,
        )
    }

    private fun buildWeekUiState(
        endDate: LocalDate,
        weeklySteps: List<Int>,
        stepGoal: Int,
        currentStreak: Int,
        longestStreak: Int,
    ): ActivityWeekUiState {
        if (weeklySteps.isEmpty()) {
            return ActivityWeekUiState()
        }

        val startDate = endDate.minusDays(6)
        val totalSteps = weeklySteps.sum()
        val dailyAvg = (totalSteps.toFloat() / weeklySteps.size).roundToInt()

        val bestIndex = weeklySteps.indices.maxByOrNull { weeklySteps[it] } ?: 0
        val bestDate = startDate.plusDays(bestIndex.toLong())
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, M/d", Locale.getDefault())

        val bestDaySteps = weeklySteps[bestIndex]
        val bestDayDate = bestDate.format(dateFormatter)

        val weeklyBreakdown = weeklySteps.mapIndexed { index, steps ->
            val date = startDate.plusDays(index.toLong())
            WeeklyBreakdownItem(
                dayLabel = date.dayOfWeek.name.take(3).lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) },
                dateLabel = "${date.monthValue}/${date.dayOfMonth}",
                steps = steps,
                goalAchieved = steps >= stepGoal,
            )
        }.sortedByDescending { it.dateLabel }

        val achievementFlags = weeklySteps.map { it >= stepGoal }
        val weekCurrentStreak = achievementFlags
            .asReversed()
            .takeWhile { it }
            .count()
        val weekLongestStreak = achievementFlags
            .fold(0 to 0) { (current, longest), achieved ->
                if (achieved) {
                    val newCurrent = current + 1
                    newCurrent to maxOf(longest, newCurrent)
                } else {
                    0 to longest
                }
            }
            .second

        return ActivityWeekUiState(
            totalSteps = totalSteps,
            dailyAvg = dailyAvg,
            weeklySteps = weeklySteps,
            bestDaySteps = bestDaySteps,
            bestDayDate = bestDayDate,
            currentStreak = maxOf(currentStreak, weekCurrentStreak),
            longestStreak = maxOf(longestStreak, weekLongestStreak),
            weeklyBreakdown = weeklyBreakdown,
        )
    }

    private fun buildMonthUiState(
        referenceDate: LocalDate,
        monthlyRecords: List<StepRecord>,
        defaultGoal: Int,
        currentStreak: Int,
        longestStreak: Int,
    ): ActivityMonthUiState {
        if (monthlyRecords.isEmpty()) {
            return ActivityMonthUiState()
        }

        val year = referenceDate.year
        val month = referenceDate.monthValue
        val daysInMonth = referenceDate.lengthOfMonth()

        val stepsByDay = IntArray(daysInMonth) { 0 }
        val recordsByDate = monthlyRecords.associateBy { LocalDate.ofEpochDay(it.dateEpochDay) }

        monthlyRecords.forEach { record ->
            val date = LocalDate.ofEpochDay(record.dateEpochDay)
            if (date.year == year && date.monthValue == month) {
                val index = date.dayOfMonth - 1
                if (index in stepsByDay.indices) {
                    stepsByDay[index] = record.stepCount
                }
            }
        }

        val monthlySteps = stepsByDay.toList()
        val totalSteps = monthlySteps.sum()
        val dailyAvg = (totalSteps.toFloat() / daysInMonth).roundToInt()

        val bestIndex = monthlySteps.indices.maxByOrNull { monthlySteps[it] } ?: 0
        val bestDate = LocalDate.of(year, month, bestIndex + 1)
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, M/d", Locale.getDefault())

        val bestDaySteps = monthlySteps[bestIndex]
        val bestDayDate = bestDate.format(dateFormatter)

        val recentDates = buildList {
            var date = referenceDate
            while (size < 7 && date.monthValue == month && date.year == year) {
                add(date)
                date = date.minusDays(1)
            }
        }

        val monthlyBreakdown = recentDates.map { date ->
            val steps = monthlySteps.getOrElse(date.dayOfMonth - 1) { 0 }
            val record = recordsByDate[date]
            val goal = (record?.goalSteps ?: defaultGoal).takeIf { it > 0 } ?: defaultGoal

            MonthlyBreakdownItem(
                dayLabel = date.dayOfWeek.name.take(3).lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) },
                dateLabel = "${date.monthValue}/${date.dayOfMonth}",
                steps = steps,
                goalAchieved = steps >= goal,
            )
        }

        return ActivityMonthUiState(
            totalSteps = totalSteps,
            dailyAvg = dailyAvg,
            monthlySteps = monthlySteps,
            bestDaySteps = bestDaySteps,
            bestDayDate = bestDayDate,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            monthlyBreakdown = monthlyBreakdown,
        )
    }

    private fun computeStreaks(
        monthlyRecords: List<StepRecord>,
        defaultGoal: Int,
    ): Pair<Int, Int> {
        if (monthlyRecords.isEmpty()) return 0 to 0

        val sorted = monthlyRecords.sortedBy { it.dateEpochDay }

        var longest = 0
        var current = 0
        sorted.forEach { record ->
            val goal = (record.goalSteps.takeIf { it > 0 } ?: defaultGoal)
            if (record.stepCount >= goal) {
                current += 1
                if (current > longest) longest = current
            } else {
                current = 0
            }
        }

        var currentStreak = 0
        for (record in sorted.asReversed()) {
            val goal = (record.goalSteps.takeIf { it > 0 } ?: defaultGoal)
            if (record.stepCount >= goal) {
                currentStreak += 1
            } else {
                break
            }
        }

        return currentStreak to longest
    }
}
