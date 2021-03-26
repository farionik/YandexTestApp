package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import com.farionik.yandextestapp.repository.work_manager.DownloadCryptoWorkManager
import com.farionik.yandextestapp.repository.work_manager.RefreshCryptoWorkManger

class CryptoViewModel(
    context: Context
): ViewModel(), LifecycleObserver {

    private val workManager = WorkManager.getInstance(context)
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private val _downloadCryptoState = MutableLiveData<WorkInfo>()
    val downloadCryptoState: LiveData<WorkInfo>
        get() = _downloadCryptoState

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun loadStartData() {
        val refreshWorkRequest = OneTimeWorkRequestBuilder<RefreshCryptoWorkManger>()
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniqueWork(
            "refresh_crypto_request",
            ExistingWorkPolicy.REPLACE,
            refreshWorkRequest
        )
        workManager.getWorkInfoByIdLiveData(refreshWorkRequest.id)
            .observeForever { _downloadCryptoState.postValue(it) }
    }

    fun loadMore() {
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadCryptoWorkManager>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "download_more_crypto",
            ExistingWorkPolicy.KEEP,
            downloadWorkRequest
        )
        workManager.getWorkInfoByIdLiveData(downloadWorkRequest.id)
            .observeForever { _downloadCryptoState.postValue(it) }
    }

}