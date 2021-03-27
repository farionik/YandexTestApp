package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import com.farionik.yandextestapp.repository.StockRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.repository.network.WebServicesProvider
import com.farionik.yandextestapp.repository.work_manager.DownloadStockWorkManager
import com.farionik.yandextestapp.repository.work_manager.RefreshStockWorkManager
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
open class StockViewModel(
    context: Context,
    private val appDatabase: AppDatabase,
    private val stockRepository: StockRepository,
    private val webServicesProvider: WebServicesProvider
) : BaseViewModel(context) {

    // величина скрола toolBar
    var appBarOffsetMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    val stocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().stocksRelationFlow(false)
            .asLiveData(viewModelScope.coroutineContext)

    val favouriteStocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().favouriteStocksFlow()
            .asLiveData(viewModelScope.coroutineContext)

    private val _downloadStockState = MutableLiveData<WorkInfo>()
    val downloadStockState: LiveData<WorkInfo>
        get() = _downloadStockState

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun updateData() {
        val refreshWorkRequest = OneTimeWorkRequestBuilder<RefreshStockWorkManager>()
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniqueWork(
            "refresh_request",
            ExistingWorkPolicy.REPLACE,
            refreshWorkRequest
        )
        workManager.getWorkInfoByIdLiveData(refreshWorkRequest.id)
            .observeForever { _downloadStockState.postValue(it) }
    }

    fun loadMoreStocks() {
        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadStockWorkManager>()
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniqueWork(
            "download_more_stocks",
            ExistingWorkPolicy.KEEP,
            downloadWorkRequest
        )
        workManager.getWorkInfoByIdLiveData(downloadWorkRequest.id)
            .observeForever { _downloadStockState.postValue(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelAllJob() {
        webServicesProvider.stopSocket()
    }

    fun likeStock(symbol: String) {
        stockRepository.likeStock(symbol)
    }
}