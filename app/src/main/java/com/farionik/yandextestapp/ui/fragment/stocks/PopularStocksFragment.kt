package com.farionik.yandextestapp.ui.fragment.stocks

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.adapter.Interaction
import com.farionik.yandextestapp.ui.adapter.StockPagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PopularStocksFragment : BaseStockFragment() {

    private lateinit var stockPagingAdapter: StockPagingAdapter

    override fun initAdapter() {
        stockPagingAdapter =
            StockPagingAdapter(interaction = object : Interaction {
                override fun likeCompany(stockModelRelation: StockModelRelation, position: Int) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val symbol = stockModelRelation.stock.symbol
                        try {
                            stockViewModel.likeStock(symbol)
                            stockPagingAdapter.updateItem(position)
                        } catch (e: Exception) {
                        }
                    }
                }

                override fun openCompanyDetail(stockModelRelation: StockModelRelation) {
                    //mainActivityListener?.openDetailScreen(companyEntity.symbol)
                }
            })

        initRecyclerView(stockPagingAdapter)

        /*viewLifecycleOwner.lifecycleScope.launch {
            stockViewModel.stockFlow.collectLatest { stockPagingAdapter.submitData(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            stockPagingAdapter.loadStateFlow.collectLatest { loadStates ->
                handleLoading(loadStates)
                handleError(loadStates)
            }
        }*/
    }

    private fun handleLoading(loadStates: CombinedLoadStates) {
        val refresh = loadStates.refresh
        val append = loadStates.append
        val showProgress = (refresh is LoadState.Loading) or (append is LoadState.Loading)
        swipeRefreshLayout.isRefreshing = showProgress
    }

    private fun handleError(loadStates: CombinedLoadStates) {
        val refresh = loadStates.refresh
        val append = loadStates.append
        if (refresh is LoadState.Error) {
            refresh.error.message?.let { showError(it) }
        }
        if (append is LoadState.Error) {
            append.error.message?.let { showError(it) }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}