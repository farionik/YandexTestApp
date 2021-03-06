package com.farionik.yandextestapp.repository.database.chart

import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao

@Dao
abstract class ChartDAO : BaseDao<ChartEntity> {

    @Query("DELETE FROM ChartTable")
    abstract fun cleanTable()

}