package com.example.appquanlybenhancanhan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LuuBenhAnTK {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(benhAnLuu: LuuBenhAnTimKiem)
    @Update
    suspend fun update(benhAnLuu: LuuBenhAnTimKiem)
    @Delete
    suspend fun delete(benhAnLuu: LuuBenhAnTimKiem)
    @Query("SELECT * FROM benh_an_luu_table")
    fun getAllBenhAnLuu(): Flow<List<LuuBenhAnTimKiem>>
}