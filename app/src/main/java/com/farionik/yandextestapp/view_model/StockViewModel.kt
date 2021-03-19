package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.repository.LogoRepository
import com.farionik.yandextestapp.repository.StockRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.repository.network.WebServicesProvider
import com.farionik.yandextestapp.repository.network.noNetworkStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.lang.Exception

open class StockViewModel(
    private val appDatabase: AppDatabase,
    private val stockRepository: StockRepository,
    private val logoRepository: LogoRepository,
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

    private val _loadingStocksStateLiveData = MutableLiveData<NetworkStatus>()
    val loadingStocksStateLiveData: LiveData<NetworkStatus>
        get() = _loadingStocksStateLiveData

    private var loadingJob: Job? = null

    // получить список компаний по рейтенгу api
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fetchCompanies() {
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
                if (status == NetworkStatus.SUCCESS) {
                    _loadingStocksStateLiveData.postValue(NetworkStatus.LOADING("Please wait..."))
                    logoRepository.loadCompaniesLogo()
                    _loadingStocksStateLiveData.postValue(status)
                } else {
                    _loadingStocksStateLiveData.postValue(status)
                }
            }
        } else {
            _loadingStocksStateLiveData.value = noNetworkStatus
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelAllJob() {
        webServicesProvider.stopSocket()
        loadingJob?.cancel()
    }

    fun likeStock(symbol: String) {
        viewModelScope.launch(IO) {
            stockRepository.likeStock(symbol)
        }
    }
}