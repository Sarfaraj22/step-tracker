package com.example.steptracker.domain.use_case.steps

import com.example.steptracker.domain.model.StepRecord
import com.example.steptracker.domain.repository.StepRepository
import javax.inject.Inject

class GetStepRecordForDateUseCase @Inject constructor(
    private val repository: StepRepository
) {
    suspend operator fun invoke(dateEpochDay: Long): Result<StepRecord?> =
        repository.getRecordForDate(dateEpochDay)
}
