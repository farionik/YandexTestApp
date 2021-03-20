package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.company.StockModelRelation

interface LogoRepository {
    suspend fun loadCompaniesLogo(stock: List<StockModelRelation>)
}