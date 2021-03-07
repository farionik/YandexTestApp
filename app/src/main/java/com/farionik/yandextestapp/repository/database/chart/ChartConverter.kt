package com.farionik.yandextestapp.repository.database.chart

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChartConverter {

    @TypeConverter
    fun fromChartValues(models: List<ChartValues?>?): String? {
        return Gson().toJson(models)
    }

    @TypeConverter
    fun toChartValues(data: String?): List<ChartValues?>? {
        return Gson().fromJson(data, object : TypeToken<List<ChartValues?>?>() {}.type)
    }

}