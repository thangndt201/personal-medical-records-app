package com.example.appquanlybenhancanhan.UI.user

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.MainUserHome
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.Patient
import kotlinx.coroutines.launch
import java.util.Calendar

class MainCapNhatThongTinUser : AppCompatActivity() {

    // View cho thông tin chỉ hiển thị (từ User)
    private lateinit var tvHoTen: TextView
    private lateinit var tvGioiTinh: TextView
    private lateinit var tvSDT: TextView

    // View cho thông tin user nhập
    private lateinit var edtNamSinh: EditText
    private lateinit var edtQueQuan: EditText
    private lateinit var edtCCCD: EditText

    private lateinit var btnLuu: Button

    private var userId: Long = -1L
    private lateinit var patientDao: com.example.appquanlybenhancanhan.data.PatientDao
    private lateinit var userDao: com.example.appquanlybenhancanhan.data.UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cap_nhat_thong_tin_user)

        // Ánh xạ view
        tvHoTen = findViewById(R.id.tvHoTen)
        tvGioiTinh = findViewById(R.id.tvGioiTinh)
        tvSDT = findViewById(R.id.tvSDT)

        edtNamSinh = findViewById(R.id.edtNamSinh)
        edtQueQuan = findViewById(R.id.edtQueQuan)
        edtCCCD = findViewById(R.id.edtCCCD)

        btnLuu = findViewById(R.id.btnLuuThongTin)

        // Lấy userId từ Intent
        userId = intent.getLongExtra("userId", -1L)
        if (userId == -1L) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Khởi tạo DAO
        val db = AppDatabase.getDatabase(this)
        patientDao = db.patientDao()
        userDao = db.userDao()

        // Load thông tin từ User và Patient (nếu đã có)
        loadUserAndPatientData()

        // Sự kiện nút Lưu
        btnLuu.setOnClickListener {
            savePatientInfo()
        }

        // DatePicker cho Năm sinh
        edtNamSinh.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    edtNamSinh.setText(formattedDate)
                },
                year, month, day
            )
            datePicker.show()
        }
    }

    private fun loadUserAndPatientData() {
        lifecycleScope.launch {
            val user = userDao.getUserById(userId)
            if (user != null) {
                tvHoTen.text = user.fullName
                tvGioiTinh.text = user.gender ?: "Chưa xác định"
                tvSDT.text = user.phone ?: "Chưa có"
            }

            // Lấy thông tin Patient nếu đã tồn tại (cho phép sửa lại)
            val patient = patientDao.getPatientByUserId(userId)
            if (patient != null) {
                edtNamSinh.setText(patient.ngaySinh)
                edtQueQuan.setText(patient.queQuan)
                edtCCCD.setText(patient.cccd)
            }
        }
    }

    private fun savePatientInfo() {
        val ngaySinh = edtNamSinh.text.toString().trim()
        val queQuan = edtQueQuan.text.toString().trim()
        val cccd = edtCCCD.text.toString().trim()

        // Validate các trường bắt buộc
        if (ngaySinh.isEmpty() || queQuan.isEmpty() || cccd.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin còn lại!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingPatient = patientDao.getPatientByUserId(userId)

            if (existingPatient == null) {
                // Tạo mới Patient
                val newPatient = Patient(
                    maHoSo = "",
                    hoTen = tvHoTen.text.toString(),
                    gioiTinh = tvGioiTinh.text.toString(),
                    ngaySinh = ngaySinh,
                    queQuan = queQuan,
                    cccd = cccd,
                    sdt = tvSDT.text.toString(),
                    userId = userId
                )
                patientDao.insert(newPatient)
                Toast.makeText(this@MainCapNhatThongTinUser, "Lưu thông tin bệnh nhân thành công!", Toast.LENGTH_LONG).show()
            } else {
                // Cập nhật Patient hiện có
                val updatedPatient = existingPatient.copy(
                    ngaySinh = ngaySinh,
                    queQuan = queQuan,
                    cccd = cccd
                )
                patientDao.update(updatedPatient)
                Toast.makeText(this@MainCapNhatThongTinUser, "Cập nhật thông tin thành công!", Toast.LENGTH_LONG).show()
            }

            // Chuyển về MainUserHome
            val intent = Intent(this@MainCapNhatThongTinUser, MainUserHome::class.java)
            intent.putExtra("userId", userId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}