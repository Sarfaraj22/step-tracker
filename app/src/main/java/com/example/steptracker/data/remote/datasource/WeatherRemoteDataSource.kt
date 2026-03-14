package com.example.steptracker.data.remote.datasource

import com.example.steptracker.data.remote.api.WeatherApiService
import com.example.steptracker.data.remote.dto.toDomain
import com.example.steptracker.domain.model.WeatherInfo
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(
    private val api: WeatherApiService,
) {
    companion object {
        private const val API_KEY = "5eedddf352ea45f1984114833261403"
    }

    suspend fun getCurrentWeather(query: String): WeatherInfo {
        return api.getCurrentWeather(query = query, apiKey = API_KEY).toDomain()
    }
}
