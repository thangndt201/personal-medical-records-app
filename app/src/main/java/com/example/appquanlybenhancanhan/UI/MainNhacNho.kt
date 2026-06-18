package com.example.appquanlybenhancanhan.UI

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.NhacNho
import com.example.appquanlybenhancanhan.data.NhacNhoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainNhacNho : AppCompatActivity() {
    private lateinit var edtMedicineName: EditText
    private lateinit var edtDosage: EditText
    private lateinit var edtGio: EditText
    private lateinit var cbRepeat: CheckBox
    private lateinit var snThongBao: Switch
    private lateinit var btnSave: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var lvDanhSach: ListView

    // Database components
    private lateinit var db: AppDatabase
    private lateinit var nhacNhoDao: NhacNhoDao
    private var selectedNhacNho: NhacNho? = null

    // ListView Adapter and data sources
    private lateinit var nhacNhoAdapter: ArrayAdapter<String>
    private val nhacNhoDisplayList = mutableListOf<String>()
    private var currentNhacNhoList = listOf<NhacNho>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_nhac_nho)
        setControl()

        // Init DB
        db = AppDatabase.getDatabase(applicationContext)
        nhacNhoDao = db.nhacNhoDao()

        // Setup Adapter
        nhacNhoAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nhacNhoDisplayList)
        lvDanhSach.adapter = nhacNhoAdapter

        // Observe data changes from Room
        lifecycleScope.launch {
            nhacNhoDao.getAllNhacNho().collectLatest { nhacNhoList ->
                currentNhacNhoList = nhacNhoList
                nhacNhoDisplayList.clear()
                nhacNhoList.forEach { nhacNho ->
                    val lapLaiText = if (nhacNho.lapLai) "Hằng ngày" else "Một lần"
                    nhacNhoDisplayList.add("${nhacNho.tenThuoc} - ${nhacNho.lieuLuong}\nGiờ: ${nhacNho.gio} | $lapLaiText")
                }
                nhacNhoAdapter.notifyDataSetChanged()
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
        edtMedicineName = findViewById(R.id.edtMedicineName)
        edtDosage = findViewById(R.id.edtDosage)
        edtGio = findViewById(R.id.edtGio)
        cbRepeat = findViewById(R.id.cbRepeat)
        snThongBao = findViewById(R.id.snThongBao)
        btnSave = findViewById(R.id.btnSave)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        lvDanhSach = findViewById(R.id.lvDanhSach)
    }

    private fun setEvent() {
        // Chọn giờ
        edtGio.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, h, m -> edtGio.setText(String.format("%02d:%02d", h, m)) },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Lưu
        btnSave.setOnClickListener {
            val ten = edtMedicineName.text.toString().trim()
            val lieu = edtDosage.text.toString().trim()
            val gio = edtGio.text.toString().trim()
            if (ten.isEmpty() || lieu.isEmpty() || gio.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nhacNho = NhacNho(
                tenThuoc = ten,
                lieuLuong = lieu,
                gio = gio,
                lapLai = cbRepeat.isChecked,
                thongBao = snThongBao.isChecked
            )

            lifecycleScope.launch(Dispatchers.IO) {
                nhacNhoDao.insert(nhacNho)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainNhacNho, "Đã lưu nhắc nhở!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }

        // Sửa
        btnSua.setOnClickListener {
            if (selectedNhacNho == null) {
                Toast.makeText(this, "Vui lòng chọn nhắc nhở cần sửa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ten = edtMedicineName.text.toString().trim()
            val lieu = edtDosage.text.toString().trim()
            val gio = edtGio.text.toString().trim()
            if (ten.isEmpty() || lieu.isEmpty() || gio.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedNhacNho = selectedNhacNho!!.copy(
                tenThuoc = ten,
                lieuLuong = lieu,
                gio = gio,
                lapLai = cbRepeat.isChecked,
                thongBao = snThongBao.isChecked
            )

            lifecycleScope.launch(Dispatchers.IO) {
                nhacNhoDao.update(updatedNhacNho)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainNhacNho, "Đã cập nhật nhắc nhở!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }

        // Xóa
        btnXoa.setOnClickListener {
            if (selectedNhacNho == null) {
                Toast.makeText(this, "Vui lòng chọn nhắc nhở cần xóa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa nhắc nhở này không?")
                .setPositiveButton("Xóa") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        nhacNhoDao.delete(selectedNhacNho!!)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainNhacNho, "Đã xóa nhắc nhở.", Toast.LENGTH_SHORT).show()
                            clearFields()
                        }
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        // Chọn item
        lvDanhSach.setOnItemClickListener { _, _, position, _ ->
            if (position < currentNhacNhoList.size) {
                selectedNhacNho = currentNhacNhoList[position]
                val item = selectedNhacNho!!
                edtMedicineName.setText(item.tenThuoc)
                edtDosage.setText(item.lieuLuong)
                edtGio.setText(item.gio)
                cbRepeat.isChecked = item.lapLai
                snThongBao.isChecked = item.thongBao
            }
        }
    }

    private fun clearFields() {
        edtMedicineName.text.clear()
        edtDosage.text.clear()
        edtGio.text.clear()
        cbRepeat.isChecked = false
        snThongBao.isChecked = true
        selectedNhacNho = null
        lvDanhSach.clearChoices()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có muốn thoát không?")
            .setPositiveButton("Có") { _, _ -> finish() }
            .setNegativeButton("Không", null)
            .show()
    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }
}