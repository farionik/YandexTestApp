package com.farionik.yandextestapp.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.ui.BaseFragment


class SearchResultFragment : BaseFragment() {

    private lateinit var showMore: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showMore = view.findViewById(R.id.showMoreTitle)
    }
}