package com.example.steptracker.data.remote.api

import com.example.steptracker.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") query: String,
        @Query("key") apiKey: String,
    ): WeatherDto
}
