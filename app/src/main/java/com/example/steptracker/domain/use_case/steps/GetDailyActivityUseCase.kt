package com.example.steptracker.domain.use_case.steps

import com.example.steptracker.domain.model.DailyActivity
import com.example.steptracker.domain.repository.StepRepository
import java.time.LocalDate
import javax.inject.Inject

class GetDailyActivityUseCase @Inject constructor(
    private val stepRepository: StepRepository,
) {
    suspend operator fun invoke(date: LocalDate): DailyActivity =
        stepRepository.getDailyActivity(date)
}
