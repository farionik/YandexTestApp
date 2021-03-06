package com.farionik.yandextestapp.ui.activity

import androidx.fragment.app.Fragment

interface MainActivityListener {
    fun openDetailScreen(symbol: String)

    fun openScreen(fragment: Fragment, addToBackStack: Boolean = true)

    fun searchAction(request: String)

    fun backClicked()
}