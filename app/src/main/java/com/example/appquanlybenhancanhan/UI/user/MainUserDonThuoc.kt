package com.example.appquanlybenhancanhan.UI.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.adapter.DonThuocAdapter
import com.example.appquanlybenhancanhan.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainUserDonThuoc : AppCompatActivity() {

    private lateinit var rvDonThuoc: RecyclerView
    private lateinit var tvEmpty: View
    private lateinit var adapter: DonThuocAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_don_thuoc)

        rvDonThuoc = findViewById(R.id.rvDonThuoc)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = DonThuocAdapter()
        rvDonThuoc.adapter = adapter
        rvDonThuoc.layoutManager = LinearLayoutManager(this)

        // Toolbar back
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        loadAllDonThuoc()
    }

    private fun loadAllDonThuoc() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.donThuocDao()

        lifecycleScope.launch {
            val list = dao.getAllDonThuoc().first()

            if (list.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvDonThuoc.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvDonThuoc.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }
    }
}