package com.example.steptracker.data.di

import android.content.Context
import androidx.room.Room
import com.example.steptracker.data.local.dao.StepDao
import com.example.steptracker.data.local.database.StepTrackerDatabase
import com.example.steptracker.data.repository.StepRepositoryImpl
import com.example.steptracker.domain.repository.StepRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindStepRepository(impl: StepRepositoryImpl): StepRepository

    companion object {

        @Provides
        @Singleton
        fun provideStepTrackerDatabase(
            @ApplicationContext context: Context
        ): StepTrackerDatabase =
            Room.databaseBuilder(
                context,
                StepTrackerDatabase::class.java,
                StepTrackerDatabase.DATABASE_NAME
            ).build()

        @Provides
        @Singleton
        fun provideStepDao(database: StepTrackerDatabase): StepDao =
            database.stepDao()
    }
}
