package com.farionik.yandextestapp.repository.database.company

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CompanyConverter {

    @TypeConverter
    fun fromTags(models: List<String?>?): String? {
        return Gson().toJson(models)
    }

    @TypeConverter
    fun toTags(data: String?): List<String?>? {
        return Gson().fromJson(data, object : TypeToken<List<String?>?>() {}.type)
    }

}