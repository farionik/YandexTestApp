package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.NewsRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.chart.createChartID
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CompanyViewModel(
    private val companyDetailRepository: CompanyRepository,
    private val newsRepository: NewsRepository,
    private val appDatabase: AppDatabase
) : ViewModel(), LifecycleObserver {

    // выбранная компания на просмотр
    private val _companyDetailSymbolLiveData = MutableLiveData<String>()
    val companySymbolLiveData: LiveData<String>
        get() = _companyDetailSymbolLiveData


    val selectedStock: LiveData<StockModelRelation>
        get() = Transformations.switchMap(companySymbolLiveData) {
            appDatabase.stockDAO().stockEntityLiveData(it)
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

    fun setSelectedCompanySymbol(symbol: String) {
        _companyDetailSymbolLiveData.value = symbol

        if (NetworkUtils.isConnected()) {
            viewModelScope.launch(IO) {
                newsRepository.fetchNews(symbol)
                companyDetailRepository.loadCompanyInfo(symbol)
            }
        }
    }

    fun likeCompany(symbol: String) {
        if (NetworkUtils.isConnected()) {
            viewModelScope.launch {
                companyDetailRepository.likeStock(symbol)
            }
        }
    }
}