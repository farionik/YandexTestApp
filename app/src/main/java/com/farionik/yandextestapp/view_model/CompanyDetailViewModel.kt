package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.farionik.yandextestapp.repository.CompanyDetailRepositoryImpl
import com.farionik.yandextestapp.repository.NewsRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.chart.createChartID
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

@ObsoleteCoroutinesApi
class CompanyDetailViewModel(
    private val companyDetailRepository: CompanyDetailRepositoryImpl,
    private val newsRepository: NewsRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {

    // выбранная компания на просмотр
    private val _companyDetailSymbolLiveData = MutableLiveData<String>()
    val companyDetailModelLiveData: LiveData<CompanyEntity>
        get() = Transformations.switchMap(_companyDetailSymbolLiveData) {
            if (it.isNullOrEmpty()) {
                return@switchMap null
            }
            return@switchMap appDatabase.companyDAO().companyEntityLiveData(it)
        }

    private val _selectedRangeLiveData = MutableLiveData<ChartRange>()
    val chartLiveData: LiveData<ChartEntity?>
        get() = Transformations.switchMap(_companyDetailSymbolLiveData) { symbol ->
            Transformations.switchMap(_selectedRangeLiveData) { range ->
                val chartID = createChartID(symbol, range)
                appDatabase.chartDAO().chartLiveData(chartID)
            }
        }

    // новости компании
    val newsLiveData: LiveData<List<NewsEntity>> =
        Transformations.switchMap(_companyDetailSymbolLiveData) {
            appDatabase.newsDAO().newsLiveData(it)
        }


    fun setChartRange(symbol: String, range: ChartRange) {
        _selectedRangeLiveData.value = range
        viewModelScope.launch(IO) {
            companyDetailRepository.loadCompanyCharts(symbol, range)
        }
    }

    fun setCompanyDetail(symbol: String) {
        _companyDetailSymbolLiveData.value = symbol
        viewModelScope.launch(IO) {
            newsRepository.fetchNews(symbol)
            companyDetailRepository.loadCompanyInfo(symbol)
        }
    }

    fun likeCompany(symbol: String) {
        viewModelScope.launch {
            companyDetailRepository.likeCompany(symbol)
        }
    }
}