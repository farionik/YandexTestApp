package com.farionik.yandextestapp.ui.activity

import androidx.fragment.app.Fragment

interface MainActivityListener {
    fun openScreen(fragment: Fragment, addToBackStack: Boolean = true)

    fun backClicked()
}