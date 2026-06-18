package com.example.appquanlybenhancanhan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LichHenDao {
    @Insert
    suspend fun insert(lichHen: LichHen)
    @Update
    suspend fun update(lichHen: LichHen)
    @Delete
    suspend fun delete(lichHen: LichHen)
    @Query("SELECT * FROM lich_hen_table")
    fun getAllLichHen(): Flow<List<LichHen>>
}