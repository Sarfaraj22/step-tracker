package com.example.steptracker.domain.model

data class WeatherInfo(
    val tempC: Double,
    val conditionText: String,
    val conditionCode: Int,
    val isDay: Boolean,
)
