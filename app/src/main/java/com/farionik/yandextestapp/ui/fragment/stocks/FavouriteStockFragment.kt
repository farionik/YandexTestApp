package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.adapter.Interaction
import com.farionik.yandextestapp.ui.adapter.StockAdapter
import kotlinx.coroutines.launch

class FavouriteStockFragment : BaseStockFragment() {

    private lateinit var stockAdapter: StockAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockViewModel.favouriteStocksLiveData.observe(
            viewLifecycleOwner,
            { stockAdapter.swapData(it) }
        )
    }

    override fun initAdapter() {
        stockAdapter = StockAdapter(interaction = object : Interaction {
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
        initRecyclerView(stockAdapter)
    }
}