package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.di.Containers
import com.farionik.yandextestapp.di.LocalCiceroneHolder
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.NewsRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.chart.createChartID
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import com.farionik.yandextestapp.ui.AppScreens
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CompanyViewModel(
    private val companyDetailRepository: CompanyRepository,
    private val newsRepository: NewsRepository,
    private val appDatabase: AppDatabase,
    private val router: Router
) : ViewModel(), LifecycleObserver {

    /*private val router: Router
        get() = localCiceroneHolder.getCicerone(Containers.MAIN_FRAGMENT_CONTAINER.name).router*/

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

    fun openDetail(stockModelRelation: StockModelRelation) {
        router.navigateTo(AppScreens.companyDetailScreen())

        val stockSymbol = stockModelRelation.stock.symbol

        _companyDetailSymbolLiveData.value = stockSymbol

        if (NetworkUtils.isConnected()) {
            viewModelScope.launch(IO) {
                newsRepository.fetchNews(stockSymbol)
                companyDetailRepository.loadCompanyInfo(stockSymbol)
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

    fun backClick() {
        router.exit()
    }
}