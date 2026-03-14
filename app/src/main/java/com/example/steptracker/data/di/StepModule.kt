package com.example.steptracker.data.di

import android.content.Context
import androidx.room.Room
import com.example.steptracker.data.local.dao.DailyActivityDao
import com.example.steptracker.data.local.dao.HourlyStepDao
import com.example.steptracker.data.local.database.StepTrackerDatabase
import com.example.steptracker.data.repository.StepRepositoryImpl
import com.example.steptracker.domain.repository.StepRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StepModule {

    @Binds
    @Singleton
    abstract fun bindStepRepository(impl: StepRepositoryImpl): StepRepository

    companion object {

        @Provides
        @Singleton
        fun provideStepTrackerDatabase(
            @ApplicationContext context: Context,
        ): StepTrackerDatabase = Room.databaseBuilder(
            context,
            StepTrackerDatabase::class.java,
            "step_tracker_db",
        ).build()

        @Provides
        fun provideDailyActivityDao(db: StepTrackerDatabase): DailyActivityDao =
            db.dailyActivityDao()

        @Provides
        fun provideHourlyStepDao(db: StepTrackerDatabase): HourlyStepDao =
            db.hourlyStepDao()

        @Provides
        @Singleton
        fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}
