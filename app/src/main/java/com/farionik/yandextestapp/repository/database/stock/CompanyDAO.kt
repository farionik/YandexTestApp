package com.farionik.yandextestapp.repository.database.stock

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao

@Dao
abstract class CompanyDAO : BaseDao<CompanyEntity> {

    @Query("SELECT * FROM CompanyTable WHERE symbol =:symbol")
    abstract fun companyEntityLiveData(symbol: String): LiveData<CompanyEntity>

}