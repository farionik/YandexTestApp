package com.farionik.yandextestapp.repository.database.company

import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao

@Dao
abstract class CompanyLogoDAO : BaseDao<CompanyLogoEntity> {
    @Query("SELECT * FROM CompanyLogoTable WHERE symbol =:symbol")
    abstract suspend fun companyLogo(symbol: String): CompanyLogoEntity?
}