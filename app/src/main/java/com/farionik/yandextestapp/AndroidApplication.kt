package com.farionik.yandextestapp

import android.app.Application
import com.farionik.yandextestapp.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

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
                    appViewModelModule,
                    navigationModule
                )
            )
        }

        Timber.plant(object : Timber.DebugTree() {

        })
    }
}