package com.farionik.yandextestapp.ui

import com.farionik.yandextestapp.ui.fragment.MainFragment
import com.farionik.yandextestapp.ui.fragment.SplashFragment
import com.farionik.yandextestapp.ui.fragment.detail.CompanyDetailFragment
import com.farionik.yandextestapp.ui.fragment.search.SearchFragment
import com.farionik.yandextestapp.ui.fragment.search.SearchResultFragment
import com.farionik.yandextestapp.ui.fragment.stocks.StockFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object AppScreens {
    fun startScreen() = FragmentScreen() { SplashFragment() }

    fun mainScreen() = FragmentScreen() { MainFragment() }

    fun stockScreen() = FragmentScreen() { StockFragment() }

    fun searchScreen() = FragmentScreen() { SearchFragment() }

    fun searchResultScreen() = FragmentScreen() { SearchResultFragment() }

    fun companyDetailScreen() = FragmentScreen() { CompanyDetailFragment() }
}