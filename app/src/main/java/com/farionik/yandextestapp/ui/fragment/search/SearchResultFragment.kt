package com.farionik.yandextestapp.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.ui.adapter.StockAdapter
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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

        searchViewModel.searchLoadingState.observe(viewLifecycleOwner, {
            it?.let {
                recyclerView.isVisible = it.state != WorkInfo.State.RUNNING
                progressBar.isVisible = it.state == WorkInfo.State.RUNNING
            }
        })
    }
}