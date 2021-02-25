package com.farionik.yandextestapp

import android.app.Application
import com.farionik.yandextestapp.di.appApiModule
import com.farionik.yandextestapp.di.appDatabaseModule
import com.farionik.yandextestapp.di.appRepositoryModule
import com.farionik.yandextestapp.di.appViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AndroidApplication)
            modules(
                listOf(
                    appApiModule,
                    appDatabaseModule,
                    appRepositoryModule,
                    appViewModelModule
                )
            )
        }
    }
}