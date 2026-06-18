package com.example.appquanlybenhancanhan.UI.user

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.adapter.LichTaiKhamAdapter
import com.example.appquanlybenhancanhan.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainUserXemLichTaiKham : AppCompatActivity() {

    private lateinit var rvLichTaiKham: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: LichTaiKhamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_xem_lich_tai_kham)

        // Ánh xạ view
        rvLichTaiKham = findViewById(R.id.rvLichTaiKham)
        tvEmpty = findViewById(R.id.tvEmpty)

        // Setup RecyclerView
        adapter = LichTaiKhamAdapter()
        rvLichTaiKham.adapter = adapter
        rvLichTaiKham.layoutManager = LinearLayoutManager(this)

        // Toolbar back
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Load dữ liệu
        loadLichTaiKham()
    }

    private fun loadLichTaiKham() {
        val db = AppDatabase.getDatabase(this)
        val lichHenDao = db.lichHenDao()

        lifecycleScope.launch {
            val list = lichHenDao.getAllLichHen().first()

            if (list.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvLichTaiKham.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvLichTaiKham.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }
    }
}