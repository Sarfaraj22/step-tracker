package com.example.steptracker.data.remote.dto

import com.example.steptracker.domain.model.DailyActivity
import java.time.LocalDate

data class DailyActivityDto(
    val date: String? = null,
    val stepCount: Long? = null,
    val stepGoal: Long? = null,
    val distanceKm: Double? = null,
    val caloriesBurned: Long? = null,
    val activeMinutes: Long? = null,
    val avgHeartRate: Long? = null,
    val hourlySteps: List<Long>? = null,
    val weeklyDistanceKm: Double? = null,
) {
    fun toDomain(): DailyActivity = DailyActivity(
        date = date?.let { LocalDate.parse(it) } ?: LocalDate.now(),
        stepCount = stepCount?.toInt() ?: 0,
        stepGoal = stepGoal?.toInt() ?: 10000,
        distanceKm = distanceKm?.toFloat() ?: 0f,
        caloriesBurned = caloriesBurned?.toInt() ?: 0,
        activeMinutes = activeMinutes?.toInt() ?: 0,
        avgHeartRate = avgHeartRate?.toInt() ?: 0,
        hourlySteps = hourlySteps?.map { it.toInt() } ?: List(24) { 0 },
        weeklyDistanceKm = weeklyDistanceKm?.toFloat() ?: 0f,
    )

    fun toMap(): Map<String, Any> = mapOf(
        "date" to (date ?: ""),
        "stepCount" to (stepCount ?: 0L),
        "stepGoal" to (stepGoal ?: 10000L),
        "distanceKm" to (distanceKm ?: 0.0),
        "caloriesBurned" to (caloriesBurned ?: 0L),
        "activeMinutes" to (activeMinutes ?: 0L),
        "avgHeartRate" to (avgHeartRate ?: 0L),
        "hourlySteps" to (hourlySteps ?: List(24) { 0L }),
        "weeklyDistanceKm" to (weeklyDistanceKm ?: 0.0),
    )
}
