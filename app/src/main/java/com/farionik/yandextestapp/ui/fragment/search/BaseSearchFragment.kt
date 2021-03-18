package com.farionik.yandextestapp.ui.fragment.search

import androidx.fragment.app.Fragment
import com.farionik.yandextestapp.view_model.SearchViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

open class BaseSearchFragment : Fragment() {

    val searchViewModel by sharedViewModel<SearchViewModel>()
}