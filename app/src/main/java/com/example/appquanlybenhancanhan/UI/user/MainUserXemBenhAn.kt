package com.example.appquanlybenhancanhan.UI.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.adapter.BenhAnAdapter
import com.example.appquanlybenhancanhan.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainUserXemBenhAn : AppCompatActivity() {
    private lateinit var rvBenhAn: RecyclerView
    private lateinit var tvEmpty: View
    private lateinit var adapter: BenhAnAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_xem_benh_an)

        rvBenhAn = findViewById(R.id.rvBenhAn)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = BenhAnAdapter()
        rvBenhAn.adapter = adapter
        rvBenhAn.layoutManager = LinearLayoutManager(this)

        // Toolbar back
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        loadAllBenhAn()
    }

    private fun loadAllBenhAn() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.benhAnDao()  // Đảm bảo bạn có BenhAnDao

        lifecycleScope.launch {
            val list = dao.getAllBenhAn().first()

            if (list.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvBenhAn.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvBenhAn.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }
    }
}