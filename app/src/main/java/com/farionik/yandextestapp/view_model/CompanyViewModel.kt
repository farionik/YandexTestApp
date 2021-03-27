package com.farionik.yandextestapp.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.workDataOf
import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.chart.createChartID
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import com.farionik.yandextestapp.repository.database.stock.CompanyEntity
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.repository.work_manager.DownloadChartWorkManager
import com.farionik.yandextestapp.repository.work_manager.DownloadCompanyInfoWorkManager
import com.farionik.yandextestapp.repository.work_manager.DownloadNewsWorkManager
import com.farionik.yandextestapp.ui.AppScreens
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class CompanyViewModel(
    context: Context,
    private val companyDetailRepository: CompanyRepository,
    private val appDatabase: AppDatabase,
    private val router: Router
) : BaseViewModel(context) {

    // выбранная компания на просмотр
    private val _companyDetailSymbolLiveData = MutableLiveData<String>()
    val companySymbolLiveData: LiveData<String>
        get() = _companyDetailSymbolLiveData

    val selectedStock: LiveData<StockModelRelation>
        get() = Transformations.switchMap(companySymbolLiveData) {
            appDatabase.stockDAO().stockModelRelationLiveData(it)
        }

    val selectedCompany: LiveData<CompanyEntity>
        get() = Transformations.switchMap(companySymbolLiveData) {
            appDatabase.companyDAO().companyEntityLiveData(it)
        }

    // выбранный график
    private val _selectedRangeLiveData = MutableLiveData<ChartRange>()
    val chartLiveData: LiveData<ChartEntity?>
        get() = Transformations.switchMap(_companyDetailSymbolLiveData) { symbol ->
            Transformations.switchMap(_selectedRangeLiveData) { range ->
                val chartID = createChartID(symbol, range)
                appDatabase.chartDAO().chartLiveData(chartID)
            }
        }

    // новости компании
    val newsLiveData: LiveData<List<NewsEntity>>
        get() = Transformations.switchMap(_companyDetailSymbolLiveData) {
            appDatabase.newsDAO().newsLiveData(it)
        }


    fun setChartRange(symbol: String, range: ChartRange) {
        _selectedRangeLiveData.value = range
        if (NetworkUtils.isConnected()) {
            viewModelScope.launch(IO) {
                companyDetailRepository.loadCompanyCharts(symbol, range)
            }
        }
    }

    private val _chartLoadingState = MutableLiveData<WorkInfo>()
    val chartLoadingState
        get() = _chartLoadingState

    private val _companyLoadingState = MutableLiveData<WorkInfo>()
    val companyLoadingState
        get() = _companyLoadingState

    private val _newsLoadingState = MutableLiveData<WorkInfo>()
    val newsLoadingState
        get() = _newsLoadingState


    fun openDetail(stockModelRelation: StockModelRelation) {
        router.navigateTo(AppScreens.companyDetailScreen(), false)

        val stockSymbol = stockModelRelation.stock.symbol
        _companyDetailSymbolLiveData.value = stockSymbol

        loadData(stockSymbol)
    }

    private fun loadData(symbol: String, loadAll: Boolean = true) {
        val data = workDataOf(
            "symbol" to symbol,
        )

        val newsWorkRequest = OneTimeWorkRequestBuilder<DownloadNewsWorkManager>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        val companyInfoRequest = OneTimeWorkRequestBuilder<DownloadCompanyInfoWorkManager>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        val companyChartRequest = OneTimeWorkRequestBuilder<DownloadChartWorkManager>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        var workers = listOf(newsWorkRequest, companyInfoRequest, companyChartRequest)
        if (!loadAll) {
            workers = listOf(newsWorkRequest);
        }

        workManager
            .beginWith(workers)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(companyChartRequest.id)
            .observeForever { _chartLoadingState.postValue(it) }

        workManager.getWorkInfoByIdLiveData(companyInfoRequest.id)
            .observeForever { _companyLoadingState.postValue(it) }

        workManager.getWorkInfoByIdLiveData(newsWorkRequest.id)
            .observeForever { _newsLoadingState.postValue(it) }
    }

    fun fetchNews(stockModelRelation: StockModelRelation?) {
        stockModelRelation?.stock?.let {
            loadData(it.symbol, false)
        }
    }

    fun likeCompany(symbol: String) {
        companyDetailRepository.likeStock(symbol)
    }

    fun backClick() {
        router.exit()
    }
}