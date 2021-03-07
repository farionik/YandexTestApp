package com.farionik.yandextestapp.repository.network

data class SPStoredModel(val ticker: String, val Name: String)

data class LogoResponse(val url: String)

data class PriceResponse(
    val latestPrice: Double,
    val change: Double,
    val changePercent: Double
)

data class ChartResponse(val high: Float)
