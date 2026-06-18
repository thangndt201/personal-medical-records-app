package com.example.appquanlybenhancanhan.UI.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.MainUserHome
import com.example.appquanlybenhancanhan.data.AppDatabase
import kotlinx.coroutines.launch

class MainUserThongTin : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvFullName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvEmail: TextView

    private lateinit var tvMaHoSo: TextView
    private lateinit var tvHoTenPatient: TextView
    private lateinit var tvGioiTinh: TextView
    private lateinit var tvNgaySinh: TextView
    private lateinit var tvQueQuan: TextView
    private lateinit var tvCccd: TextView
    private lateinit var tvSdtPatient: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user_thong_tin)

        // Ánh xạ view
        tvUsername = findViewById(R.id.tvUsername)
        tvFullName = findViewById(R.id.tvFullName)
        tvPhone = findViewById(R.id.tvPhone)
        tvEmail = findViewById(R.id.tvEmail)

        tvMaHoSo = findViewById(R.id.tvMaHoSo)
        tvHoTenPatient = findViewById(R.id.tvHoTenPatient)
        tvGioiTinh = findViewById(R.id.tvGioiTinh)
        tvNgaySinh = findViewById(R.id.tvNgaySinh)
        tvQueQuan = findViewById(R.id.tvQueQuan)
        tvCccd = findViewById(R.id.tvCccd)
        tvSdtPatient = findViewById(R.id.tvSdtPatient)

        // Lấy userId từ Intent (do MainLogin hoặc MainUserHome truyền sang)
        val userId = intent.getLongExtra("userId", -1L)
        if (userId == -1L) {
            finish()
            return
        }

        loadProfile(userId)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Nút mũi tên back → quay về Home ngay, không hỏi
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainUserHome::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfile(userId: Long) {
        val database = AppDatabase.getDatabase(this)  // hoặc getInstance(this)
        val userDao = database.userDao()
        val patientDao = database.patientDao()  // bạn cần có PatientDao

        lifecycleScope.launch {
            // Lấy thông tin User
            val user = userDao.getUserById(userId)
            if (user != null) {
                tvUsername.text = "Tên đăng nhập: ${user.username}"
                tvFullName.text = "Họ và tên: ${user.fullName}"
                tvPhone.text = "Số điện thoại: ${user.phone ?: "-"}"
                tvEmail.text = "Email: ${user.email ?: "-"}"
            }

            // Lấy thông tin Patient liên kết với userId này
            val patient = patientDao.getPatientByUserId(userId)

            if (patient != null) {
                tvMaHoSo.text = "Mã hồ sơ: ${patient.maHoSo}"
                tvHoTenPatient.text = "Họ tên: ${patient.hoTen}"
                tvGioiTinh.text = "Giới tính: ${patient.gioiTinh}"
                tvNgaySinh.text = "Ngày sinh: ${patient.ngaySinh}"
                tvQueQuan.text = "Quê quán: ${patient.queQuan}"
                tvCccd.text = "CCCD: ${patient.cccd}"
                tvSdtPatient.text = "SĐT: ${patient.sdt}"
            } else {
                // Nếu chưa có Patient (lỗi hoặc chưa tạo), hiển thị thông báo
                tvMaHoSo.text = "Mã hồ sơ: Chưa có"
                tvHoTenPatient.text = "Họ tên: Chưa có"
                tvGioiTinh.text = "Giới tính: Chưa có"
                tvNgaySinh.text = "Ngày sinh: Chưa có"
                tvQueQuan.text = "Quê quán: Chưa có"
                tvCccd.text = "CCCD: Chưa có"
                tvSdtPatient.text = "SĐT: Chưa có"
            }
        }
    }
}