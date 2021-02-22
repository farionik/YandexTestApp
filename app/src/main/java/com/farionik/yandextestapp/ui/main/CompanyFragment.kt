package com.farionik.yandextestapp.ui.main

import android.os.Bundle
import android.view.View
import com.farionik.yandextestapp.ui.BaseFragment

class CompanyFragment : BaseFragment() {

    companion object {
        fun newInstance(): CompanyFragment {
            val args = Bundle()
            val fragment = CompanyFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.companyLiveData.observe(viewLifecycleOwner, { adapter.swapData(it) })
        /*mainViewModel.loadingAllDataProgress.observe(
            viewLifecycleOwner,
            { progressBar.visibility = if (it) View.VISIBLE else View.GONE }
        )*/
    }


}