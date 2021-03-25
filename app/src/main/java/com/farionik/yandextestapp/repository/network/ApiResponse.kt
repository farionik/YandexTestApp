package com.farionik.yandextestapp.repository.network

data class SPStoredModel(
    val ticker: String,
    val Name: String
)

data class LogoResponse(val url: String)

data class ChartResponse(
    val high: Float,
    val label: String
)