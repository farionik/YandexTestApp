package com.farionik.yandextestapp.di

import androidx.room.Room
import com.farionik.yandextestapp.repository.*
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.network.NetworkProvider
import com.farionik.yandextestapp.repository.network.WebServicesProvider
import com.farionik.yandextestapp.view_model.CompanyViewModel
import com.farionik.yandextestapp.view_model.SearchViewModel
import com.farionik.yandextestapp.view_model.StockViewModel
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Cicerone.Companion.create
import com.github.terrakok.cicerone.Router
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

    single<SearchRepository> { SearchRepositoryImpl(get(), get(), get()) }
}

val appViewModelModule = module {
    viewModel {
        StockViewModel(get(), get(), get(), get())
    }

    viewModel {
        CompanyViewModel(get(), get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }
}

private val cicerone: Cicerone<Router> = create()
val navigationModule = module {
    single { cicerone.router }
    single { cicerone.getNavigatorHolder() }
    single { LocalCiceroneHolder() }
}
