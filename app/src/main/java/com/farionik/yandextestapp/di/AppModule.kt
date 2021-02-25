package com.farionik.yandextestapp.di

import androidx.room.Room
import com.farionik.yandextestapp.data.AppDatabase
import com.farionik.yandextestapp.network.NetworkProvider
import com.farionik.yandextestapp.network.WebServicesProvider
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.CompanyRepositoryImpl
import com.farionik.yandextestapp.ui.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appApiModule = module {
    single { NetworkProvider().createApi() }
    //single { WebServicesProvider() }
}

val appDatabaseModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "YandexDemoAppDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }
}

val appRepositoryModule = module {
    single<CompanyRepository> { CompanyRepositoryImpl(get(), get(), get()) }
}

val appViewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
}
