package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.repository.database.company.StockModelRelation

class FavouriteStockFragment : BaseStockFragment() {
    override val stockSource: LiveData<List<StockModelRelation>>
        get() = stockViewModel.favouriteStocksLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //swipeRefreshLayout.isVisible = false
    }
}