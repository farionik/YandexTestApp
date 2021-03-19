package com.farionik.yandextestapp.ui.fragment.detail.chart

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentChartBinding
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange.*
import com.farionik.yandextestapp.ui.util.formatChangeValue
import com.farionik.yandextestapp.ui.util.formatPercentValue
import com.farionik.yandextestapp.ui.util.formatPriceValue
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartFragment : BaseFragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var currentSymbol: String

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
        initRadioButtons()
        subscribeViewModel()
    }

    private fun initChart() {
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
            invalidate()
        }
    }

    private fun setChartData(data: ChartEntity?) {
        lifecycleScope.launch(Dispatchers.Main) {
            val entries = mutableListOf<Entry>()

            data?.values?.let {
                for (value in it) {
                    if (value.price > 0) {
                        val entry = Entry(it.indexOf(value).toFloat(), value.price)
                        entry.data = value
                        entries.add(entry)
                    }
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
                        ContextCompat.getDrawable(
                            binding.chart.context,
                            R.drawable.chart_fill_background
                        )
                    fillAlpha = 10
                }

                binding.chart.data = LineData(lineDataSet).apply {
                    setValueTextSize(9f)
                    setDrawValues(false)
                }
                binding.chart.invalidate()
            }
        }
    }

    private fun initRadioButtons() {
        binding.btnChartDay.setOnClickListener { setButtonChecked(it.id) }
        binding.btnChartWeek.setOnClickListener { setButtonChecked(it.id) }
        binding.btnChartMonth.setOnClickListener { setButtonChecked(it.id) }
        binding.btnChart6Month.setOnClickListener { setButtonChecked(it.id) }
        binding.btnChartYear.setOnClickListener { setButtonChecked(it.id) }
        binding.btnChartAll.setOnClickListener { setButtonChecked(it.id) }
    }

    private fun setButtonChecked(buttonId: Int) {
        binding.btnChartDay.isChecked = buttonId == R.id.btnChartDay
        binding.btnChartWeek.isChecked = buttonId == R.id.btnChartWeek
        binding.btnChartMonth.isChecked = buttonId == R.id.btnChartMonth
        binding.btnChart6Month.isChecked = buttonId == R.id.btnChart6Month
        binding.btnChartYear.isChecked = buttonId == R.id.btnChartYear
        binding.btnChartAll.isChecked = buttonId == R.id.btnChartAll


        binding.chart.run {
            clear()
            invalidate()
        }

        when (buttonId) {
            R.id.btnChartDay -> companyViewModel.setChartRange(currentSymbol, DAY)
            R.id.btnChartWeek -> companyViewModel.setChartRange(currentSymbol, WEEK)
            R.id.btnChartMonth -> companyViewModel.setChartRange(currentSymbol, MONTH)
            R.id.btnChart6Month -> companyViewModel.setChartRange(currentSymbol, HALF_YEAR)
            R.id.btnChartYear -> companyViewModel.setChartRange(currentSymbol, YEAR)
            R.id.btnChartAll -> companyViewModel.setChartRange(currentSymbol, ALL)
            else -> {

            }
        }
    }

    private fun subscribeViewModel() {
        companyViewModel.companySymbolLiveData.observe(viewLifecycleOwner, {
            currentSymbol = it
            setButtonChecked(R.id.btnChartDay)
        })

        companyViewModel.selectedStock.observe(viewLifecycleOwner, {
            with(binding) {
                lastPrice.formatPriceValue(it.stock)
                tvChange.formatChangeValue(it.stock)
                tvPercentChange.formatPercentValue(it.stock)

                val buyButtonText = "Buy for $${it.stock.latestPrice}"
                btnBuy.text = buyButtonText
            }
        })

        companyViewModel.chartLiveData.observe(viewLifecycleOwner, { setChartData(it) })
    }
}