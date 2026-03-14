package com.example.steptracker.domain.use_case.sync

import com.example.steptracker.domain.model.DateUtils
import com.example.steptracker.domain.repository.AuthRepository
import com.example.steptracker.domain.repository.FirestoreRepository
import com.example.steptracker.domain.repository.StepRepository
import javax.inject.Inject

class SyncStepRecordUseCase @Inject constructor(
    private val stepRepository: StepRepository,
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val user = authRepository.getCurrentUser()
            ?: return Result.failure(IllegalStateException("No authenticated user"))

        val todayEpochDay = DateUtils.todayEpochDay()
        val recordResult = stepRepository.getRecordForDate(todayEpochDay)

        val record = recordResult.getOrElse { return Result.failure(it) }
            ?: return Result.success(Unit)

        return firestoreRepository.syncStepRecord(user.uid, record)
    }
}
