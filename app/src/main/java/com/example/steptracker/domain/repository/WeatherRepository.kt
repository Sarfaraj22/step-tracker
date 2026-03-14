package com.example.steptracker.domain.repository

import com.example.steptracker.domain.model.WeatherInfo

interface WeatherRepository {
    suspend fun getCurrentWeather(query: String): Result<WeatherInfo>
}
