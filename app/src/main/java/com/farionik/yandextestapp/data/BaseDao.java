package com.farionik.yandextestapp.data;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> entities);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIgnore(T entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllIgnore(List<T> entities);

    @Update
    void update(T entity);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateIgnore(T entity);
}
