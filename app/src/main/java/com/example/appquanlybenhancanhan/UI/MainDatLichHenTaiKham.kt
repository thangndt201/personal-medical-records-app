package com.example.appquanlybenhancanhan.UI

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.LichHen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainDatLichHenTaiKham : AppCompatActivity() {

    private lateinit var spBenhVien: Spinner
    private lateinit var edtNgayKham: EditText
    private lateinit var edtGioKham: EditText
    private lateinit var spTrangThai: Spinner
    private lateinit var btnThem: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var btnIn: Button
    private lateinit var lvDanhSach: ListView

    private lateinit var db: AppDatabase
    private lateinit var lichHenDao: com.example.appquanlybenhancanhan.data.LichHenDao
    private var selectedLichHen: LichHen? = null

    private lateinit var lichHenAdapter: ArrayAdapter<String>
    private val lichHenDisplayList = mutableListOf<String>()
    private var currentLichHenList = listOf<LichHen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_dat_lich_hen_tai_kham)

        setControl()
        setupSpinners()
        setEvent()

        db = AppDatabase.getDatabase(applicationContext)
        lichHenDao = db.lichHenDao()

        lichHenAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lichHenDisplayList)
        lvDanhSach.adapter = lichHenAdapter

        lifecycleScope.launch {
            lichHenDao.getAllLichHen().collectLatest { list ->
                currentLichHenList = list
                lichHenDisplayList.clear()
                list.forEach { lh ->
                    lichHenDisplayList.add(
                        "${lh.benhVien}\n${lh.ngay} ${lh.gio}\nTrạng thái: ${lh.trangThai}"
                    )
                }
                lichHenAdapter.notifyDataSetChanged()
            }
        }

        // Toolbar back
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainHome::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
    }

    private fun setControl() {
        spBenhVien = findViewById(R.id.spBenhVien)
        edtNgayKham = findViewById(R.id.edtNgayKham)
        edtGioKham = findViewById(R.id.edtGioKham)
        spTrangThai = findViewById(R.id.spTrangThai)
        btnThem = findViewById(R.id.btnThem)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        btnIn = findViewById(R.id.btnIn)
        lvDanhSach = findViewById(R.id.lvDanhSach)
    }

    private fun setupSpinners() {
        // Spinner bệnh viện
        val benhVienList = arrayOf("Chọn bệnh viện", "BV Chợ Rẫy", "BV 115", "BV Y Dược", "BV Tâm Đức")
        spBenhVien.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, benhVienList)

        // Spinner trạng thái
        val trangThaiList = arrayOf("Chưa khám", "Đã khám", "Hủy")
        spTrangThai.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, trangThaiList)
    }

    private fun setEvent() {
        // Chọn ngày
        edtNgayKham.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                edtNgayKham.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Chọn giờ
        edtGioKham.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                edtGioKham.setText(String.format("%02d:%02d", h, m))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        // Thêm mới
        btnThem.setOnClickListener {
            val benhVien = spBenhVien.selectedItem.toString()
            val ngay = edtNgayKham.text.toString().trim()
            val gio = edtGioKham.text.toString().trim()
            val trangThai = spTrangThai.selectedItem.toString()

            if (benhVien == "Chọn bệnh viện" || ngay.isEmpty() || gio.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newLichHen = LichHen(benhVien = benhVien, ngay = ngay, gio = gio, trangThai = trangThai)

            lifecycleScope.launch(Dispatchers.IO) {
                lichHenDao.insert(newLichHen)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainDatLichHenTaiKham, "Đã thêm lịch hẹn!", Toast.LENGTH_SHORT).show()
                    clearInput()
                }
            }
        }

        // Sửa
        btnSua.setOnClickListener {
            if (selectedLichHen == null) {
                Toast.makeText(this, "Vui lòng chọn lịch hẹn để sửa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val benhVien = spBenhVien.selectedItem.toString()
            val ngay = edtNgayKham.text.toString().trim()
            val gio = edtGioKham.text.toString().trim()
            val trangThai = spTrangThai.selectedItem.toString()

            if (benhVien == "Chọn bệnh viện" || ngay.isEmpty() || gio.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = selectedLichHen!!.copy(
                benhVien = benhVien,
                ngay = ngay,
                gio = gio,
                trangThai = trangThai
            )

            lifecycleScope.launch(Dispatchers.IO) {
                lichHenDao.update(updated)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainDatLichHenTaiKham, "Đã sửa lịch hẹn!", Toast.LENGTH_SHORT).show()
                    clearInput()
                }
            }
        }

        // Xóa
        btnXoa.setOnClickListener {
            if (selectedLichHen == null) {
                Toast.makeText(this, "Vui lòng chọn lịch hẹn để xóa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa lịch hẹn này?")
                .setPositiveButton("Xóa") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        lichHenDao.delete(selectedLichHen!!)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainDatLichHenTaiKham, "Đã xóa!", Toast.LENGTH_SHORT).show()
                            clearInput()
                        }
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        // Chọn từ list
        lvDanhSach.setOnItemClickListener { _, _, position, _ ->
            selectedLichHen = currentLichHenList[position]
            val lh = selectedLichHen!!

            // Điền thông tin
            edtNgayKham.setText(lh.ngay)
            edtGioKham.setText(lh.gio)

            // Set spinner bệnh viện
            val bvAdapter = spBenhVien.adapter as ArrayAdapter<String>
            val bvPos = bvAdapter.getPosition(lh.benhVien)
            spBenhVien.setSelection(if (bvPos >= 0) bvPos else 0)

            // Set spinner trạng thái
            val ttAdapter = spTrangThai.adapter as ArrayAdapter<String>
            val ttPos = ttAdapter.getPosition(lh.trangThai)
            spTrangThai.setSelection(if (ttPos >= 0) ttPos else 0)

            Toast.makeText(this, "Đã chọn để sửa/xóa", Toast.LENGTH_SHORT).show()
        }

        // In danh sách (tạm thời)
        btnIn.setOnClickListener {
            Toast.makeText(this, "Chức năng in đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInput() {
        edtNgayKham.text.clear()
        edtGioKham.text.clear()
        spBenhVien.setSelection(0)
        spTrangThai.setSelection(0)
        selectedLichHen = null
    }
}