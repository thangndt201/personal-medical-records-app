package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nhac_nho_table")
data class NhacNho(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tenThuoc: String,
    val lieuLuong: String,
    val gio: String,
    val lapLai: Boolean,
    val thongBao: Boolean,
    val daUong: Boolean = false
)