package com.example.steptracker.domain.model

import java.time.LocalDate

data class DailyActivity(
    val date: LocalDate,
    val stepCount: Int,
    val stepGoal: Int,
    val distanceKm: Float,
    val caloriesBurned: Int,
    val activeMinutes: Int,
    val avgHeartRate: Int,
    val hourlySteps: List<Int>,
    val weeklyDistanceKm: Float,
)
