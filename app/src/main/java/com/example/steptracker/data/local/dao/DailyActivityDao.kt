package com.example.steptracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.steptracker.data.local.entity.DailyActivityEntity

@Dao
interface DailyActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: DailyActivityEntity)

    @Query("SELECT * FROM daily_activity WHERE date = :date")
    suspend fun getByDate(date: String): DailyActivityEntity?

    @Query("SELECT COUNT(*) FROM daily_activity")
    suspend fun count(): Int
}
