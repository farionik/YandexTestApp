package com.farionik.yandextestapp.repository.database.company

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CompanyLogoTable")
data class CompanyLogoEntity(
    @PrimaryKey
    val symbol: String,
    val url: String,
    val localPath: String?
)
