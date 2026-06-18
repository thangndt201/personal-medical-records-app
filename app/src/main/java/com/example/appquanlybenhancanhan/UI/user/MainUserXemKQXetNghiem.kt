package com.example.appquanlybenhancanhan.UI.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.adapter.KetQuaXetNghiemAdapter
import com.example.appquanlybenhancanhan.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainUserXemKetQuaXetNghiem : AppCompatActivity() {

    private lateinit var rvKetQua: RecyclerView
    private lateinit var tvEmpty: View
    private lateinit var adapter: KetQuaXetNghiemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_xem_kqxet_nghiem)

        rvKetQua = findViewById(R.id.rvKetQua)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = KetQuaXetNghiemAdapter()
        rvKetQua.adapter = adapter
        rvKetQua.layoutManager = LinearLayoutManager(this)

        // Toolbar back
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        loadAllKetQua()
    }

    private fun loadAllKetQua() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.ketQuaXetNghiemDao()

        lifecycleScope.launch {
            val list = dao.getAllKetQua().first()

            if (list.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvKetQua.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvKetQua.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }
    }
}