package com.farionik.yandextestapp.di

import androidx.room.Room
import com.farionik.yandextestapp.data.AppDatabase
import com.farionik.yandextestapp.network.NetworkProvider
import com.farionik.yandextestapp.network.WebServicesProvider
import com.farionik.yandextestapp.ui.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { NetworkProvider().createApi() }
    single { WebServicesProvider() }
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "YandexDemoAppDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }


    viewModel {
        MainViewModel(get(), get(), get())
    }
}
