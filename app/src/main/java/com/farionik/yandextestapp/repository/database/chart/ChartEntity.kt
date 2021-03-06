package com.farionik.yandextestapp.repository.database.chart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChartTable")
data class ChartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var range: String,
    val average: Float
)