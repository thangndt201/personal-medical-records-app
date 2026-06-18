package com.example.appquanlybenhancanhan.UI

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.UI.adapter.UserAdapter
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainQuanLyTaiKhoan : AppCompatActivity() {
    private lateinit var rvUsers: RecyclerView
    private lateinit var tvEmpty: View
    private lateinit var adapter: UserAdapter
    private lateinit var userDao: com.example.appquanlybenhancanhan.data.UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_quan_ly_tai_khoan)

        rvUsers = findViewById(R.id.rvUsers)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = UserAdapter { user ->
            confirmDelete(user)
        }

        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)

        val database = AppDatabase.getDatabase(this) // hoặc getInstance
        userDao = database.userDao()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Nút mũi tên back → quay về Home ngay, không hỏi
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainHome::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val users = userDao.getAllUsers()
                .first()

            if (users.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvUsers.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvUsers.visibility = View.VISIBLE
                // Lọc bỏ admin
                adapter.setUsers(users)
            }
        }
    }

    private fun confirmDelete(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Xóa tài khoản")
            .setMessage("Bạn có chắc muốn xóa tài khoản ${user.username}?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteUser(user: User) {
        lifecycleScope.launch {
            userDao.delete(user) // cần thêm hàm delete vào UserDao
            Toast.makeText(this@MainQuanLyTaiKhoan, "Đã xóa ${user.username}", Toast.LENGTH_SHORT).show()
            loadUsers()
        }
    }
}