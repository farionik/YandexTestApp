package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange

interface CompanyRepository : StockRepository {
    suspend fun loadCompanyInfo(symbol: String): NetworkState

    suspend fun loadCompanyCharts(symbol: String, chartRange: ChartRange)
}