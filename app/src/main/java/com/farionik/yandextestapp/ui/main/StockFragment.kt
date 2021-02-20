package com.farionik.yandextestapp.ui.main

import android.os.Bundle
import android.view.View
import com.farionik.yandextestapp.data.StockEntity
import com.farionik.yandextestapp.ui.BaseFragment
import com.farionik.yandextestapp.ui.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class StockFragment : BaseFragment() {

    private val mainViewModel by viewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.stockLiveData.observe(viewLifecycleOwner, {adapter.swapData(it)})
    }
}