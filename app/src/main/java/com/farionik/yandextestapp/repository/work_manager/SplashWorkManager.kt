package com.farionik.yandextestapp.repository.work_manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.farionik.yandextestapp.repository.CryptoRepository
import com.farionik.yandextestapp.repository.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class SplashWorkManager(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val stockRepository by inject<StockRepository>()
    private val cryptoRepository by inject<CryptoRepository>()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            stockRepository.loadStartData()
            cryptoRepository.loadStartData()
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