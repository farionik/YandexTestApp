package com.farionik.yandextestapp.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.ui.adapter.StockAdapter
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.ui.fragment.stocks.BaseStockFragment
import com.farionik.yandextestapp.view_model.SearchViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SearchResultFragment : BaseSearchFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(searchViewModel)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val stockAdapter = StockAdapter(true, object : StockAdapter.Interaction {
            override fun likeCompany(stockModelRelation: StockModelRelation, position: Int) {
                val symbol = stockModelRelation.stock.symbol
                searchViewModel.likeStock(symbol)
            }

            override fun openCompanyDetail(stockModelRelation: StockModelRelation) {
                companyViewModel.openDetail(stockModelRelation)
            }
        })
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(CompanySpaceItemDecoration())
            this.adapter = stockAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            searchViewModel.searchResult.collect {
                stockAdapter.swapData(it)
            }
        }

        searchViewModel.loadingStocksStateLiveData.observe(viewLifecycleOwner, {
            recyclerView.isVisible = it !is NetworkState.LOADING
            progressBar.isVisible = it is NetworkState.LOADING
        })
    }
}