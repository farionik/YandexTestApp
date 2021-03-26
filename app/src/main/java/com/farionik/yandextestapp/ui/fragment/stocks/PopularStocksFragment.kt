package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.ui.adapter.PaginationListener

class PopularStocksFragment : BaseStockFragment() {

    private var workState: WorkInfo.State? = null
    override val stockSource: LiveData<List<StockModelRelation>>
        get() = stockViewModel.stocksLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.setOnRefreshListener { stockViewModel.updateData() }

        binding.recyclerView.addOnScrollListener(object : PaginationListener() {
            override fun loadMoreItems(totalCount: Int) {
                stockViewModel.loadMoreStocks()
            }

            override fun isLoading() = workState == WorkInfo.State.RUNNING
        })

        stockViewModel.downloadStockState.observe(viewLifecycleOwner, {
            it?.let {
                workState = it.state
                binding.swipeRefresh.isRefreshing = it.state == WorkInfo.State.RUNNING
            }
        })
    }
}