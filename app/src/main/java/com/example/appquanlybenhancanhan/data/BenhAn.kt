package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benh_an_table")
data class BenhAn(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tenBenh: String,
    val moTa: String,
    val duongDanTep: String? = null
)