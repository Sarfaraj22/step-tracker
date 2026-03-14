package com.example.steptracker.data.repository

import com.example.steptracker.domain.model.StepRecord
import com.example.steptracker.domain.repository.FirestoreRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreRepository {

    override suspend fun syncStepRecord(userId: String, record: StepRecord): Result<Unit> {
        return runCatching {
            val data = mapOf(
                "dateEpochDay" to record.dateEpochDay,
                "stepCount" to record.stepCount,
                "distanceMeters" to record.distanceMeters,
                "caloriesBurned" to record.caloriesBurned,
                "activeMinutes" to record.activeMinutes,
                "goalSteps" to record.goalSteps,
                "hourlySteps" to record.hourlySteps
            )
            firestore
                .collection("users")
                .document(userId)
                .collection("stepRecords")
                .document(record.dateEpochDay.toString())
                .set(data)
                .await()
        }
    }
}
