package com.farionik.yandextestapp.ui

import com.farionik.yandextestapp.ui.fragment.SplashFragment
import com.farionik.yandextestapp.ui.fragment.stocks.StockFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object AppScreens {
    fun startScreen() = FragmentScreen() { SplashFragment() }

    fun mainScreen() = FragmentScreen() { StockFragment() }
}