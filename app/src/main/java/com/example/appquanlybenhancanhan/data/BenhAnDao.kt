package com.example.appquanlybenhancanhan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BenhAnDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(benhAn: BenhAn)
    @Update
    suspend fun update(benhAn: BenhAn)
    @Delete
    suspend fun delete(benhAn: BenhAn)
    @Query("SELECT * FROM benh_an_table ORDER BY id DESC")
    fun getAllBenhAn(): Flow<List<BenhAn>>
}