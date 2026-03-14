package com.example.steptracker.domain.use_case.weather

import com.example.steptracker.domain.model.WeatherInfo
import com.example.steptracker.domain.repository.WeatherRepository
import javax.inject.Inject

data class WeatherResult(
    val weatherInfo: WeatherInfo,
    val isWalkSuitable: Boolean,
    val message: String,
)

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(lat: Double?, lon: Double?): Result<WeatherResult> {
        val query = if (lat != null && lon != null) "$lat,$lon" else "auto:ip"
        return repository.getCurrentWeather(query).map { info ->
            val suitable = isWalkSuitable(info.conditionCode, info.tempC)
            WeatherResult(
                weatherInfo = info,
                isWalkSuitable = suitable,
                message = if (suitable) "Perfect day for a walk!" else "Better stay indoors today",
            )
        }
    }

    private fun isWalkSuitable(conditionCode: Int, tempC: Double): Boolean {
        if (tempC < 0) return false
        // Codes 1000–1030: Clear/Sunny, Partly Cloudy, Cloudy, Overcast, Mist — suitable
        // Codes >= 1063 involve precipitation/storms — not suitable
        return conditionCode <= 1030
    }
}
