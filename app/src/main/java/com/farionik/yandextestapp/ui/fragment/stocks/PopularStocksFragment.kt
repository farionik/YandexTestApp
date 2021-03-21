package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.ui.adapter.PaginationListener
import com.farionik.yandextestapp.ui.adapter.StockAdapter
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import kotlinx.coroutines.launch

class PopularStocksFragment : BaseStockFragment() {

    private lateinit var stockAdapter: StockAdapter
    private var networkState: NetworkState? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockViewModel.stocksLiveData.observe(viewLifecycleOwner, { stockAdapter.swapData(it) })
        stockViewModel.loadingStocksStateLiveData.observe(viewLifecycleOwner, {
            networkState = it
            swipeRefreshLayout.isRefreshing = it is NetworkState.LOADING
        })

        swipeRefreshLayout.setOnRefreshListener { stockViewModel.updateData() }
    }

    override fun initAdapter() {
        stockAdapter = StockAdapter(interaction = object : StockAdapter.Interaction {
            override fun likeCompany(stockModelRelation: StockModelRelation, position: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val symbol = stockModelRelation.stock.symbol
                    try {
                        stockViewModel.likeStock(symbol)
                        stockAdapter.notifyItemChanged(position)
                    } catch (e: Exception) {

                    }
                }
            }

            override fun openCompanyDetail(stockModelRelation: StockModelRelation) {
                companyViewModel.openDetail(stockModelRelation)
            }
        })

        recyclerView.apply {
            val layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            setHasFixedSize(true)
            addItemDecoration(CompanySpaceItemDecoration())
            this.adapter = stockAdapter
            addOnScrollListener(object : PaginationListener(layoutManager) {
                override fun loadMoreItems(totalCount: Int) {
                    stockViewModel.fetchCompanies(totalCount)
                }

                override fun isLoading() = networkState is NetworkState.LOADING
            })
        }
    }

    /*private fun handleLoading(loadStates: CombinedLoadStates) {
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
    }*/
}