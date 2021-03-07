package com.farionik.yandextestapp.ui.fragment.detail.chart

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.farionik.yandextestapp.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

@SuppressLint("ViewConstructor")
class ChartMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private var tvPrice: TextView = findViewById(R.id.tvPrice)
    private var tvDate: TextView = findViewById(R.id.tvDate)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {


        val price = e?.x
        val date = e?.y

        tvPrice.text = "$123.45"
        tvDate.text = "3 nov 2020"

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height + 4).toFloat())
    }

}