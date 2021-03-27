package com.farionik.yandextestapp.repository.database.stock

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.farionik.yandextestapp.repository.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StockDAO : BaseDao<StockEntity> {

    @Query("SELECT * FROM StockTable")
    abstract suspend fun stockList(): List<StockEntity>

    @Query("SELECT * FROM StockTable WHERE symbol =:symbol")
    abstract suspend fun stockEntity(symbol: String): StockEntity?

    @Transaction
    @Query("SELECT * FROM StockTable WHERE symbol =:symbol")
    abstract fun stockModelRelationLiveData(symbol: String): LiveData<StockModelRelation>

    @Transaction
    @Query("SELECT * FROM StockTable WHERE symbol =:symbol")
    abstract fun stockModelRelation(symbol: String): StockModelRelation?

    @Transaction
    @Query("SELECT * FROM StockTable WHERE isUserSearch =:isUserSearch")
    abstract fun stocksRelationFlow(isUserSearch: Boolean): Flow<List<StockModelRelation>>

    @Query("SELECT * FROM StockTable")
    abstract fun stocksFlow(): Flow<List<StockEntity>>

    @Transaction
    @Query("SELECT * FROM StockTable WHERE isFavourite")
    abstract fun favouriteStocksFlow(): Flow<List<StockModelRelation>>

    @Query("DELETE FROM StockTable WHERE isUserSearch is 1 AND isFavourite = 0")
    abstract suspend fun deleteUserSearch()

    @Query("SELECT * FROM StockTable WHERE isUserSearch is 1 AND isFavourite = 1")
    abstract fun searchFavouriteStock(): List<StockEntity>

    suspend fun updateUserSearch() {
        deleteUserSearch()
        searchFavouriteStock().let { list ->
            list.map { it.isUserSearch = false }
            insertAll(list)
        }
    }
}