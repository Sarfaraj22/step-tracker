package com.example.steptracker.domain.use_case.steps

import com.example.steptracker.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTodayStepsUseCase @Inject constructor(
    private val repository: StepRepository
) {
    operator fun invoke(): Flow<Int> = repository.observeTodayStepCount()
}
