package com.example.steptracker.domain.use_case.steps

import com.example.steptracker.domain.repository.StepRepository
import java.time.LocalDate
import javax.inject.Inject

class GetWeeklyStepsUseCase @Inject constructor(
    private val stepRepository: StepRepository,
) {
    suspend operator fun invoke(endDate: LocalDate): List<Int> =
        stepRepository.getWeeklySteps(endDate)
}
