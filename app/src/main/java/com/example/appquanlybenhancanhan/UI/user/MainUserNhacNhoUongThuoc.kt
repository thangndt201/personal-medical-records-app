package com.example.appquanlybenhancanhan.UI.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.adapter.NhacNhoAdapter
import com.example.appquanlybenhancanhan.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainUserNhacNhoUongThuoc : AppCompatActivity() {

    private lateinit var rvMedication: RecyclerView
    private lateinit var tvEmpty: View
    private lateinit var adapter: NhacNhoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_nhac_nho_uong_thuoc)

        rvMedication = findViewById(R.id.rvMedication)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = NhacNhoAdapter(this)
        rvMedication.adapter = adapter
        rvMedication.layoutManager = LinearLayoutManager(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        loadNhacNho()
    }

    private fun loadNhacNho() {
        val db = AppDatabase.getDatabase(this)
        val dao = db.nhacNhoDao()

        lifecycleScope.launch {
            val list = dao.getAllNhacNho().first()

            if (list.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvMedication.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvMedication.visibility = View.VISIBLE
                adapter.setData(list)
            }
        }
    }
}