package com.farionik.yandextestapp.repository.database.search

import androidx.room.Dao
import androidx.room.Query
import com.farionik.yandextestapp.repository.database.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserSearchDAO : BaseDao<UserSearchEntity> {
    @Query("SELECT * FROM UserSearchTable")
    abstract fun userSearchFlow(): Flow<List<UserSearchEntity>>

    @Query("SELECT * FROM UserSearchTable WHERE title =:searchRequest")
    abstract fun checkUserSearch(searchRequest:String): UserSearchEntity?
}