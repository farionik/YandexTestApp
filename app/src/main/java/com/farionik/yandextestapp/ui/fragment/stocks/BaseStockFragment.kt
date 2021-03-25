package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.farionik.yandextestapp.databinding.FragmentStocksBinding
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.adapter.StockAdapter
import com.farionik.yandextestapp.ui.adapter.list_item_decorator.CompanySpaceItemDecoration
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.farionik.yandextestapp.view_model.StockViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseStockFragment : Fragment() {

    abstract val stockSource: LiveData<List<StockModelRelation>>

    val stockViewModel by sharedViewModel<StockViewModel>()
    val companyViewModel by sharedViewModel<CompanyViewModel>()

    protected lateinit var binding: FragmentStocksBinding
    private lateinit var stockAdapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStocksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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