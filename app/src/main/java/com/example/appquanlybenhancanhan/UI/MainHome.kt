package com.example.appquanlybenhancanhan.UI

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainHome : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var cardQuanLyBenhAn: CardView
    private lateinit var cardQuanLyLichHen: CardView
    private lateinit var cardNhacNho: CardView
    private lateinit var cardQuanLyTaiKhoan: CardView
    private lateinit var tvSoBenhNhan: TextView
    private lateinit var tvLichHenHomNay: TextView
    private lateinit var tvDonThuocMoi: TextView
    private lateinit var cardThongKe: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_home)

        // Ánh xạ view
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        cardQuanLyBenhAn = findViewById(R.id.cardQuanLyBenhAn)
        cardQuanLyLichHen = findViewById(R.id.cardQuanLyLichHen)
        cardNhacNho = findViewById(R.id.cardNhacNho)
        cardQuanLyTaiKhoan = findViewById(R.id.cardQuanLyTaiKhoan)

        // Thống kê nhanh
        tvSoBenhNhan = findViewById(R.id.tvSoBenhNhan)
        tvLichHenHomNay = findViewById(R.id.tvLichHenHomNay)
        tvDonThuocMoi = findViewById(R.id.tvDonThuocMoi)
        cardThongKe = findViewById(R.id.cardThongKe)

        // Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Load số liệu thống kê thực
        loadThongKeNhanh()
        // Click các card chức năng
        setupMainCardsClick()

        // Click card thống kê để mở chi tiết
        cardThongKe.setOnClickListener {
            startActivity(Intent(this, MainThongKeChiTiet::class.java))
        }

        // Navigation Drawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.nav_thong_tin_bn -> startActivity(
                    Intent(
                        this,
                        MainQuanLyThongTinBenhNhan::class.java
                    )
                )

                R.id.nav_tim_kiem -> startActivity(Intent(this, MainTimKiem::class.java))
                R.id.nav_kq_xet_nghiem -> startActivity(
                    Intent(
                        this,
                        MainQuanLyKetQuaXetNghiem::class.java
                    )
                )

                R.id.nav_nhap_benh_an -> startActivity(Intent(this, MainNhapBenhAn::class.java))
                R.id.nav_don_thuoc -> startActivity(Intent(this, MainDonThuoc::class.java))
                R.id.nav_lich_hen -> startActivity(Intent(this, MainDatLichHenTaiKham::class.java))
                R.id.nav_nhac_nho -> startActivity(Intent(this, MainNhacNho::class.java))
                R.id.nav_xuat_in -> startActivity(
                    Intent(
                        this,
                        MainXuatIn::class.java
                    )
                )

                R.id.nav_quan_ly_tai_khoan -> startActivity(
                    Intent(
                        this,
                        MainQuanLyTaiKhoan::class.java
                    )
                )
                R.id.nav_thong_ke -> startActivity(
                    Intent(
                        this,
                        MainThongKeChiTiet::class.java
                    )
                )

                R.id.nav_logout -> showLogoutDialog()
            }
            true
        }

        // Xử lý nút back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupMainCardsClick() {
        cardQuanLyBenhAn.setOnClickListener {
            startActivity(Intent(this, MainNhapBenhAn::class.java))
        }

        cardQuanLyLichHen.setOnClickListener {
            startActivity(Intent(this, MainDatLichHenTaiKham::class.java))
        }

        cardNhacNho.setOnClickListener {
            startActivity(Intent(this, MainNhacNho::class.java))
        }

        cardQuanLyTaiKhoan.setOnClickListener {
            startActivity(Intent(this, MainQuanLyTaiKhoan::class.java))
        }
    }

    private fun loadThongKeNhanh() {
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            try {
                val soBenhNhan = db.userDao().getAllUsers().first().size
                tvSoBenhNhan.text = soBenhNhan.toString()

                val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val lichHenList = db.lichHenDao().getAllLichHen().first()
                val soLichHenHomNay = lichHenList.count { it.ngay == today }
                tvLichHenHomNay.text = soLichHenHomNay.toString()

                val soDonThuoc = db.donThuocDao().getAllDonThuoc().first().size
                tvDonThuocMoi.text = soDonThuoc.toString()

            } catch (e: Exception) {
                tvSoBenhNhan.text = "0"
                tvLichHenHomNay.text = "0"
                tvDonThuocMoi.text = "0"
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất không?")
            .setPositiveButton("Có") { _, _ ->
                startActivity(Intent(this, MainLogin::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
            .setNegativeButton("Không", null)
            .show()
    }
}