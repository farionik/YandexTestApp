package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.stock.StockEntity

interface LogoRepository {
    suspend fun loadCompaniesLogo(stock: List<StockEntity>)

    suspend fun loadCompanyLogo(stockEntity: StockEntity)
}