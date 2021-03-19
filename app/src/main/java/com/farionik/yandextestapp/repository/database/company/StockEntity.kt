package com.farionik.yandextestapp.repository.database.company

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StockTable")
data class StockEntity(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
    val latestPrice: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,

    var isFavourite: Boolean = false
)