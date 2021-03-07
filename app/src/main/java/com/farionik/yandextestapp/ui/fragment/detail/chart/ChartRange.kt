package com.farionik.yandextestapp.ui.fragment.detail.chart

enum class ChartRange {
    DAY, WEEK, MONTH, HALF_YEAR, YEAR, ALL
}

fun ChartRange.apiRange() : String = when(this) {
    ChartRange.DAY -> "1d"
    ChartRange.WEEK -> "1w"
    ChartRange.MONTH -> "1m"
    ChartRange.HALF_YEAR -> "6m"
    ChartRange.YEAR -> "1y"
    ChartRange.ALL -> "max"
}