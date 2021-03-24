package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import com.farionik.yandextestapp.repository.DownloadStockWorkManager
import com.farionik.yandextestapp.repository.StockRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.repository.network.WebServicesProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class StockViewModel(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val stockRepository: StockRepository,
    private val webServicesProvider: WebServicesProvider
) : ViewModel(), LifecycleObserver {

    private val workManager = WorkManager.getInstance(context)

    // величина скрола toolBar
    var appBarOffsetMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    val stocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().stocksRelationFlow(false)
            .asLiveData(viewModelScope.coroutineContext)

    val favouriteStocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().favouriteStocksFlow()
            .asLiveData(viewModelScope.coroutineContext)

    private val _loadingStocksStateLiveData = MutableLiveData<NetworkState>()
    val loadingStocksStateLiveData: LiveData<NetworkState>
        get() = _loadingStocksStateLiveData



    private val _downloadStockState = MutableLiveData<WorkInfo>()
    val downloadStockState: LiveData<WorkInfo>
        get() = _downloadStockState

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun updateData() {
        //fetchCompanies(0)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val downloadWorkRequest = OneTimeWorkRequestBuilder<DownloadStockWorkManager>()
            .setConstraints(constraints)
            .build()
        workManager.enqueue(downloadWorkRequest)
        workManager.getWorkInfoByIdLiveData(downloadWorkRequest.id)
            .observeForever { _downloadStockState.postValue(it) }
        //workManager.enqueue(OneTimeWorkRequest.from(DownloadStockWorkManager::class.java))
    }

    private var loadingJob: Job? = null

    fun fetchCompanies(totalCount: Int) {
        cancelAllJob()
        // подключения sse. Только быстро съедает кредиты api
//        webServicesProvider.stopSocket()

        _loadingStocksStateLiveData.postValue(NetworkState.LOADING("Loading companies..."))
        loadingJob = viewModelScope.launch(Dispatchers.IO) {
            val state = try {
                stockRepository.loadMoreStocks(totalCount)
            } catch (e: Exception) {
                if (e is CancellationException) NetworkState.SUCCESS
                else NetworkState.ERROR(Throwable(e.message))
            }
            _loadingStocksStateLiveData.postValue(state)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelAllJob() {
        webServicesProvider.stopSocket()
        loadingJob?.cancel()
    }

    fun likeStock(symbol: String) {
        viewModelScope.launch {
            stockRepository.likeStock(symbol)
        }
    }
}