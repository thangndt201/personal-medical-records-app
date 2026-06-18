package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ketqua_xetnghiem_table")
data class KetQuaXetNghiem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val maHoSo: String,
    val ngayXetNghiem: String,
    val loaiXetNghiem: String,
    val chiSo: String,
    val ketQua: String,
    val ghiChu: String
)