package com.farionik.yandextestapp.repository.database.crypto

import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CryptoDAO : BaseDao<CryptoEntity>{

    @Query("SELECT * FROM CryptoTable")
    abstract fun cryptoFlow(): Flow<List<CryptoEntity>>

    @Query("SELECT * FROM CryptoTable")
    abstract fun cryptoList(): List<CryptoEntity>
}