package com.farionik.yandextestapp.view_model

import androidx.lifecycle.*
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.chart.createChartID
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import com.farionik.yandextestapp.ui.fragment.detail.chart.apiRange
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CompanyDetailViewModel(
    private val companyRepository: CompanyRepository,
    private val appDatabase: AppDatabase
) : CompanyViewModel(companyRepository, appDatabase) {

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

    fun setChartRange(symbol: String, range: ChartRange) {
        _selectedRangeLiveData.value = range
        viewModelScope.launch(IO) {
            companyRepository.loadCompanyCharts(symbol, range)
        }
    }

    fun setCompanyDetail(symbol: String) {
        _companyDetailSymbolLiveData.value = symbol
    }
}