package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "don_thuoc_table")
data class DonThuoc(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val maThuoc: String,
    val tenThuoc: String,
    val lieuDung: String,
    val ghiChu: String
)