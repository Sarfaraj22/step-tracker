package com.example.steptracker.domain.model

data class StepRecord(
    val dateEpochDay: Long,
    val stepCount: Int,
    val distanceMeters: Float,
    val caloriesBurned: Int,
    val activeMinutes: Int,
    val goalSteps: Int,
    val hourlySteps: List<Int> = List(24) { 0 }
)
