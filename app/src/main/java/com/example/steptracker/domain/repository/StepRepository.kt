package com.example.steptracker.domain.repository

import com.example.steptracker.domain.model.DailyActivity
import com.example.steptracker.domain.model.StepRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface StepRepository {
    fun observeTodayStepCount(): Flow<Int>
    suspend fun saveDailyRecord(record: StepRecord): Result<Unit>
    suspend fun getTodayRecord(): Result<StepRecord?>
    suspend fun getRecordForDate(dateEpochDay: Long): Result<StepRecord?>
    suspend fun getWeeklyRecords(weekStartEpochDay: Long): Result<List<StepRecord>>
    suspend fun getMonthlyRecords(year: Int, month: Int): Result<List<StepRecord>>
    suspend fun upsertHourlySnapshot(dateEpochDay: Long, hour: Int, steps: Int): Result<Unit>
    suspend fun getDailyActivity(date: LocalDate): DailyActivity
    suspend fun getWeeklySteps(endDate: LocalDate): List<Int>
    suspend fun updateDailyGoal(date: LocalDate, stepGoal: Int): Result<Unit>
    suspend fun resetAllData(): Result<Unit>
}
