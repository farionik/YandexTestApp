package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.view_model.StockViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseStockFragment : Fragment(R.layout.fragment_stocks) {

    abstract fun initAdapter()

    val stockViewModel by sharedViewModel<StockViewModel>()

    protected lateinit var recyclerView: RecyclerView
    protected lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(stockViewModel)
        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)

        if (this is PopularStocksFragment) {
            swipeRefreshLayout.setOnRefreshListener { stockViewModel.fetchCompanies() }
        } else {
            swipeRefreshLayout.isVisible = false
        }

        initAdapter()
    }

    fun <T : RecyclerView.ViewHolder?> initRecyclerView(adapter: RecyclerView.Adapter<T>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(CompanySpaceItemDecoration())
            this.adapter = adapter
        }
    }
}