package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.work_manager.RefreshCryptoWorkManger

class CryptoViewModel(
    context: Context,
    appDatabase: AppDatabase
) : BaseViewModel(context) {

    private val _downloadCryptoState = MutableLiveData<WorkInfo>()
    val downloadCryptoState: LiveData<WorkInfo>
        get() = _downloadCryptoState

    val cryptoLiveData =
        appDatabase.cryptoDAO().cryptoFlow().asLiveData(viewModelScope.coroutineContext)

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
}