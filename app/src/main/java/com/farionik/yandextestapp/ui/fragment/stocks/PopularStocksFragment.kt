package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.ui.adapter.PaginationListener

class PopularStocksFragment : BaseStockFragment() {

    private var networkState: NetworkState? = null
    override val stockSource: LiveData<List<StockModelRelation>>
        get() = stockViewModel.stocksLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout.setOnRefreshListener { stockViewModel.updateData() }

        recyclerView.addOnScrollListener(object : PaginationListener() {
            override fun loadMoreItems(totalCount: Int) {
                stockViewModel.fetchCompanies(totalCount)
            }

            override fun isLoading() = networkState is NetworkState.LOADING
        })

        stockViewModel.loadingStocksStateLiveData.observe(viewLifecycleOwner, {
            networkState = it
            swipeRefreshLayout.isRefreshing = it is NetworkState.LOADING
        })
    }
}