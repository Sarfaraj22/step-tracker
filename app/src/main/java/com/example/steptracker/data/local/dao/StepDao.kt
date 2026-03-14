package com.example.steptracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.steptracker.data.local.entity.StepRecordEntity

@Dao
interface StepDao {

    @Query("SELECT * FROM step_records WHERE dateEpochDay = :epochDay")
    suspend fun getByDate(epochDay: Long): StepRecordEntity?

    @Query(
        """
        SELECT * FROM step_records
        WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay
        ORDER BY dateEpochDay ASC
        """
    )
    suspend fun getInRange(startEpochDay: Long, endEpochDay: Long): List<StepRecordEntity>

    @Upsert
    suspend fun upsert(entity: StepRecordEntity)

    @Query("DELETE FROM step_records")
    suspend fun clearAll()
}
