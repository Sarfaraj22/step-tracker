package com.example.steptracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.steptracker.data.local.entity.HourlyStepEntity

@Dao
interface HourlyStepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(steps: List<HourlyStepEntity>)

    @Query("SELECT * FROM hourly_steps WHERE date = :date ORDER BY hour ASC")
    suspend fun getForDate(date: String): List<HourlyStepEntity>

    @Query("SELECT COUNT(*) FROM hourly_steps WHERE date = :date")
    suspend fun countForDate(date: String): Int
}
