package com.farionik.yandextestapp.di

import androidx.room.Room
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.network.NetworkProvider
import com.farionik.yandextestapp.repository.CompanyRepository
import com.farionik.yandextestapp.repository.CompanyRepositoryImpl
import com.farionik.yandextestapp.repository.NewsRepositoryImpl
import com.farionik.yandextestapp.repository.NewsRepository
import com.farionik.yandextestapp.view_model.CompanyDetailViewModel
import com.farionik.yandextestapp.view_model.CompanyViewModel
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
    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }
}

val appViewModelModule = module {
    viewModel {
        CompanyViewModel(get(), get())
    }

    viewModel {
        CompanyDetailViewModel(get(), get(), get())
    }
}
