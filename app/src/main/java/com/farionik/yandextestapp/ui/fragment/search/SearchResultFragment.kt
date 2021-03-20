package com.farionik.yandextestapp.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.ui.fragment.main.BaseStockFragment
import com.farionik.yandextestapp.view_model.SearchViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel


class SearchResultFragment : BaseStockFragment() {

    private lateinit var showMore: TextView
    val searchViewModel by sharedViewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override val dataSource: LiveData<List<StockModelRelation>>
        get() = searchViewModel.searchedStocksLiveData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMore = view.findViewById(R.id.showMoreTitle)
    }
}