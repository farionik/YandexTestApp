package com.farionik.yandextestapp.ui.util

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.stock.StockEntity

fun TextView.formatPriceValue(stockEntity: StockEntity) {
    val totalPriceText = "$${stockEntity.latestPrice}"
    text = totalPriceText
}

fun TextView.formatChangeValue(stockEntity: StockEntity) {
    stockEntity.change.let {
        val changeText = "$$it"
        text = changeText
        setTextColor(getColorForTextView(it, context))
    }
}

fun TextView.formatPercentValue(stockEntity: StockEntity) {
    stockEntity.changePercent.let {
        val percentText = " ($it)"
        text = percentText
        setTextColor(getColorForTextView(it, context))
    }
}

private fun getColorForTextView(value: Double, context: Context) =
    if (value >= 0.0) ContextCompat.getColor(context, R.color.color_percent_green)
    else ContextCompat.getColor(context, R.color.color_percent_red)