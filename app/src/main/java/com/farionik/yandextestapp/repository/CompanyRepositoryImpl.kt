package com.farionik.yandextestapp.repository

import android.content.Context
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.chart.*
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.IEXSymbolsResponse
import com.farionik.yandextestapp.repository.network.SPStoredModel
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import com.farionik.yandextestapp.ui.fragment.detail.chart.apiRange
import com.farionik.yandextestapp.ui.util.getFormattedCurrentDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class CompanyRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase
) : CompanyRepository {

    override suspend fun fetchCompanies() {
        coroutineScope {
            val response = api.loadIEXSymbols()
            if (response.isSuccessful) {

                val body = response.body() as List<IEXSymbolsResponse>
                val filtered = body.filter { it.isEnabled }

                launch(IO) {
                    loadCompany("YNDX")
                    //loadCompany(item.symbol)
                }
                /*launch(IO) {
                    loadCompany("YNDX")
                }*/
                val range = filtered.take(50)
                for (item in range) {
                    launch(IO) {
                        loadCompany(item.symbol)
                    }
                }

                Timber.d("")

            } else {
                // TODO: 3/13/21 Показать ошибку 
                // не удалось получить тикеры компаний

                // возможно надо загрузить 500 локальных

                // если нет соединения, показать об этом сообщение
            }


            /* loadSP500().collect {
                 it.add(0, SPStoredModel("YNDX", "Yandex"))
                 val range = it.take(5)
                 for (item in range) {
                     launch(IO) {
                         loadCompany(item.ticker)
                     }
                 }
             }*/
        }
    }

    override suspend fun updateCompany(symbol: String) {
        loadStockPrice(symbol)
    }

    override suspend fun searchCompanies(searchRequest: String) {
        coroutineScope {
            val searchCompanies = api.searchCompanies(searchRequest)
            Timber.i("searchCompanies: ")
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
            val entity = appDatabase.companyDAO().companyEntity(symbol)
            if (entity != null) {
                updateCompany(symbol)
            } else {
                loadCompanyInfo(symbol)
            }
        }
    }

    private suspend fun loadCompanyInfo(symbol: String) {
        val response = api.loadCompany(symbol)
        Timber.i("loadCompany: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val companyEntity = response.body()
            if (companyEntity != null) {
                appDatabase.companyDAO().insert(companyEntity)
                coroutineScope {
                    launch { loadCompanyLogo(symbol) }
                    launch { loadStockPrice(symbol) }
                }
            }
        }
    }

    private suspend fun loadCompanyLogo(symbol: String) {
        val response = api.loadCompanyLogo(symbol)
        Timber.i("loadCompanyLogo: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
            entity?.run {
                logo = response.body()?.url
                appDatabase.companyDAO().update(this)
            }
        }
    }

    private suspend fun loadStockPrice(symbol: String) {
        val response = api.loadCompanyPrice(symbol)
        Timber.i("loadStockPrice: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val body = response.body()
            val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
            Timber.d(body.toString())
            entity?.run {
                price = body?.latestPrice
                change = body?.change
                changePercent = body?.changePercent


                try {
                    appDatabase.companyDAO().update(this)
                } catch (e: Exception) {
                    Timber.d(e)
                }
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
