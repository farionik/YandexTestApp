package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange

interface CompanyRepository : StockRepository {
    suspend fun loadCompanyInfo(symbol: String): NetworkStatus

    suspend fun loadCompanyCharts(symbol: String, chartRange: ChartRange)
}