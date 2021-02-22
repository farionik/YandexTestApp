package com.farionik.yandextestapp.ui.main

import android.os.Bundle
import android.view.View
import com.farionik.yandextestapp.ui.BaseFragment

class FavouriteFragment : BaseFragment() {

    companion object{
        fun newInstance(): FavouriteFragment {
            val args = Bundle()

            val fragment = FavouriteFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.favouriteCompanyLiveData.observe(viewLifecycleOwner, { adapter.swapData(it) })
    }
}