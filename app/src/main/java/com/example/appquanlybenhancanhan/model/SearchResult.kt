package com.example.appquanlybenhancanhan.model

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appquanlybenhancanhan.R

data class SearchResult(
    val title: String,
    val description: String,
    val type: String,
    val id: Long = 0L
)