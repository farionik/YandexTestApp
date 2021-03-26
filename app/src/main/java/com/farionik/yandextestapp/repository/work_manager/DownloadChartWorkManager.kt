package com.farionik.yandextestapp.repository.work_manager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class DownloadChartWorkManager(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val companyRepository: CompanyRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val symbol = inputData.getString("symbol") as String
            companyRepository.loadCompanyCharts(symbol, ChartRange.DAY)
            companyRepository.loadCompanyCharts(symbol, ChartRange.WEEK)
            companyRepository.loadCompanyCharts(symbol, ChartRange.MONTH)
            companyRepository.loadCompanyCharts(symbol, ChartRange.HALF_YEAR)
            companyRepository.loadCompanyCharts(symbol, ChartRange.YEAR)
            companyRepository.loadCompanyCharts(symbol, ChartRange.ALL)
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