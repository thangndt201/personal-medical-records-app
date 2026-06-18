package com.example.appquanlybenhancanhan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DonThuocDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donThuoc: DonThuoc)
    @Update
    suspend fun update(donThuoc: DonThuoc)
    @Delete
    suspend fun delete(donThuoc: DonThuoc)
    @Query("SELECT * FROM don_thuoc_table ORDER BY id DESC")
    fun getAllDonThuoc(): Flow<List<DonThuoc>>
}