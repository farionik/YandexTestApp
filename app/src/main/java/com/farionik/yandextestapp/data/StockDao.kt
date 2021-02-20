package com.farionik.yandextestapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class StockDao : BaseDao<StockEntity> {
    @Query("SELECT * FROM StockTable")
    abstract fun stockLiveData(): LiveData<List<StockEntity>>
}