package com.example.steptracker.domain.use_case.steps

import com.example.steptracker.domain.repository.StepRepository
import javax.inject.Inject

class ResetUserStepDataUseCase @Inject constructor(
    private val stepRepository: StepRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        stepRepository.resetAllData()
}

