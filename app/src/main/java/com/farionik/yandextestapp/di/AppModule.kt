package com.farionik.yandextestapp.di

import com.farionik.yandextestapp.ui.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {


    viewModel { MainViewModel() }

}