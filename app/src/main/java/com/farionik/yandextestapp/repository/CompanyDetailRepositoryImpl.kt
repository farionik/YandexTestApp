package com.farionik.yandextestapp.repository

import android.content.Context
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

class CompanyDetailRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase
) : BaseRepository(context), CompanyDetailRepository,
    CompanyRepository by CompanyRepositoryImpl(context, api, appDatabase) {

    override suspend fun loadCompanyInfo(symbol: String): NetworkStatus {
        if (notConnectedToInternet()) {
            return NetworkStatus.ERROR(Throwable("Please check internet connection!"))
        }

        val response = api.loadCompany(symbol)
        Timber.d("loadCompany: $symbol code=${response.code()}")
        return if (response.isSuccessful) {
            val company = response.body() as CompanyEntity

            val companyEntity = appDatabase.companyDAO().companyEntity(symbol)
            companyEntity?.run {
                company.logo = logo
                company.latestPrice = latestPrice
                company.change = change
                company.changePercent = changePercent
            }
            appDatabase.companyDAO().update(company)
            coroutineScope {
                launch { loadStockPrice(symbol) }
            }
            NetworkStatus.SUCCESS
        } else {
            val errorMessage = response.message()
            NetworkStatus.ERROR(Throwable(errorMessage))
        }
    }

    override suspend fun loadCompanyCharts(symbol: String, chartRange: ChartRange) {
        coroutineScope {
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