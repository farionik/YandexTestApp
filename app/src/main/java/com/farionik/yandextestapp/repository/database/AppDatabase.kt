package com.farionik.yandextestapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.farionik.yandextestapp.repository.database.chart.ChartDAO
import com.farionik.yandextestapp.repository.database.chart.ChartEntity
import com.farionik.yandextestapp.repository.database.company.CompanyDAO
import com.farionik.yandextestapp.repository.database.company.CompanyEntity

@Database(
    entities = [CompanyEntity::class, ChartEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDAO(): CompanyDAO

    abstract fun chartDAO(): ChartDAO
}