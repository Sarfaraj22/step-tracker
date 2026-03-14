package com.example.steptracker.data.seeder

import com.example.steptracker.data.local.dao.DailyActivityDao
import com.example.steptracker.data.local.dao.HourlyStepDao
import com.example.steptracker.data.local.entity.DailyActivityEntity
import com.example.steptracker.data.local.entity.HourlyStepEntity
import com.example.steptracker.data.remote.datasource.RemoteStepDataSource
import com.example.steptracker.data.remote.dto.DailyActivityDto
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepDataSeeder @Inject constructor(
    private val dailyActivityDao: DailyActivityDao,
    private val hourlyStepDao: HourlyStepDao,
    private val remoteStepDataSource: RemoteStepDataSource,
) {

    // Today's hourly step distribution (hour 0–23, sums to 8429)
    private val todayHourlySteps = listOf(
        0, 0, 0, 0, 0, 0,      // 00–05: sleep
        120, 350, 580, 820,     // 06–09: morning routine + commute
        650, 480, 390, 900,     // 10–13: work + lunch walk
        420, 510, 620, 800,     // 14–17: afternoon activity
        560, 370, 210, 49,      // 18–21: evening wind-down
        0, 0,                   // 22–23: sleep
    )

    // Seven base activity profiles cycled across 30 historical days
    private data class ActivityProfile(
        val stepCount: Int,
        val stepGoal: Int,
        val distanceKm: Double,
        val caloriesBurned: Int,
        val activeMinutes: Int,
        val avgHeartRate: Int,
        val hourlySteps: List<Long>,
    )

    private val profiles = listOf(
        ActivityProfile(7200, 10000, 4.1, 285, 38, 118,
            listOf(0,0,0,0,0,0, 80,260,480,620, 520,380,290,700, 410,360,480,620, 400,290,170,40, 0,0)),
        ActivityProfile(9500, 10000, 5.4, 380, 52, 126,
            listOf(0,0,0,0,0,0, 150,400,750,980, 760,580,420,1020, 640,510,680,980, 720,490,230,50, 0,0)),
        ActivityProfile(6100, 10000, 3.5, 240, 33, 112,
            listOf(0,0,0,0,0,0, 60,200,380,520, 410,310,250,580, 360,290,380,480, 310,210,100,10, 0,0)),
        ActivityProfile(8800, 10000, 5.0, 350, 48, 122,
            listOf(0,0,0,0,0,0, 110,340,680,890, 640,500,380,890, 590,460,580,820, 570,380,190,30, 0,0)),
        ActivityProfile(11200, 10000, 6.4, 445, 65, 131,
            listOf(0,0,0,0,0,0, 200,540,1020,1280, 940,720,540,1280, 880,660,840,1080, 860,590,300,66, 0,0)),
        ActivityProfile(5400, 10000, 3.1, 215, 28, 108,
            listOf(0,0,0,0,0,0, 40,160,310,430, 360,260,200,490, 300,240,310,420, 290,180,90,20, 0,0)),
        ActivityProfile(4200, 10000, 2.4, 168, 22, 98,
            listOf(0,0,0,0,0,0, 30,110,230,330, 280,200,160,380, 230,180,240,320, 220,140,70,10, 0,0)),
    )

    suspend fun seedRoomIfEmpty() {
        if (dailyActivityDao.count() > 0) return

        val today = LocalDate.now()
        val dateStr = today.toString()

        dailyActivityDao.insert(
            DailyActivityEntity(
                date = dateStr,
                stepCount = 8429,
                stepGoal = 10000,
                distanceKm = 5.2f,
                caloriesBurned = 342,
                activeMinutes = 42,
                avgHeartRate = 124,
                weeklyDistanceKm = 32.7f,
            )
        )

        hourlyStepDao.insertAll(
            todayHourlySteps.mapIndexed { hour, steps ->
                HourlyStepEntity(date = dateStr, hour = hour, stepCount = steps)
            }
        )
    }

    suspend fun seedFirestoreIfEmpty() {
        if (remoteStepDataSource.hasAnyActivity()) return

        val today = LocalDate.now()
        val activities = mutableListOf<DailyActivityDto>()

        var cumulativeWeeklyDistance = 0.0

        for (daysAgo in 30 downTo 1) {
            val date = today.minusDays(daysAgo.toLong())
            val profile = profiles[(daysAgo - 1) % profiles.size]

            // Slight variation per week to make data feel more organic
            val weekIndex = (daysAgo - 1) / 7
            val stepVariance = when (weekIndex) {
                0 -> 0
                1 -> -500
                2 -> 300
                3 -> -200
                else -> 0
            }
            val adjustedSteps = (profile.stepCount + stepVariance).coerceAtLeast(1000).toLong()
            val adjustedDistance = (profile.distanceKm * adjustedSteps / profile.stepCount)
            val adjustedCalories = (profile.caloriesBurned * adjustedSteps / profile.stepCount).toInt()
            val adjustedActiveMin = (profile.activeMinutes * adjustedSteps / profile.stepCount).toInt()

            // Weekly distance resets every 7 days
            if (daysAgo % 7 == 0) cumulativeWeeklyDistance = 0.0
            cumulativeWeeklyDistance += adjustedDistance

            activities.add(
                DailyActivityDto(
                    date = date.toString(),
                    stepCount = adjustedSteps,
                    stepGoal = profile.stepGoal.toLong(),
                    distanceKm = adjustedDistance,
                    caloriesBurned = adjustedCalories.toLong(),
                    activeMinutes = adjustedActiveMin.toLong(),
                    avgHeartRate = profile.avgHeartRate.toLong(),
                    hourlySteps = profile.hourlySteps,
                    weeklyDistanceKm = cumulativeWeeklyDistance,
                )
            )
        }

        // Firestore batch limit is 500; split if needed
        activities.chunked(499).forEach { chunk ->
            remoteStepDataSource.seedDailyActivities(chunk)
        }
    }
}
