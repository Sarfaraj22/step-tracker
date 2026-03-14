package com.example.steptracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.steptracker.data.local.converter.Converters
import com.example.steptracker.data.local.dao.DailyActivityDao
import com.example.steptracker.data.local.dao.HourlyStepDao
import com.example.steptracker.data.local.dao.StepDao
import com.example.steptracker.data.local.entity.DailyActivityEntity
import com.example.steptracker.data.local.entity.HourlyStepEntity
import com.example.steptracker.data.local.entity.StepRecordEntity

@Database(
    entities = [
        StepRecordEntity::class,
        DailyActivityEntity::class,
        HourlyStepEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class StepTrackerDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun hourlyStepDao(): HourlyStepDao

    companion object {
        const val DATABASE_NAME = "step_tracker_db"
    }
}
