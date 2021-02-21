package com.farionik.yandextestapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = arrayOf(CompanyEntity::class),
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDAO(): CompanyDao
}