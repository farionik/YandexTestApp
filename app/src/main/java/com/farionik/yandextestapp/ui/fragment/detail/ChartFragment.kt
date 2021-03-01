package com.farionik.yandextestapp.ui.fragment.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentChartBinding
import com.farionik.yandextestapp.ui.fragment.BaseFragment
import com.farionik.yandextestapp.ui.util.formatChangeValue
import com.farionik.yandextestapp.ui.util.formatPercentValue
import com.farionik.yandextestapp.ui.util.formatPriceValue

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

        companyViewModel.companyDetailModelLiveData.observe(viewLifecycleOwner, {
            with(binding) {
                lastPrice.formatPriceValue(it)
                tvChange.formatChangeValue(it)
                tvPercentChange.formatPercentValue(it)

                btnBuy.text = "Buy for $${it.price}"
            }
        })
    }
}