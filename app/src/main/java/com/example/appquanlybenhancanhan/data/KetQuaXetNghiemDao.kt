package com.example.appquanlybenhancanhan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KetQuaXetNghiemDao {
    @Insert
    suspend fun insert(ketQua: KetQuaXetNghiem)
    @Update
    suspend fun update(ketQua: KetQuaXetNghiem)
    @Delete
    suspend fun delete(ketQua: KetQuaXetNghiem)
    @Query("SELECT * FROM ketqua_xetnghiem_table")
    fun getAllKetQua(): Flow<List<KetQuaXetNghiem>>
}