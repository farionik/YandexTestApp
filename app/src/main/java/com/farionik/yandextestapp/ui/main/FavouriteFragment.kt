package com.farionik.yandextestapp.ui.main

import android.os.Bundle
import android.view.View
import com.farionik.yandextestapp.ui.BaseFragment

class FavouriteFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.favouriteCompanyLiveData.observe(viewLifecycleOwner, { adapter.swapData(it) })
    }
}