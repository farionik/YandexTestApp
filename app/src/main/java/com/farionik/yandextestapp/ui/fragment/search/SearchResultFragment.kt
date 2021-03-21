package com.farionik.yandextestapp.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.fragment.stocks.BaseStockFragment
import com.farionik.yandextestapp.view_model.SearchViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SearchResultFragment : BaseSearchFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //showMore = view.findViewById(R.id.showMoreTitle)
    }
}