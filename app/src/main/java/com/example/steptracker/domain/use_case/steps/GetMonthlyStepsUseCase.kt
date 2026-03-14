package com.example.steptracker.domain.use_case.steps

import com.example.steptracker.domain.model.StepRecord
import com.example.steptracker.domain.repository.StepRepository
import javax.inject.Inject

class GetMonthlyStepsUseCase @Inject constructor(
    private val repository: StepRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Result<List<StepRecord>> =
        repository.getMonthlyRecords(year, month)
}
