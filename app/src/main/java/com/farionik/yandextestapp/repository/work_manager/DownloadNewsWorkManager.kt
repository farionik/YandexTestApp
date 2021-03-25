package com.farionik.yandextestapp.repository.work_manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.farionik.yandextestapp.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DownloadNewsWorkManager (appContext: Context, workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val newsRepository: NewsRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val symbol = inputData.getString("symbol") as String
            newsRepository.fetchNews(symbol)
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