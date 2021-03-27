package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class FavouriteStockFragment : BaseStockFragment() {
    override val stockSource: LiveData<List<StockModelRelation>>
        get() = stockViewModel.favouriteStocksLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.isEnabled = false
    }
}