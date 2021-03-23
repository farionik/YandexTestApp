package com.farionik.yandextestapp.repository.database.company

import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao

@Dao
abstract class StartStockDAO : BaseDao<StartStockEntity> {
    @Query("SELECT * FROM StartStockTable")
    abstract suspend fun stockList(): List<StartStockEntity>
}