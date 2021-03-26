package com.farionik.yandextestapp.repository.database.stock

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StartStockTable")
data class StartStockEntity(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
)
