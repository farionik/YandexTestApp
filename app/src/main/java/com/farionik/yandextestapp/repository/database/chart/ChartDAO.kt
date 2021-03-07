package com.farionik.yandextestapp.repository.database.chart

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao

@Dao
abstract class ChartDAO : BaseDao<ChartEntity> {

    @Query("SELECT * FROM ChartTable WHERE id=:chartID")
    abstract fun chartLiveData(chartID: String): LiveData<ChartEntity?>

    @Query("SELECT * FROM ChartTable WHERE id=:chartID")
    abstract suspend fun chartEntity(chartID: String): ChartEntity?

    @Query("DELETE FROM ChartTable")
    abstract fun cleanTable()

}