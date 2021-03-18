package com.farionik.yandextestapp.repository.network.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class LoadCompaniesWork(
    appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return Result.failure()
    }
}