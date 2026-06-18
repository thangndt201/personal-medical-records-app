package com.example.appquanlybenhancanhan.UI

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.user.*
import kotlin.apply
import kotlin.text.clear

class MainUserHome : AppCompatActivity() {

    // Các card chức năng
    private lateinit var cardXemHoSo: CardView
    private lateinit var cardLichHen: CardView
    private lateinit var cardKetQuaXetNghiem: CardView
    private lateinit var cardNhacNho: CardView
    private lateinit var cardXemBenhAn: CardView
    private lateinit var cardDonThuoc: CardView
    private lateinit var cardTimKiem: CardView
    private lateinit var cardDangXuat: CardView
    private lateinit var tvWelcome: TextView

    private var currentUserId: Long = -1L
    private var fullName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_home)

        // Ánh xạ tất cả card
        tvWelcome = findViewById(R.id.tvWelcome)

        cardXemHoSo = findViewById(R.id.cardXemHoSo)
        cardLichHen = findViewById(R.id.cardLichHen)
        cardKetQuaXetNghiem = findViewById(R.id.cardKetQuaXetNghiem)
        cardNhacNho = findViewById(R.id.cardNhacNho)
        cardXemBenhAn = findViewById(R.id.cardXemBenhAn)
        cardDonThuoc = findViewById(R.id.cardDonThuoc)
        cardTimKiem = findViewById(R.id.cardTimKiem)
        cardDangXuat = findViewById(R.id.cardDangXuat)

        // Nhận userId từ Login hoặc UpdateInfo
        currentUserId = intent.getLongExtra("userId", -1L)
        fullName = intent.getStringExtra("fullName") ?: "User"

        tvWelcome.text = "Xin chào, $fullName!"

        if (currentUserId == -1L) {
            redirectToLogin()
            return
        }

        // Gắn sự kiện cho từng card
        setupCardClicks()
    }

    private fun setupCardClicks() {
        // 1. Xem hồ sơ cá nhân
        cardXemHoSo.setOnClickListener {
            startActivity(Intent(this, MainUserThongTin::class.java).apply {
                putExtra("userId", currentUserId)
            })
        }

        // 2. Lịch hẹn tái khám
        cardLichHen.setOnClickListener {
            startActivity(Intent(this, MainUserXemLichTaiKham::class.java))
        }
//
        // 3. Kết quả xét nghiệm
        cardKetQuaXetNghiem.setOnClickListener {
            startActivity(Intent(this, MainUserXemKetQuaXetNghiem::class.java))
        }
//
        // 4. Nhắc nhở uống thuốc
        cardNhacNho.setOnClickListener {
            startActivity(Intent(this, MainUserNhacNhoUongThuoc::class.java))
        }
//
//        // 5. Xem bệnh án
        cardXemBenhAn.setOnClickListener {
            startActivity(Intent(this, MainUserXemBenhAn::class.java))
        }
//
        // 6. Xem đơn thuốc
        cardDonThuoc.setOnClickListener {
            startActivity(Intent(this, MainUserDonThuoc::class.java))
        }
//
        // 7. Tìm kiếm thông tin
        cardTimKiem.setOnClickListener {
            startActivity(Intent(this, MainTimKiem::class.java))
        }

        // 8. Đăng xuất
        cardDangXuat.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Có") { _, _ ->
                    redirectToLogin()
                }
                .setNegativeButton("Không", null)
                .show()
        }
    }
    private fun redirectToLogin() {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, MainLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

