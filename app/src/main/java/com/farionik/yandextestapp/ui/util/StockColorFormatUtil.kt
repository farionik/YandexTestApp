package com.farionik.yandextestapp.ui.util

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.CompanyEntity

fun TextView.formatPriceValue(companyEntity: CompanyEntity) {
    val totalPriceText = "$${companyEntity.latestPrice}"
    text = totalPriceText
}

fun TextView.formatChangeValue(companyEntity: CompanyEntity) {
    companyEntity.change?.let {
        val changeText = "$$it"
        text = changeText
        setTextColor(getColorForTextView(it, context))
    }
}

fun TextView.formatPercentValue(companyEntity: CompanyEntity) {
    companyEntity.changePercent?.let {
        val percentText = " ($it)"
        text = percentText
        setTextColor(getColorForTextView(it, context))
    }
}

private fun getColorForTextView(value: Double, context: Context) =
    if (value >= 0.0) ContextCompat.getColor(context, R.color.color_percent_green)
    else ContextCompat.getColor(context, R.color.color_percent_red)