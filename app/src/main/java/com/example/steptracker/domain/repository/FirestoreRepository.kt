package com.example.steptracker.domain.repository

import com.example.steptracker.domain.model.StepRecord

interface FirestoreRepository {
    suspend fun syncStepRecord(userId: String, record: StepRecord): Result<Unit>
}
