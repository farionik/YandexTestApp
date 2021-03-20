package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.ui.adapter.PaginationListener
import com.farionik.yandextestapp.ui.adapter.StockAdapter
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import kotlinx.coroutines.launch

class PopularStocksFragment : BaseStockFragment() {

    //private lateinit var stockPagingAdapter: StockPagingAdapter
    private lateinit var stockAdapter: StockAdapter
    private var networkState: NetworkState? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockViewModel.stocksLiveData.observe(viewLifecycleOwner, { stockAdapter.swapData(it) })
        stockViewModel.loadingStocksStateLiveData.observe(viewLifecycleOwner, {
            networkState = it
        })
        //stockViewModel.fetchCompanies(0)
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

            }
        })

        //initRecyclerView(stockAdapter)

        recyclerView.apply {
            val layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            setHasFixedSize(true)
            addItemDecoration(CompanySpaceItemDecoration())
            this.adapter = stockAdapter
            addOnScrollListener(object : PaginationListener(layoutManager) {
                override fun loadMoreItems(page: Int) {
                    stockViewModel.fetchCompanies(page)
                }

                override fun isLastPage() = false
                override fun isLoading() = networkState is NetworkState.LOADING
            })
        }


        /*stockPagingAdapter =
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

        initRecyclerView(stockPagingAdapter)*/

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