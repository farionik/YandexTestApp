package com.farionik.yandextestapp.di

import androidx.room.Room
import com.farionik.yandextestapp.repository.*
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.network.NetworkProvider
import com.farionik.yandextestapp.repository.network.WebServicesProvider
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.farionik.yandextestapp.view_model.StockViewModel
import com.farionik.yandextestapp.view_model.SearchViewModel
import com.github.terrakok.cicerone.Cicerone
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appApiModule = module {
    single { NetworkProvider().createApi() }
    single { WebServicesProvider(get()) }
}

val appDatabaseModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "YandexDemoAppDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }
}

val appRepositoryModule = module {
    single<StockRepository> { StockRepositoryImpl(get(), get(), get()) }
    single<CompanyRepository> { CompanyRepositoryImpl(get(), get(), get()) }

    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }
    single<LogoRepository> { LogoRepositoryImpl(get(), get(), get()) }
}

val appViewModelModule = module {
    viewModel {
        StockViewModel(get(), get(), get())
    }

    viewModel {
        CompanyViewModel(get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }
}

val navigationModule = module {
    single { Cicerone.create().router }
    single { Cicerone.create().getNavigatorHolder() }
}
