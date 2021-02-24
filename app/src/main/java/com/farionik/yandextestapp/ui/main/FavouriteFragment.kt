package com.farionik.yandextestapp.ui.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.farionik.yandextestapp.ui.BaseFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteFragment : BaseFragment() {

    companion object {
        fun newInstance(): FavouriteFragment {
            val args = Bundle()

            val fragment = FavouriteFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            mainViewModel.favouriteCompanyLiveData.collect {
                adapter.swapData(it)
            }
        }
    }
}