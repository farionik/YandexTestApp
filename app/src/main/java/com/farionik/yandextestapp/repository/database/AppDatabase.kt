package com.farionik.yandextestapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartDAO
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.company.CompanyDAO
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.news.NewsDAO
import com.farionik.yandextestapp.repository.database.news.NewsEntity

@Database(
    entities = [CompanyEntity::class, ChartEntity::class, NewsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDAO(): CompanyDAO

    abstract fun chartDAO(): ChartDAO

    abstract fun newsDAO(): NewsDAO
}