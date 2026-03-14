package com.example.steptracker.data.repository

import com.example.steptracker.data.datasource.StepCounterDataSource
import com.example.steptracker.data.local.dao.StepDao
import com.example.steptracker.data.local.entity.StepRecordEntity
import com.example.steptracker.domain.model.DateUtils
import com.example.steptracker.domain.model.StepRecord
import com.example.steptracker.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepRepositoryImpl @Inject constructor(
    private val stepDao: StepDao,
    private val stepCounterDataSource: StepCounterDataSource
) : StepRepository {

    override fun observeTodayStepCount(): Flow<Int> =
        stepCounterDataSource.todayStepCount

    override suspend fun saveDailyRecord(record: StepRecord): Result<Unit> =
        runCatching { stepDao.upsert(record.toEntity()) }

    override suspend fun getTodayRecord(): Result<StepRecord?> =
        getRecordForDate(DateUtils.todayEpochDay())

    override suspend fun getRecordForDate(dateEpochDay: Long): Result<StepRecord?> =
        runCatching { stepDao.getByDate(dateEpochDay)?.toDomain() }

    override suspend fun getWeeklyRecords(weekStartEpochDay: Long): Result<List<StepRecord>> =
        runCatching {
            val weekEndEpochDay = weekStartEpochDay + 6
            stepDao.getInRange(weekStartEpochDay, weekEndEpochDay).map { it.toDomain() }
        }

    override suspend fun getMonthlyRecords(year: Int, month: Int): Result<List<StepRecord>> =
        runCatching {
            val (startEpochDay, endEpochDay) = DateUtils.monthRangeEpochDays(year, month)
            stepDao.getInRange(startEpochDay, endEpochDay).map { it.toDomain() }
        }

    override suspend fun upsertHourlySnapshot(
        dateEpochDay: Long,
        hour: Int,
        steps: Int
    ): Result<Unit> = runCatching {
        require(hour in 0..23) { "Hour must be between 0 and 23, got $hour" }

        val existing = stepDao.getByDate(dateEpochDay)
        val updatedHourly = if (existing != null) {
            existing.parseHourlySteps().toMutableList().also { it[hour] = steps }
        } else {
            MutableList(24) { 0 }.also { it[hour] = steps }
        }

        val entity = existing?.copy(hourlyStepsCsv = updatedHourly.joinToString(","))
            ?: StepRecordEntity(
                dateEpochDay = dateEpochDay,
                stepCount = steps,
                distanceMeters = 0f,
                caloriesBurned = 0,
                activeMinutes = 0,
                goalSteps = 0,
                hourlyStepsCsv = updatedHourly.joinToString(",")
            )

        stepDao.upsert(entity)
    }

    // --- Mappers ---

    private fun StepRecord.toEntity(): StepRecordEntity = StepRecordEntity(
        dateEpochDay = dateEpochDay,
        stepCount = stepCount,
        distanceMeters = distanceMeters,
        caloriesBurned = caloriesBurned,
        activeMinutes = activeMinutes,
        goalSteps = goalSteps,
        hourlyStepsCsv = hourlySteps.joinToString(",")
    )

    private fun StepRecordEntity.toDomain(): StepRecord = StepRecord(
        dateEpochDay = dateEpochDay,
        stepCount = stepCount,
        distanceMeters = distanceMeters,
        caloriesBurned = caloriesBurned,
        activeMinutes = activeMinutes,
        goalSteps = goalSteps,
        hourlySteps = parseHourlySteps()
    )

    private fun StepRecordEntity.parseHourlySteps(): List<Int> {
        if (hourlyStepsCsv.isBlank()) return List(24) { 0 }
        val parsed = hourlyStepsCsv.split(",").map { it.trim().toIntOrNull() ?: 0 }
        return if (parsed.size == 24) parsed else List(24) { i -> parsed.getOrElse(i) { 0 } }
    }
}
