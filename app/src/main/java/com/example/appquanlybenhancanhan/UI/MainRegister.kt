package com.example.appquanlybenhancanhan.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.Patient
import com.example.appquanlybenhancanhan.data.User
import kotlinx.coroutines.launch

class MainRegister : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var edtFullName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView

    private lateinit var userDao: com.example.appquanlybenhancanhan.data.UserDao
    private lateinit var patientDao: com.example.appquanlybenhancanhan.data.PatientDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_register)

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        edtFullName = findViewById(R.id.edtFullName)
        edtPhone = findViewById(R.id.edtPhone)
        edtEmail = findViewById(R.id.edtEmail)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        // Khởi tạo database và DAO
        val database = AppDatabase.getDatabase(this)
        userDao = database.userDao()
        patientDao = database.patientDao()

        btnRegister.setOnClickListener {
            performRegister()
        }

        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, MainLogin::class.java))
            finish()
        }
    }

    private fun performRegister() {
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString()
        val confirmPassword = edtConfirmPassword.text.toString()
        val fullName = edtFullName.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        // Giới tính
        val gender = if (rbMale.isChecked) "Nam" else "Nữ"

        // Validate
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        if (username.length < 4 || password.length < 6) {
            Toast.makeText(this, "Username ≥ 4 ký tự, mật khẩu ≥ 6 ký tự", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (userDao.getUserByUsername(username) != null) {
                Toast.makeText(this@MainRegister, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val newUser = User(
                username = username,
                password = password,
                fullName = fullName,
                phone = if (phone.isEmpty()) null else phone,
                email = if (email.isEmpty()) null else email,
                gender = gender
            )

            val userId = userDao.registerUser(newUser)

            Toast.makeText(this@MainRegister, "Đăng ký thành công!\nVui lòng cập nhật thông tin bệnh nhân sau khi đăng nhập.", Toast.LENGTH_LONG).show()

            // Chuyển thẳng sang Login
            startActivity(Intent(this@MainRegister, MainLogin::class.java))
            finish()
        }
    }
}