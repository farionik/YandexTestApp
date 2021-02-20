package com.farionik.yandextestapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StockTable")
data class StockEntity(
    @PrimaryKey
    val Symbol: String,
    val Sector: String,
    val Name: String,
    val price: String?,
    val percent: String?
)
