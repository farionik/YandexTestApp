package com.farionik.yandextestapp.ui.fragment.detail

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
import com.github.mikephil.charting.formatter.IFillFormatter

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
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(ContextCompat.getColor(binding.chart.context, R.color.black))
            //highLightColor = Color.rgb(244, 117, 117)
            color = ContextCompat.getColor(binding.chart.context, R.color.black)
            fillColor = Color.WHITE
            fillAlpha = 100
            setDrawHorizontalHighlightIndicator(false)
            /*set1.setFillFormatter(IFillFormatter { dataSet, dataProvider ->
                chart.getAxisLeft().getAxisMinimum()
            })*/
        }
        val data = LineData(lineDataSet)
//        data.setValueTypeface(tfLight)
        data.setValueTextSize(9f)
        data.setDrawValues(false)

        binding.chart.run {
            setViewPortOffsets(0f, 0f, 0f, 0f)
            setBackgroundColor(Color.WHITE)
            description.isEnabled = false
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawGridBackground(false)
            legend.isEnabled = false
            //setBackgroundColor(Color.rgb())
            setData(data)
            invalidate()
        }


    }
}