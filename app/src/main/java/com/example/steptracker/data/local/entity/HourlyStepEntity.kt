package com.example.steptracker.data.local.entity

import androidx.room.Entity

@Entity(tableName = "hourly_steps", primaryKeys = ["date", "hour"])
data class HourlyStepEntity(
    val date: String,
    val hour: Int,
    val stepCount: Int,
)
