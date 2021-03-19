package com.farionik.yandextestapp.ui

import com.farionik.yandextestapp.ui.fragment.main.MainFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object AppScreens {
    fun mainScreen() = FragmentScreen() { MainFragment() }
}