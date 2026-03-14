package com.example.steptracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.steptracker.data.local.converter.Converters
import com.example.steptracker.data.local.dao.StepDao
import com.example.steptracker.data.local.entity.StepRecordEntity

@Database(
    entities = [StepRecordEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class StepTrackerDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao

    companion object {
        const val DATABASE_NAME = "step_tracker_db"
    }
}
