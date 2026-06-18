package com.example.appquanlybenhancanhan.UI

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.adapter.SearchResultAdapter
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.model.SearchResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainTimKiem : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var rvResults: RecyclerView
    private lateinit var tvNoResult: View
    private lateinit var adapter: SearchResultAdapter

    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tim_kiem)

        rvResults = findViewById(R.id.rvSearchResults)
        tvNoResult = findViewById(R.id.tvNoResult)

        adapter = SearchResultAdapter()
        rvResults.adapter = adapter
        rvResults.layoutManager = LinearLayoutManager(this)

        // Toolbar back
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        // SearchView
        searchView = findViewById(R.id.searchView)
        searchView.requestFocus()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it.trim()) }
                return true
            }
        })

        // Nhận quyền admin hoặc user
        isAdmin = intent.getBooleanExtra("isAdmin", false)
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            adapter.setResults(emptyList())
            tvNoResult.visibility = View.VISIBLE
            rvResults.visibility = View.GONE
            return
        }

        val db = AppDatabase.getDatabase(this)
        val results = mutableListOf<SearchResult>()

        lifecycleScope.launch {
            // Tìm trong bệnh án
            val benhAnList = db.benhAnDao().getAllBenhAn().first()
            benhAnList.filter { it.tenBenh.contains(query, ignoreCase = true) || it.moTa.contains(query, ignoreCase = true) }
                .forEach {
                    results.add(SearchResult("Bệnh án: ${it.tenBenh}", it.moTa, "benh_an", it.id.toLong()))
                }

            // Tìm trong lịch hẹn
            val lichHenList = db.lichHenDao().getAllLichHen().first()
            lichHenList.filter { it.benhVien.contains(query, ignoreCase = true) || it.ngay.contains(query) }
                .forEach {
                    results.add(SearchResult("Lịch tái khám - ${it.benhVien}", "${it.ngay} - ${it.gio}", "lich_hen", it.id.toLong()))
                }

            // Tìm trong đơn thuốc
            val donThuocList = db.donThuocDao().getAllDonThuoc().first()
            donThuocList.filter { it.tenThuoc.contains(query, ignoreCase = true) }
                .forEach {
                    results.add(SearchResult("Đơn thuốc: ${it.tenThuoc}", "Liều: ${it.lieuDung}", "don_thuoc", it.id.toLong()))
                }

            // Tìm trong kết quả xét nghiệm
            val kqxnList = db.ketQuaXetNghiemDao().getAllKetQua().first()
            kqxnList.filter { it.loaiXetNghiem.contains(query, ignoreCase = true) || it.chiSo.contains(query, ignoreCase = true) }
                .forEach {
                    results.add(SearchResult("Xét nghiệm: ${it.loaiXetNghiem}", "Kết quả: ${it.ketQua}", "ket_qua_xn", it.id.toLong()))
                }

            // Cập nhật UI
            if (results.isEmpty()) {
                tvNoResult.visibility = View.VISIBLE
                rvResults.visibility = View.GONE
            } else {
                tvNoResult.visibility = View.GONE
                rvResults.visibility = View.VISIBLE
                adapter.setResults(results)
            }
        }
    }
}
