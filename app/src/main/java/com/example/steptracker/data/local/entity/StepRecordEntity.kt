package com.example.steptracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_records")
data class StepRecordEntity(
    @PrimaryKey
    val dateEpochDay: Long,
    val stepCount: Int,
    val distanceMeters: Float,
    val caloriesBurned: Int,
    val activeMinutes: Int,
    val goalSteps: Int,
    val hourlyStepsCsv: String = ""
)
