package com.farionik.yandextestapp.repository

import android.content.Context
import android.util.Log
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.*
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.SPStoredModel
import com.farionik.yandextestapp.repository.network.TOKEN
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import com.farionik.yandextestapp.ui.fragment.detail.chart.apiRange
import com.farionik.yandextestapp.ui.util.getFormattedCurrentDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*

class CompanyRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase
) : CompanyRepository {

    override suspend fun fetchCompanies() {
        coroutineScope {
            loadSP500().collect {
                it.add(0, SPStoredModel("YNDX", "Yandex"))
                val range = it.take(10)
                for (item in range) {
                    loadCompany(item.ticker)
                }
            }
        }
    }

    override suspend fun searchCompanies(searchRequest: String) {
        coroutineScope {
            val searchCompanies = api.searchCompanies(searchRequest, TOKEN)
            Log.i("TAG", "searchCompanies: ")
        }
    }

    private fun loadSP500(): Flow<MutableList<SPStoredModel>> = flow {
        val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }

        emit(Gson().fromJson(content, object : TypeToken<MutableList<SPStoredModel?>?>() {}.type))
    }

    private suspend fun loadCompany(symbol: String) {
        coroutineScope {
            launch(IO) {
                val entity = appDatabase.companyDAO().companyEntity(symbol)
                if (entity != null) {
                    //loadStockPrice(symbol)
                } else {
                    loadCompanyInfo(symbol)
                    launch { loadCompanyLogo(symbol) }
                    launch { loadStockPrice(symbol) }
                }
            }
        }
    }

    private suspend fun loadCompanyInfo(symbol: String) {
        val response = api.loadCompany(symbol, TOKEN)
        Log.i("TAG", "loadCompany: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val companyEntity = response.body()
            if (companyEntity != null) {
                appDatabase.companyDAO().insert(companyEntity)
            }
        }
    }

    private suspend fun loadCompanyLogo(symbol: String) {
        val response = api.loadCompanyLogo(symbol, TOKEN)
        Log.i("TAG", "loadCompanyLogo: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
            entity?.run {
                logo = response.body()?.url
                appDatabase.companyDAO().update(this)
            }
        }
    }

    private suspend fun loadStockPrice(symbol: String) {
        val response = api.loadCompanyPrice(symbol, TOKEN)
        Log.i("TAG", "loadStockPrice: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val body = response.body()
            val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
            entity?.run {
                price = body?.latestPrice
                change = body?.change
                changePercent = body?.changePercent
                appDatabase.companyDAO().update(this)
            }
        }
    }

    override fun companiesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().companyFlow()

    override fun favouriteCompaniesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().favouriteCompanyLiveData()

    override suspend fun likeCompany(symbol: String) {
        coroutineScope {
            val companyEntity = appDatabase.companyDAO().companyEntity(symbol)
            companyEntity?.let {
                companyEntity.isFavourite = !companyEntity.isFavourite
                appDatabase.companyDAO().update(companyEntity)
            }
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
                ChartRange.ALL -> api.loadChart(chartSymbol(), range.apiRange(), true, TOKEN)
                else -> api.loadChart(chartSymbol(), range.apiRange(), TOKEN)
            }
        }

        if (result.isSuccessful) {
            val chartValues: List<ChartValues>? = result.body()?.map {
                return@map ChartValues(it.label, it.high)
            }
            chartEntity.lastUpdate = getFormattedCurrentDate()
            chartEntity.values = chartValues

            appDatabase.chartDAO().update(chartEntity)
            Log.i("TAG", "loadCompanyCharts: ${chartEntity.chartSymbol()} complete")
        }
    }
}
