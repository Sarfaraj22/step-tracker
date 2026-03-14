package com.example.steptracker.data.repository

import com.example.steptracker.data.remote.datasource.WeatherRemoteDataSource
import com.example.steptracker.domain.model.WeatherInfo
import com.example.steptracker.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
) : WeatherRepository {
    override suspend fun getCurrentWeather(query: String): Result<WeatherInfo> {
        return runCatching { remoteDataSource.getCurrentWeather(query) }
    }
}
