package com.example.steptracker.data.local.datasource

import com.example.steptracker.data.local.dao.DailyActivityDao
import com.example.steptracker.data.local.dao.HourlyStepDao
import com.example.steptracker.data.local.entity.DailyActivityEntity
import com.example.steptracker.domain.model.DailyActivity
import java.time.LocalDate
import javax.inject.Inject

class LocalStepDataSource @Inject constructor(
    private val dailyActivityDao: DailyActivityDao,
    private val hourlyStepDao: HourlyStepDao,
) {

    suspend fun getTodayActivity(): DailyActivity {
        val today = LocalDate.now()
        val dateStr = today.toString()

        val entity = dailyActivityDao.getByDate(dateStr)
        val hourlyEntities = hourlyStepDao.getForDate(dateStr)

        val hourlySteps = if (hourlyEntities.size == 24) {
            hourlyEntities.map { it.stepCount }
        } else {
            List(24) { hour -> hourlyEntities.find { it.hour == hour }?.stepCount ?: 0 }
        }

        return DailyActivity(
            date = today,
            stepCount = entity?.stepCount ?: 0,
            stepGoal = entity?.stepGoal ?: 10000,
            distanceKm = entity?.distanceKm ?: 0f,
            caloriesBurned = entity?.caloriesBurned ?: 0,
            activeMinutes = entity?.activeMinutes ?: 0,
            avgHeartRate = entity?.avgHeartRate ?: 0,
            hourlySteps = hourlySteps,
            weeklyDistanceKm = entity?.weeklyDistanceKm ?: 0f,
        )
    }

    suspend fun updateDailyGoal(date: LocalDate, stepGoal: Int) {
        val dateStr = date.toString()
        val existing = dailyActivityDao.getByDate(dateStr)
        val updated = if (existing != null) {
            existing.copy(stepGoal = stepGoal)
        } else {
            DailyActivityEntity(
                date = dateStr,
                stepCount = 0,
                stepGoal = stepGoal,
                distanceKm = 0f,
                caloriesBurned = 0,
                activeMinutes = 0,
                avgHeartRate = 0,
                weeklyDistanceKm = 0f,
            )
        }
        dailyActivityDao.insert(updated)
    }

    suspend fun clearAll() {
        dailyActivityDao.clearAll()
        hourlyStepDao.clearAll()
    }
}
