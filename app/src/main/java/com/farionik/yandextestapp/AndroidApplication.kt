package com.farionik.yandextestapp

import android.app.Application
import com.farionik.yandextestapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AndroidApplication)
            modules(appModule)
        }
    }
}