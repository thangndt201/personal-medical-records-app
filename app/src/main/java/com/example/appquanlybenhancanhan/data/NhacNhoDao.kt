package com.example.appquanlybenhancanhan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NhacNhoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nhacNho: NhacNho)

    @Update
    suspend fun update(nhacNho: NhacNho)

    @Delete
    suspend fun delete(nhacNho: NhacNho)

    @Query("SELECT * FROM nhac_nho_table ORDER BY id DESC")
    fun getAllNhacNho(): Flow<List<NhacNho>>
}