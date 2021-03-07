package com.farionik.yandextestapp.repository.database.chart

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChartDAO : BaseDao<ChartEntity> {

    @Query("SELECT * FROM ChartTable WHERE range =:range")
    abstract fun chartLiveData(range: String): LiveData<List<ChartEntity>>

    fun chartLiveData(range: ChartRange?): LiveData<List<ChartEntity>>? = when (range) {
        ChartRange.DAY -> chartLiveData("1d")
        ChartRange.WEEK -> chartLiveData("7d")
        ChartRange.MONTH -> chartLiveData("1m")
        ChartRange.HALF_YEAR -> chartLiveData("6m")
        ChartRange.YEAR -> chartLiveData("1y")
        ChartRange.ALL -> chartLiveData("max")
        else -> null
    }

    @Query("DELETE FROM ChartTable")
    abstract fun cleanTable()

}