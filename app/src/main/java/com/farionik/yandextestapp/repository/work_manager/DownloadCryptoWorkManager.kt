package com.farionik.yandextestapp.repository.work_manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.farionik.yandextestapp.repository.CryptoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DownloadCryptoWorkManager(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val cryptoRepository: CryptoRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            cryptoRepository.loadMoreData()
            Result.success()
        } catch (error: Throwable) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}