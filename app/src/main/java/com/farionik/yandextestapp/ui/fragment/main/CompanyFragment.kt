package com.farionik.yandextestapp.ui.fragment.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkStatus

class CompanyFragment : BaseListFragment() {
    override val dataSource: LiveData<List<StockModelRelation>>
        get() = stockViewModel.stocksLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stockViewModel.loadingStocksStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is NetworkStatus.SUCCESS -> hideProgress()
                is NetworkStatus.LOADING -> showProgress(it.message)
                is NetworkStatus.ERROR -> showError(
                    it.e.message ?: "Error when load companies. Try later"
                )
            }
        })
    }
}