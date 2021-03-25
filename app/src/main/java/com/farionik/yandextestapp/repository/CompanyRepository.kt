package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange

interface CompanyRepository : StockRepository {
    suspend fun loadCompanyInfo(symbol: String)

    suspend fun loadCompanyCharts(symbol: String, chartRange: ChartRange)
}