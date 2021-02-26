package com.farionik.yandextestapp.ui.fragment.search

import com.farionik.yandextestapp.ui.model.SearchModel

interface SearchedClickedListener {
    fun searchModelClicked(model: SearchModel)
}