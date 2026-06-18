package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_table")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val maHoSo: String,
    val hoTen: String,
    val gioiTinh: String,
    val ngaySinh: String,
    val queQuan: String,
    val cccd: String,
    val sdt: String,
    val userId: Long? = null
)