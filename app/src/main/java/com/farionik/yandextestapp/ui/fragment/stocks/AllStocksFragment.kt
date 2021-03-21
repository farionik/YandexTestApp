package com.farionik.yandextestapp.ui.fragment.stocks

import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.repository.database.company.StockModelRelation

class AllStocksFragment: BaseStockFragment() {
    override val stockSource: LiveData<List<StockModelRelation>>
        get() = stockViewModel.favouriteStocksLiveData
}