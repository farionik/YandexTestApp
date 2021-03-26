package com.farionik.yandextestapp.repository.database.stock

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.farionik.yandextestapp.ui.fragment.search.ISearchModel

@Entity(tableName = "StockTable")
data class StockEntity(
    @PrimaryKey
    val symbol: String,
    val companyName: String,
    val latestPrice: Double,
    val change: Double,
    val changePercent: Double,
    val volume: Long,

    var isFavourite: Boolean = false,
    var isUserSearch: Boolean = false

) : ISearchModel {
    @Ignore
    override fun id() = symbol
    @Ignore
    override fun title() = companyName
    @Ignore
    override fun content() = toString()
}