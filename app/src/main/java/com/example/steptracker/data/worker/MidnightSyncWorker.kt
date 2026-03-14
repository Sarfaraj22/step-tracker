package com.example.steptracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.steptracker.domain.use_case.sync.SyncStepRecordUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MidnightSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncStepRecordUseCase: SyncStepRecordUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return syncStepRecordUseCase()
            .fold(
                onSuccess = { Result.success() },
                onFailure = { Result.retry() }
            )
    }
}
