package com.farionik.yandextestapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyDao
import com.farionik.yandextestapp.repository.database.company.CompanyEntity

@Database(
    entities = arrayOf(CompanyEntity::class),
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDAO(): CompanyDao
}