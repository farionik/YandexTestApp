package com.farionik.yandextestapp.ui.fragment.main

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.activity.MainActivityListener
import com.farionik.yandextestapp.ui.fragment.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.view_model.StockViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.lang.Error

abstract class BaseStockFragment : Fragment(R.layout.fragment_stocks) {

    abstract val dataSource: LiveData<List<StockModelRelation>>

    val stockViewModel by sharedViewModel<StockViewModel>()

    lateinit var stockAdapter: StockAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var mainActivityListener: MainActivityListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(stockViewModel)

        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            if (this is CompanyFragment) {
                stockViewModel.fetchCompanies()
            }
        }

        createAdapter()
    }

    private fun createAdapter() {
        stockAdapter = StockAdapter(interaction = object : StockAdapter.Interaction {
            override fun likeCompany(stockModelRelation: StockModelRelation, position: Int) {
                stockViewModel.likeStock(stockModelRelation.stock.symbol)
                stockAdapter.notifyItemChanged(position, stockModelRelation)
            }

            override fun openCompanyDetail(stockModelRelation: StockModelRelation) {
                //mainActivityListener?.openDetailScreen(companyEntity.symbol)
            }
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(CompanySpaceItemDecoration())
            adapter = stockAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            stockViewModel.stockFlow.collectLatest { stockAdapter.submitData(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            stockAdapter.loadStateFlow.collectLatest { loadStates ->
                handleLoading(loadStates)
                handleError(loadStates)
            }
        }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        }
    }
}