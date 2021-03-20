package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.farionik.yandextestapp.repository.StockRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.repository.network.WebServicesProvider
import com.farionik.yandextestapp.repository.pagination.StockPagingSource
import com.farionik.yandextestapp.repository.pagination.StockPagingSource.Companion.PAGE_SIZE
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


    val stockFlow = Pager(PagingConfig(PAGE_SIZE, PAGE_SIZE)) {
        StockPagingSource(stockRepository)
    }.flow.cachedIn(viewModelScope)


    val stocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().stocksFlow()
            .asLiveData(viewModelScope.coroutineContext)

    val favouriteStocksLiveData: LiveData<List<StockModelRelation>>
        get() = appDatabase.stockDAO().favouriteStocksFlow()
            .asLiveData(viewModelScope.coroutineContext)


    private val _loadingStocksStateLiveData = MutableLiveData<NetworkState>()
    val loadingStocksStateLiveData: LiveData<NetworkState>
        get() = _loadingStocksStateLiveData


    fun fetchCompanies(page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingStocksStateLiveData.postValue(NetworkState.LOADING("Loading companies..."))
            try {
                val state = stockRepository.loadStockPage(page)
                _loadingStocksStateLiveData.postValue(state)
            } catch (e: Exception) {
                _loadingStocksStateLiveData.postValue(NetworkState.ERROR(e))
            }
        }
    }


    private var loadingJob: Job? = null

    // получить список компаний по рейтенгу api
    /*fun fetchCompanies() {
        cancelAllJob()
        // подключения sse. Только быстро съедает кредиты api
//        webServicesProvider.stopSocket()

        if (NetworkUtils.isConnected()) {
            _loadingStocksStateLiveData.value = NetworkStatus.LOADING("Loading companies...")
            loadingJob = viewModelScope.launch(IO) {
                val status = try {
                    stockRepository.fetchStocks()
                } catch (e: Exception) {
                    if (e is CancellationException) NetworkStatus.SUCCESS
                    else NetworkStatus.ERROR(Throwable(e.message))
                }
                _loadingStocksStateLiveData.postValue(status)
            }
        } else {
            _loadingStocksStateLiveData.value = noNetworkStatus
        }
    }*/

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelAllJob() {
        webServicesProvider.stopSocket()
        loadingJob?.cancel()
    }

    suspend fun likeStock(symbol: String) = stockRepository.likeStock(symbol)
}