package com.farionik.yandextestapp.ui.fragment.stocks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.databinding.FragmentStocksBinding
import com.farionik.yandextestapp.repository.database.stock.StockModelRelation
import com.farionik.yandextestapp.view_model.CryptoViewModel
import com.farionik.yandextestapp.view_model.StockViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class CryptoFragment: Fragment(R.layout.fragment_stocks) {

    private lateinit var binding: FragmentStocksBinding
    private val cryptoViewModel by sharedViewModel<CryptoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStocksBinding.bind(view)
        viewLifecycleOwner.lifecycle.addObserver(cryptoViewModel)
    }
}