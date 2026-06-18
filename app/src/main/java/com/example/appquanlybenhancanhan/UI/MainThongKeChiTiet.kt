package com.example.appquanlybenhancanhan.UI

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainThongKeChiTiet : AppCompatActivity() {

    private lateinit var tvSoBenhNhan: TextView
    private lateinit var tvSoKetQuaXN: TextView
    private lateinit var tvSoBenhAn: TextView
    private lateinit var tvSoDonThuoc: TextView
    private lateinit var tvSoLichHen: TextView
    private lateinit var tvSoNhacNho: TextView
    private lateinit var tvSoTaiKhoan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_thong_ke_chi_tiet)

        // Ánh xạ
        tvSoBenhNhan = findViewById(R.id.tvSoBenhNhan)
        tvSoKetQuaXN = findViewById(R.id.tvSoKetQuaXN)
        tvSoBenhAn = findViewById(R.id.tvSoBenhAn)
        tvSoDonThuoc = findViewById(R.id.tvSoDonThuoc)
        tvSoLichHen = findViewById(R.id.tvSoLichHen)
        tvSoNhacNho = findViewById(R.id.tvSoNhacNho)
        tvSoTaiKhoan = findViewById(R.id.tvSoTaiKhoan)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        loadThongKe()
    }

    private fun loadThongKe() {
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            tvSoBenhNhan.text = db.userDao().getAllUsers().first().size.toString()
            tvSoKetQuaXN.text = db.ketQuaXetNghiemDao().getAllKetQua().first().size.toString()
            tvSoBenhAn.text = db.benhAnDao().getAllBenhAn().first().size.toString()
            tvSoDonThuoc.text = db.donThuocDao()?.getAllDonThuoc()?.first()?.size?.toString() ?: "0"
            tvSoLichHen.text = db.lichHenDao().getAllLichHen().first().size.toString()
            tvSoNhacNho.text = db.nhacNhoDao()?.getAllNhacNho()?.first()?.size?.toString() ?: "0"
            tvSoTaiKhoan.text = db.userDao().getAllUsers().first().size.toString()
        }
    }
}