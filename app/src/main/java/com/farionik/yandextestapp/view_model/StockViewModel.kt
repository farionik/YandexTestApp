package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.farionik.yandextestapp.di.Containers
import com.farionik.yandextestapp.di.Containers.*
import com.farionik.yandextestapp.di.LocalCiceroneHolder
import com.farionik.yandextestapp.repository.StockRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.repository.network.WebServicesProvider
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class StockViewModel(
    private val appDatabase: AppDatabase,
    private val stockRepository: StockRepository,
    private val webServicesProvider: WebServicesProvider
) : ViewModel(), LifecycleObserver {

    // величина скрола toolBar
    var appBarOffsetMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    val stocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().stocksFlow()
            .asLiveData(viewModelScope.coroutineContext)

    val favouriteStocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().favouriteStocksFlow()
            .asLiveData(viewModelScope.coroutineContext)


    private val _loadingStocksStateLiveData = MutableLiveData<NetworkState>()
    val loadingStocksStateLiveData: LiveData<NetworkState>
        get() = _loadingStocksStateLiveData


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun loadStartData() {
        fetchCompanies(0)
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

    suspend fun likeStock(symbol: String) = stockRepository.likeStock(symbol)
}