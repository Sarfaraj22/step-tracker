package com.example.steptracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val date: String,
    val stepCount: Int,
    val stepGoal: Int,
    val distanceKm: Float,
    val caloriesBurned: Int,
    val activeMinutes: Int,
    val avgHeartRate: Int,
    val weeklyDistanceKm: Float,
)
