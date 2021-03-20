package com.farionik.yandextestapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartDAO
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.company.*
import com.farionik.yandextestapp.repository.database.news.NewsDAO
import com.farionik.yandextestapp.repository.database.news.NewsEntity

@Database(
    entities = [
        StockEntity::class,
        CompanyEntity::class,
        CompanyLogoEntity::class,
        ChartEntity::class,
        NewsEntity::class,
        StartStockEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stockDAO(): StockDAO

    abstract fun companyDAO(): CompanyDAO

    abstract fun companyLogoDAO(): CompanyLogoDAO

    abstract fun chartDAO(): ChartDAO

    abstract fun newsDAO(): NewsDAO

    abstract fun startStockDAO(): StartStockDAO
}