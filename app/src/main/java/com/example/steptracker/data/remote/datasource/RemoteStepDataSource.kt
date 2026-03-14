package com.example.steptracker.data.remote.datasource

import com.example.steptracker.data.remote.dto.DailyActivityDto
import com.example.steptracker.domain.model.DailyActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class RemoteStepDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {

    private val uid: String
        get() = auth.currentUser?.uid ?: error("User not authenticated")

    private fun userCollection() =
        firestore.collection("users").document(uid).collection("daily_activity")

    suspend fun getDailyActivity(date: LocalDate): DailyActivity {
        val snapshot = userCollection()
            .document(date.toString())
            .get()
            .await()

        return if (snapshot.exists()) {
            snapshot.toObject(DailyActivityDto::class.java)?.toDomain()
                ?: emptyActivity(date)
        } else {
            emptyActivity(date)
        }
    }

    suspend fun getWeeklySteps(endDate: LocalDate): List<Int> {
        val startDate = endDate.minusDays(6)

        val snapshots = userCollection()
            .whereGreaterThanOrEqualTo("date", startDate.toString())
            .whereLessThanOrEqualTo("date", endDate.toString())
            .get()
            .await()

        val stepsByDate = snapshots.documents.associate { doc ->
            val dto = doc.toObject(DailyActivityDto::class.java)
            (dto?.date ?: "") to (dto?.stepCount?.toInt() ?: 0)
        }

        return (0..6).map { offset ->
            val date = startDate.plusDays(offset.toLong()).toString()
            stepsByDate[date] ?: 0
        }
    }

    suspend fun seedDailyActivities(activities: List<DailyActivityDto>) {
        val batch = firestore.batch()
        activities.forEach { dto ->
            val docRef = userCollection().document(dto.date ?: return@forEach)
            batch.set(docRef, dto.toMap())
        }
        batch.commit().await()
    }

    suspend fun hasAnyActivity(): Boolean {
        val snapshot = userCollection().limit(1).get().await()
        return !snapshot.isEmpty
    }

    private fun emptyActivity(date: LocalDate) = DailyActivity(
        date = date,
        stepCount = 0,
        stepGoal = 10000,
        distanceKm = 0f,
        caloriesBurned = 0,
        activeMinutes = 0,
        avgHeartRate = 0,
        hourlySteps = List(24) { 0 },
        weeklyDistanceKm = 0f,
    )
}
