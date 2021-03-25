package com.farionik.yandextestapp.repository.work_manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.farionik.yandextestapp.repository.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DownloadStockWorkManager(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    companion object {
        const val KEY_COUNT = "KEY_COUNT"
    }

    private val stockRepository: StockRepository by inject()

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        return@withContext try {
            val count = inputData.getInt(KEY_COUNT, 0)
            stockRepository.loadMoreStocks(count)
        } catch (error: Throwable) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}