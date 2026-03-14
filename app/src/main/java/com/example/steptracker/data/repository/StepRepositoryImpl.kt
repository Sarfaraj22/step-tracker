package com.example.steptracker.data.repository

import com.example.steptracker.data.local.datasource.LocalStepDataSource
import com.example.steptracker.data.remote.datasource.RemoteStepDataSource
import com.example.steptracker.domain.model.DailyActivity
import com.example.steptracker.domain.repository.StepRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepRepositoryImpl @Inject constructor(
    private val local: LocalStepDataSource,
    private val remote: RemoteStepDataSource,
) : StepRepository {

    override suspend fun getDailyActivity(date: LocalDate): DailyActivity =
        if (date == LocalDate.now()) local.getTodayActivity()
        else remote.getDailyActivity(date)

    override suspend fun getWeeklySteps(endDate: LocalDate): List<Int> {
        val steps = remote.getWeeklySteps(endDate).toMutableList()
        val today = LocalDate.now()
        // The weekly range is endDate-6 (index 0) through endDate (index 6).
        // If today falls within this window, Firestore has no document for it,
        // so we replace that position with the step count from Room.
        if (!today.isBefore(endDate.minusDays(6)) && !today.isAfter(endDate)) {
            val todayIndex = (6 - java.time.temporal.ChronoUnit.DAYS.between(today, endDate)).toInt()
            if (todayIndex in 0..6) {
                steps[todayIndex] = local.getTodayActivity().stepCount
            }
        }
        return steps
    }
}
