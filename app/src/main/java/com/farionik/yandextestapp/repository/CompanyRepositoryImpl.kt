package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.*
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import com.farionik.yandextestapp.ui.fragment.detail.chart.apiRange
import com.farionik.yandextestapp.ui.util.getFormattedCurrentDate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class CompanyRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase
) : BaseRepository(), CompanyRepository,
    StockRepository by StockRepositoryImpl(api, appDatabase) {

    override suspend fun loadCompanyInfo(symbol: String): NetworkStatus =
        when (checkInternetConnection()) {
            is NetworkStatus.ERROR -> checkInternetConnection()
            else -> {
                val response = api.loadCompanyInfo(symbol)
                if (response.isSuccessful) {
                    val company = response.body() as CompanyEntity
                    appDatabase.companyDAO().insert(company)
                    coroutineScope {
                        launch { loadStockPrice(symbol) }
                    }
                    NetworkStatus.SUCCESS
                } else {
                    val errorMessage = response.message()
                    NetworkStatus.ERROR(Throwable(errorMessage))
                }
            }
        }

    override suspend fun loadCompanyCharts(symbol: String, chartRange: ChartRange) {
        val chartID = createChartID(symbol, chartRange)
        val chartEntity = appDatabase.chartDAO().chartEntity(chartID)
        if (chartEntity == null) {
            createChartData(chartID)
        } else {
            if (chartEntity.isNeedUpdate()) {
                updateChartData(chartEntity)
            }
        }
    }

    private suspend fun createChartData(chartID: String) {
        // создать уникальный id для графика
        val chartEntity = ChartEntity(chartID)
        appDatabase.chartDAO().insert(chartEntity)
        updateChartData(chartEntity)
    }

    private suspend fun updateChartData(chartEntity: ChartEntity) {
        // для 6m, y, max грузить chartSimplify
        val result = chartEntity.run {
            when (val range = chartRange()) {
                ChartRange.HALF_YEAR,
                ChartRange.YEAR,
                ChartRange.ALL -> api.loadChart(chartSymbol(), range.apiRange(), true)
                else -> api.loadChart(chartSymbol(), range.apiRange())
            }
        }

        if (result.isSuccessful) {
            val chartValues: List<ChartValues>? = result.body()?.map {
                return@map ChartValues(it.label, it.high)
            }
            chartEntity.lastUpdate = getFormattedCurrentDate()
            chartEntity.values = chartValues

            appDatabase.chartDAO().update(chartEntity)
            Timber.i("loadCompanyCharts: ${chartEntity.chartSymbol()} complete")
        }
    }

}