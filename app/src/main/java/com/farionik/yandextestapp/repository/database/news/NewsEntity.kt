package com.farionik.yandextestapp.repository.database.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NewsTable")
data class NewsEntity(
    @PrimaryKey
    var id: String,
    val datetime: Long,
    val headline: String,
    val source: String,
    val url: String,
    val summary: String,
    val related: String,
    val image: String,
    val lang: String
)
