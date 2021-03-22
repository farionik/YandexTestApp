package com.farionik.yandextestapp.repository.database.search

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.farionik.yandextestapp.ui.fragment.search.ISearchModel

@Entity(tableName = "UserSearchTable")
data class UserSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String
) : ISearchModel {
    @Ignore
    override fun id() = id.toString()
    @Ignore
    override fun title() = title
    @Ignore
    override fun content() = toString()
}