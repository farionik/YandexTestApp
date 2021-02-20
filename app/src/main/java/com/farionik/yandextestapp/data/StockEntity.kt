package com.farionik.yandextestapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StockTable")
data class StockEntity(
    @PrimaryKey
    val id: Int,
    val ticker: String,
    val company_name: String,
    val price: String,
    val percent: String
)
