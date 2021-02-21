package com.farionik.yandextestapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class CompanyDao : BaseDao<CompanyEntity> {

    @Query("SELECT * FROM CompanyTable WHERE symbol =:symbol")
    abstract suspend fun companyEntity(symbol: String): CompanyEntity?

    @Query("SELECT * FROM CompanyTable")
    abstract fun companyLiveData(): LiveData<List<CompanyEntity>>
}