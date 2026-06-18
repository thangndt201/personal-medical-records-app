package com.example.appquanlybenhancanhan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val username: String,
    val password: String, // Lưu ý: thực tế nên hash password (dùng BCrypt), nhưng đồ án thì có thể lưu plain tạm
    val fullName: String,
    val phone: String?,
    val email: String?,
    val gender: String? = "Nam"
)