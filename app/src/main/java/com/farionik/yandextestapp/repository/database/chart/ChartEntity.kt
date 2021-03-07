package com.farionik.yandextestapp.repository.database.chart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ChartTable")
data class ChartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var range: String,
    var label: String,
    val price: Float
)