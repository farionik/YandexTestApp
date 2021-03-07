package com.farionik.yandextestapp.ui.fragment.detail.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentChartBinding
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import com.farionik.yandextestapp.ui.util.formatChangeValue
import com.farionik.yandextestapp.ui.util.formatPercentValue
import com.farionik.yandextestapp.ui.util.formatPriceValue
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartFragment : BaseFragment() {

    private lateinit var binding: FragmentChartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initChart()

        companyDetailViewModel.companyDetailModelLiveData.observe(viewLifecycleOwner, {
            with(binding) {
                lastPrice.formatPriceValue(it)
                tvChange.formatChangeValue(it)
                tvPercentChange.formatPercentValue(it)

                val buyButtonText = "Buy for $${it.price}"
                btnBuy.text = buyButtonText
            }
        })
    }

    private fun initChart() {

        val entries = mutableListOf<Entry>()
        for (i in 0..40) {
            entries.add(Entry(i.toFloat(), Math.random().toFloat()))
        }
        val lineDataSet = LineDataSet(entries, "Data set").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            setDrawFilled(true)
            setDrawCircles(false)
            setDrawHighlightIndicators(false)
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(ContextCompat.getColor(binding.chart.context, R.color.black))
            color = ContextCompat.getColor(binding.chart.context, R.color.black)
            fillDrawable =
                ContextCompat.getDrawable(binding.chart.context, R.drawable.chart_fill_background)
            fillAlpha = 10
        }
        val data = LineData(lineDataSet).apply {
            setValueTextSize(9f)
            setDrawValues(false)
        }

        binding.chart.run {
            setViewPortOffsets(0f, 0f, 0f, 0f)
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            isDragEnabled = false
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            legend.isEnabled = false

            setDrawGridBackground(false)
            setGridBackgroundColor(Color.WHITE)

            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)

            axisLeft.setDrawGridLines(false)
            axisLeft.axisLineColor = Color.WHITE

            axisRight.setDrawGridLines(false)

            animateXY(500, 500)

            val markerView = ChartMarkerView(this.context, R.layout.custom_marker_view)
            markerView.chartView = this
            marker = markerView

            setData(data)
        }
        binding.chart.invalidate()
    }
}