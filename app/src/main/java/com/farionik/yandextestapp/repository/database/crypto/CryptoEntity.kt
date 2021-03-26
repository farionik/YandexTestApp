package com.farionik.yandextestapp.repository.database.crypto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CryptoTable")
data class CryptoEntity(
    @PrimaryKey(autoGenerate = false)
    val symbol: String,
    val name: String,
    val region: String = "",
    val currency: String = "",
    val price: String,
)
