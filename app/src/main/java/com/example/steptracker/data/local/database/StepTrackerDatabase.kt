package com.example.steptracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.steptracker.data.local.dao.DailyActivityDao
import com.example.steptracker.data.local.dao.HourlyStepDao
import com.example.steptracker.data.local.entity.DailyActivityEntity
import com.example.steptracker.data.local.entity.HourlyStepEntity

@Database(
    entities = [DailyActivityEntity::class, HourlyStepEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class StepTrackerDatabase : RoomDatabase() {
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun hourlyStepDao(): HourlyStepDao
}
