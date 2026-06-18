package com.example.appquanlybenhancanhan.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.UI.user.MainCapNhatThongTinUser
import kotlinx.coroutines.launch

class MainLogin : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_login)

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, MainRegister::class.java))
        }

        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Trường hợp admin cứng
        if (username == "admin" && password == "admin") {
            startActivity(Intent(this, MainHome::class.java))
            finish()
            return
        }

        // 2. Đăng nhập user thường từ database
        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val patientDao = database.patientDao()

        lifecycleScope.launch {
            val user = userDao.login(username, password)

            if (user != null) {
                Toast.makeText(this@MainLogin, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                // Kiểm tra xem user này đã có thông tin Patient chưa
                val patient = patientDao.getPatientByUserId(user.userId)

                val intent = if (patient == null) {
                    // Chưa có thông tin bệnh nhân thì bắt buộc cập nhật trước
                    Intent(this@MainLogin, MainCapNhatThongTinUser::class.java)
                } else {
                    // Đã có → vào thẳng home của user
                    Intent(this@MainLogin, MainUserHome::class.java)
                }

                intent.putExtra("userId", user.userId)
                intent.putExtra("fullName", user.fullName)
                intent.putExtra("username", user.username)

                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@MainLogin, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            }
        }
    }
}