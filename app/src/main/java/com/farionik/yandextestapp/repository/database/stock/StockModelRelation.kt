package com.farionik.yandextestapp.repository.database.stock

import androidx.room.Embedded
import androidx.room.Relation

class StockModelRelation {

    @Embedded
    lateinit var stock: StockEntity

    @Relation(parentColumn = "symbol", entityColumn = "symbol")
    var logo: CompanyLogoEntity? = null
}