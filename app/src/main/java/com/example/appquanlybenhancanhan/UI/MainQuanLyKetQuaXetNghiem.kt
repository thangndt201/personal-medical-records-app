package com.example.appquanlybenhancanhan.UI

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.KetQuaXetNghiem
import com.example.appquanlybenhancanhan.data.KetQuaXetNghiemDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainQuanLyKetQuaXetNghiem : AppCompatActivity() {
    private lateinit var edtMaHoSo: EditText
    private lateinit var edtNgayXetNghiem: EditText
    private lateinit var edtChiSo: EditText
    private lateinit var edtKetQua: EditText
    private lateinit var edtGhiChu: EditText
    private lateinit var spLoaiXetNghiem: Spinner
    private lateinit var btnLuu: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var listXetNghiem: ListView

    private lateinit var db: AppDatabase
    private lateinit var ketQuaDao: KetQuaXetNghiemDao
    private var selectedKetQua: KetQuaXetNghiem? = null

    private lateinit var ketQuaAdapter: ArrayAdapter<String>
    private val ketQuaDisplayList = mutableListOf<String>()
    private var currentKetQuaList = listOf<KetQuaXetNghiem>()
    private lateinit var loaiXN: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_quan_ly_ket_qua_xet_nghiem)
        setControl()

        db = AppDatabase.getDatabase(applicationContext)
        ketQuaDao = db.ketQuaXetNghiemDao()

        ketQuaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ketQuaDisplayList)
        listXetNghiem.adapter = ketQuaAdapter

        lifecycleScope.launch {
            ketQuaDao.getAllKetQua().collectLatest { ketQuaList ->
                currentKetQuaList = ketQuaList
                ketQuaDisplayList.clear()
                ketQuaList.forEach { ketQua ->
                    ketQuaDisplayList.add(
                        "Mã: ${ketQua.maHoSo}\nNgày: ${ketQua.ngayXetNghiem}\n" +
                                "Loại: ${ketQua.loaiXetNghiem}\nChỉ số: ${ketQua.chiSo}\nKQ: ${ketQua.ketQua}"
                    )
                }
                ketQuaAdapter.notifyDataSetChanged()
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
        edtMaHoSo = findViewById(R.id.edtMaHoSo)
        edtNgayXetNghiem = findViewById(R.id.edtNgayXetNghiem)
        edtChiSo = findViewById(R.id.edtChiSo)
        edtKetQua = findViewById(R.id.edtKetQua)
        edtGhiChu = findViewById(R.id.edtGhiChu)
        spLoaiXetNghiem = findViewById(R.id.spLoaiXetNghiem)
        btnLuu = findViewById(R.id.btnLuu)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        listXetNghiem = findViewById(R.id.listXetNghiem)

        loaiXN = arrayOf("Xét nghiệm máu", "Nước tiểu", "Sinh hóa", "X-quang", "Siêu âm")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, loaiXN)
        spLoaiXetNghiem.adapter = spinnerAdapter
    }

    private fun setEvent() {
        edtNgayXetNghiem.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                edtNgayXetNghiem.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnLuu.setOnClickListener {
            if (selectedKetQua != null) {
                Toast.makeText(this, "Kết quả đã tồn tại, vui lòng dùng nút 'Sửa'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveKetQuaData()
        }

        btnSua.setOnClickListener {
            if (selectedKetQua == null) {
                Toast.makeText(this, "Vui lòng chọn một kết quả từ danh sách để sửa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveKetQuaData(isUpdating = true)
        }

        btnXoa.setOnClickListener {
            if (selectedKetQua == null) {
                Toast.makeText(this, "Chọn dòng cần xóa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Xóa kết quả xét nghiệm này?")
                .setPositiveButton("Có") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        ketQuaDao.delete(selectedKetQua!!)
                        withContext(Dispatchers.Main) {
                            clearInput()
                            Toast.makeText(this@MainQuanLyKetQuaXetNghiem, "Đã xóa!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Không", null)
                .show()
        }

        listXetNghiem.setOnItemClickListener { _, _, position, _ ->
            if (position < currentKetQuaList.size) {
                loadKetQuaToForm(currentKetQuaList[position])
            }
        }
    }

    private fun saveKetQuaData(isUpdating: Boolean = false) {
        val ma = edtMaHoSo.text.toString().trim()
        val ngay = edtNgayXetNghiem.text.toString().trim()
        val loai = spLoaiXetNghiem.selectedItem.toString()
        val chiSo = edtChiSo.text.toString().trim()
        val ketQuaValue = edtKetQua.text.toString().trim()

        if (ma.isEmpty() || ngay.isEmpty() || chiSo.isEmpty() || ketQuaValue.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        val ghiChu = edtGhiChu.text.toString().trim()

        val ketQua = if (isUpdating) {
            selectedKetQua!!.copy(
                maHoSo = ma,
                ngayXetNghiem = ngay,
                loaiXetNghiem = loai,
                chiSo = chiSo,
                ketQua = ketQuaValue,
                ghiChu = ghiChu
            )
        } else {
            KetQuaXetNghiem(
                maHoSo = ma,
                ngayXetNghiem = ngay,
                loaiXetNghiem = loai,
                chiSo = chiSo,
                ketQua = ketQuaValue,
                ghiChu = ghiChu
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            if (isUpdating) {
                ketQuaDao.update(ketQua)
            } else {
                ketQuaDao.insert(ketQua)
            }
            withContext(Dispatchers.Main) {
                val message = if (isUpdating) "Cập nhật thành công!" else "Thêm mới thành công!"
                Toast.makeText(this@MainQuanLyKetQuaXetNghiem, message, Toast.LENGTH_SHORT).show()
                clearInput()
            }
        }
    }

    private fun loadKetQuaToForm(ketQua: KetQuaXetNghiem) {
        selectedKetQua = ketQua
        edtMaHoSo.setText(ketQua.maHoSo)
        edtNgayXetNghiem.setText(ketQua.ngayXetNghiem)
        spLoaiXetNghiem.setSelection(loaiXN.indexOf(ketQua.loaiXetNghiem))
        edtChiSo.setText(ketQua.chiSo)
        edtKetQua.setText(ketQua.ketQua)
        edtGhiChu.setText(ketQua.ghiChu)
        edtMaHoSo.isEnabled = false
        Toast.makeText(this, "Đã chọn kết quả để sửa/xóa", Toast.LENGTH_SHORT).show()
    }

    private fun clearInput() {
        edtMaHoSo.text.clear()
        edtNgayXetNghiem.text.clear()
        edtChiSo.text.clear()
        edtKetQua.text.clear()
        edtGhiChu.text.clear()
        spLoaiXetNghiem.setSelection(0)
        selectedKetQua = null
        edtMaHoSo.isEnabled = true
    }
}
