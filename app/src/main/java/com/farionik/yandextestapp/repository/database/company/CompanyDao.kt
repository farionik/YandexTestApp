package com.farionik.yandextestapp.repository.database.company

import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CompanyDao : BaseDao<CompanyEntity> {

    @Query("SELECT * FROM CompanyTable WHERE symbol =:symbol")
    abstract suspend fun companyEntity(symbol: String): CompanyEntity?

    @Query("SELECT * FROM CompanyTable")
    abstract fun companyFlow(): Flow<List<CompanyEntity>>

    @Query("SELECT * FROM CompanyTable WHERE isFavourite IS 1")
    abstract fun favouriteCompanyLiveData(): Flow<List<CompanyEntity>>
}