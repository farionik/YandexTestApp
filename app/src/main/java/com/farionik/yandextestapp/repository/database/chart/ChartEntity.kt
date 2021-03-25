package com.farionik.yandextestapp.repository.database.chart

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import com.farionik.yandextestapp.ui.util.getFormattedCurrentDate
import com.farionik.yandextestapp.ui.util.sdf
import java.util.*

@Entity(tableName = "ChartTable")
@TypeConverters(ChartConverter::class)
data class ChartEntity(
    @PrimaryKey
    val id: String,
    var lastUpdate: String = getFormattedCurrentDate(),
    var values: List<ChartValues>? = null
)

fun createChartID(symbol: String, range: ChartRange): String = symbol + ":" + range.name

fun ChartEntity.chartRange(): ChartRange = ChartRange.valueOf(id.split(":")[1])
fun ChartEntity.chartSymbol(): String = id.split(":")[0]


fun ChartEntity.isNeedUpdate(): Boolean {
    val date = sdf.parse(lastUpdate) ?: Date()
    val currentDate = Date()


    val interval = 60 * 60 * 1000
    val diff = (currentDate.time - date.time)
    return diff > interval

//    return currentDate.after(date)

    /*return if (chartRange() == ChartRange.DAY) {
        val interval = 60 * 1000
        val diff = (currentDate.time - date.time)
        diff > interval
    } else {
        currentDate.after(date)
    }*/
}