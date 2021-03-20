package com.farionik.yandextestapp.ui.fragment.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.repository.database.company.StockModelRelation

class CompanyFragment : BaseStockFragment() {
    override val dataSource: LiveData<List<StockModelRelation>>
        get() = stockViewModel.stocksLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}