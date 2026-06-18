package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benh_an_luu_table")
data class LuuBenhAnTimKiem(
    @PrimaryKey
    val maBenhAn: String,
    val tenBenhNhan: String,
    val chuanDoan: String
)