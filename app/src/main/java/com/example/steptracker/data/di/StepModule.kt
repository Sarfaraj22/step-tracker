package com.example.steptracker.data.di

import com.example.steptracker.data.local.dao.DailyActivityDao
import com.example.steptracker.data.local.dao.HourlyStepDao
import com.example.steptracker.data.local.database.StepTrackerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StepModule {

    @Provides
    fun provideDailyActivityDao(db: StepTrackerDatabase): DailyActivityDao =
        db.dailyActivityDao()

    @Provides
    fun provideHourlyStepDao(db: StepTrackerDatabase): HourlyStepDao =
        db.hourlyStepDao()
}
