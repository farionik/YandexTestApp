package com.farionik.yandextestapp.ui.main

import android.os.Bundle
import android.view.View
import com.farionik.yandextestapp.data.SnippetEntity
import com.farionik.yandextestapp.ui.BaseFragment

class StockFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = mutableListOf<SnippetEntity>().apply {
            for (i in 0..30) {
                add(SnippetEntity("Ticker $i", "name $i", "$10 000", "+$0.12 (1,15%)"))
            }
        }
        adapter.swapData(data)
    }
}