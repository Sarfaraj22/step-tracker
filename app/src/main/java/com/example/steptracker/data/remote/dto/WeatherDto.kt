package com.example.steptracker.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.example.steptracker.domain.model.WeatherInfo

data class WeatherDto(
    @SerializedName("current") val current: CurrentDto,
)

data class CurrentDto(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("condition") val condition: ConditionDto,
)

data class ConditionDto(
    @SerializedName("text") val text: String,
    @SerializedName("code") val code: Int,
)

fun WeatherDto.toDomain(): WeatherInfo = WeatherInfo(
    tempC = current.tempC,
    conditionText = current.condition.text,
    conditionCode = current.condition.code,
    isDay = current.isDay == 1,
)
