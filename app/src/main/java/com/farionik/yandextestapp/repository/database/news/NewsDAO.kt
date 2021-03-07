package com.farionik.yandextestapp.repository.database.news

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao

@Dao
abstract class NewsDAO : BaseDao<NewsEntity> {
    @Query("SELECT * FROM NewsTable WHERE related LIKE :symbol")
    abstract fun newsLiveData(symbol: String): LiveData<List<NewsEntity>>
}