package com.farionik.yandextestapp.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class DownloadStockWorkManager(
    appContext: Context, workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val stockRepository : StockRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        return@withContext try {
            stockRepository.updateLocalData()
        } catch (error: Throwable) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

}