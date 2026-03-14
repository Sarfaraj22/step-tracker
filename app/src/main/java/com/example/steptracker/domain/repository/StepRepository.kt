package com.example.steptracker.domain.repository

import com.example.steptracker.domain.model.DailyActivity
import java.time.LocalDate

interface StepRepository {
    suspend fun getDailyActivity(date: LocalDate): DailyActivity
    suspend fun getWeeklySteps(endDate: LocalDate): List<Int>
}
