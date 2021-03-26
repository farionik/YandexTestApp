package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.WorkManager

open class BaseViewModel(context: Context) : ViewModel(), LifecycleObserver {

    val workManager = WorkManager.getInstance(context)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

}