package com.example.appquanlybenhancanhan.UI

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.DonThuoc
import com.example.appquanlybenhancanhan.data.DonThuocDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainDonThuoc : AppCompatActivity() {
    private lateinit var edtma: EditText
    private lateinit var edtten: EditText
    private lateinit var edtlieu: EditText
    private lateinit var edtghichu: EditText
    private lateinit var btnLuu: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var lvDonThuoc: ListView

    // Database components
    private lateinit var db: AppDatabase
    private lateinit var donThuocDao: DonThuocDao
    private var selectedDonThuoc: DonThuoc? = null

    // ListView Adapter and data sources
    private lateinit var donThuocAdapter: ArrayAdapter<String>
    private val donThuocDisplayList = mutableListOf<String>()
    private var currentDonThuocList = listOf<DonThuoc>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_don_thuoc)
        setControl()

        // Init DB
        db = AppDatabase.getDatabase(applicationContext)
        donThuocDao = db.donThuocDao()

        // Setup Adapter
        donThuocAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, donThuocDisplayList)
        lvDonThuoc.adapter = donThuocAdapter

        // Observe data changes from Room
        lifecycleScope.launch {
            donThuocDao.getAllDonThuoc().collectLatest { donThuocList ->
                currentDonThuocList = donThuocList
                donThuocDisplayList.clear()
                donThuocList.forEach { donThuoc ->
                    donThuocDisplayList.add("Mã: ${donThuoc.maThuoc} - Tên: ${donThuoc.tenThuoc}\nLiều dùng: ${donThuoc.lieuDung}\nGhi chú: ${donThuoc.ghiChu}")
                }
                donThuocAdapter.notifyDataSetChanged()
            }
        }

        setEvent()
        //Toolbar & mũi tên quay lại
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Nút mũi tên back quay về Home ngay, không hỏi
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainHome::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setControl() {
        edtma = findViewById(R.id.edtma)
        edtten = findViewById(R.id.edtten)
        edtlieu = findViewById(R.id.edtlieu)
        edtghichu = findViewById(R.id.edtghichu)
        btnLuu = findViewById(R.id.btnLuu)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        lvDonThuoc = findViewById(R.id.lvDonThuoc)
    }

    private fun setEvent() {
        // === LƯU ===
        btnLuu.setOnClickListener {
            val ma = edtma.text.toString().trim()
            val ten = edtten.text.toString().trim()
            if (ma.isEmpty() || ten.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ Mã và Tên thuốc!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val lieuDung = edtlieu.text.toString().trim()
            val ghiChu = edtghichu.text.toString().trim()
            val donThuoc = DonThuoc(maThuoc = ma, tenThuoc = ten, lieuDung = lieuDung, ghiChu = ghiChu)

            lifecycleScope.launch(Dispatchers.IO) {
                donThuocDao.insert(donThuoc)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainDonThuoc, "Đã lưu đơn thuốc!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }

        // === SỬA ===
        btnSua.setOnClickListener {
            if (selectedDonThuoc == null) {
                Toast.makeText(this, "Vui lòng chọn đơn thuốc để sửa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ma = edtma.text.toString().trim()
            val ten = edtten.text.toString().trim()
            if (ma.isEmpty() || ten.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ Mã và Tên thuốc!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val lieuDung = edtlieu.text.toString().trim()
            val ghiChu = edtghichu.text.toString().trim()

            val updatedDonThuoc = selectedDonThuoc!!.copy(
                maThuoc = ma,
                tenThuoc = ten,
                lieuDung = lieuDung,
                ghiChu = ghiChu
            )

            lifecycleScope.launch(Dispatchers.IO) {
                donThuocDao.update(updatedDonThuoc)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainDonThuoc, "Đã cập nhật đơn thuốc!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }

        // === XÓA ===
        btnXoa.setOnClickListener {
            if (selectedDonThuoc == null) {
                Toast.makeText(this, "Vui lòng chọn đơn thuốc để xóa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa đơn thuốc này không?")
                .setPositiveButton("Có") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        donThuocDao.delete(selectedDonThuoc!!)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainDonThuoc, "Đã xóa đơn thuốc.", Toast.LENGTH_SHORT).show()
                            clearFields()
                        }
                    }
                }
                .setNegativeButton("Không", null)
                .show()
        }

        // === CHỌN ITEM ===
        lvDonThuoc.setOnItemClickListener { _, _, position, _ ->
            if (position < currentDonThuocList.size) {
                selectedDonThuoc = currentDonThuocList[position]
                val donThuoc = selectedDonThuoc!!
                edtma.setText(donThuoc.maThuoc)
                edtten.setText(donThuoc.tenThuoc)
                edtlieu.setText(donThuoc.lieuDung)
                edtghichu.setText(donThuoc.ghiChu)
            }
        }
    }

    private fun clearFields() {
        edtma.setText("")
        edtten.setText("")
        edtlieu.setText("")
        edtghichu.setText("")
        selectedDonThuoc = null
        lvDonThuoc.clearChoices()
    }
}