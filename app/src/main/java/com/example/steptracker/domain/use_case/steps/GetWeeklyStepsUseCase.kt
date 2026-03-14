package com.example.steptracker.domain.use_case.steps

import com.example.steptracker.domain.model.StepRecord
import com.example.steptracker.domain.repository.StepRepository
import javax.inject.Inject

class GetWeeklyStepsUseCase @Inject constructor(
    private val repository: StepRepository
) {
    suspend operator fun invoke(weekStartEpochDay: Long): Result<List<StepRecord>> =
        repository.getWeeklyRecords(weekStartEpochDay)
}
