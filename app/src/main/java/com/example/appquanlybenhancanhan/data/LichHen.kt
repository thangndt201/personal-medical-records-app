package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lich_hen_table")
data class LichHen(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val benhVien: String,
    val ngay: String,
    val gio: String,
    val trangThai: String = "Chưa khám"
)