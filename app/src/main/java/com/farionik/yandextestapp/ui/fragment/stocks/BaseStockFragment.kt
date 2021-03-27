package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentStocksBinding
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.ui.adapter.StockAdapter
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.farionik.yandextestapp.view_model.StockViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
abstract class BaseStockFragment : Fragment(R.layout.fragment_stocks) {

    abstract val stockSource: LiveData<List<StockModelRelation>>

    val stockViewModel by sharedViewModel<StockViewModel>()
    val companyViewModel by sharedViewModel<CompanyViewModel>()

    protected lateinit var binding: FragmentStocksBinding
    private lateinit var stockAdapter: StockAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStocksBinding.bind(view)
        viewLifecycleOwner.lifecycle.addObserver(stockViewModel)

        initAdapter()
        stockSource.observe(viewLifecycleOwner, { stockAdapter.swapData(it) })
    }

    private fun initAdapter() {
        stockAdapter = StockAdapter(interaction = object : StockAdapter.Interaction {
            override fun likeCompany(stockModelRelation: StockModelRelation, position: Int) {
                val symbol = stockModelRelation.stock.symbol
                stockViewModel.likeStock(symbol)
            }

            override fun openCompanyDetail(stockModelRelation: StockModelRelation) {
                companyViewModel.openDetail(stockModelRelation)
            }
        })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(CompanySpaceItemDecoration())
            this.adapter = stockAdapter
        }
    }
}