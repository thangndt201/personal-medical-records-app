package com.example.appquanlybenhancanhan.UI

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.Patient
import com.example.appquanlybenhancanhan.data.PatientDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainQuanLyThongTinBenhNhan : AppCompatActivity() {
    private lateinit var edtHoTen: EditText
    private lateinit var edtNgaySinh: EditText
    private lateinit var edtQueQuan: EditText
    private lateinit var edtCCCD: EditText
    private lateinit var edtSDT: EditText
    private lateinit var radNam: RadioButton
    private lateinit var radNu: RadioButton
    private lateinit var edtMaHoSo: EditText
    private lateinit var btnLuu: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var listBenhNhan: ListView
    private lateinit var radioGroup: RadioGroup

    private lateinit var db: AppDatabase
    private lateinit var patientDao: PatientDao
    private var selectedPatient: Patient? = null

    private lateinit var patientAdapter: ArrayAdapter<String>
    private val patientDisplayList = mutableListOf<String>()
    private var currentPatients = listOf<Patient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_quan_ly_thong_tin_benh_nhan)

        setControl()

        // Khởi tạo Room Database
        db = AppDatabase.getDatabase(applicationContext)
        patientDao = db.patientDao()

        // Setup adapter cho ListView
        patientAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, patientDisplayList)
        listBenhNhan.adapter = patientAdapter

        lifecycleScope.launch {
            patientDao.getAllPatients().collectLatest { patients ->
                currentPatients = patients
                patientDisplayList.clear()
                patients.forEach { patient ->
                    patientDisplayList.add(
                        "Mã: ${patient.maHoSo}\nTên: ${patient.hoTen}\n" +
                        "Giới tính: ${patient.gioiTinh}\nNgày sinh: ${patient.ngaySinh}\n" +
                        "Quê: ${patient.queQuan}\nCCCD: ${patient.cccd}"
                    )
                }
                patientAdapter.notifyDataSetChanged()
            }
        }

        setEvent()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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
        edtHoTen = findViewById(R.id.edtHoTen)
        edtNgaySinh = findViewById(R.id.edtNamSinh)
        edtQueQuan = findViewById(R.id.edtQueQuan)
        edtCCCD = findViewById(R.id.edtCCCD)
        edtSDT = findViewById(R.id.edtSDT)
        radNam = findViewById(R.id.radNam)
        radNu = findViewById(R.id.radNu)
        edtMaHoSo = findViewById(R.id.edtMaHoSo)
        btnLuu = findViewById(R.id.btnLuu)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        listBenhNhan = findViewById(R.id.listBenhNhan)
        radioGroup = findViewById(R.id.radioGroup)
    }

    private fun setEvent() {
        // Khi click vào ô Ngày sinh → hiện lịch chọn ngày
        edtNgaySinh.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Định dạng ngày thành dd/mm/yyyy
                    val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    edtNgaySinh.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Nút Lưu
        btnLuu.setOnClickListener {
            if (selectedPatient != null) {
                Toast.makeText(this, "Bệnh nhân đã tồn tại, vui lòng dùng nút 'Sửa'", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            savePatientData()
        }

        // Nút Sửa
        btnSua.setOnClickListener {
            if (selectedPatient == null) {
                Toast.makeText(this, "Vui lòng chọn một bệnh nhân từ danh sách để sửa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            savePatientData(isUpdating = true)
        }

        btnXoa.setOnClickListener {
            if (selectedPatient == null) {
                Toast.makeText(this, "Vui lòng chọn bệnh nhân để xóa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa bệnh nhân này?")
                .setPositiveButton("Có") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        patientDao.delete(selectedPatient!!)
                        withContext(Dispatchers.Main) {
                            clearInput()
                            Toast.makeText(this@MainQuanLyThongTinBenhNhan, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Không", null)
                .show()
        }

        listBenhNhan.setOnItemClickListener { _, _, position, _ ->
            if (position < currentPatients.size) {
                loadPatientToForm(currentPatients[position])
            }
        }
    }

    private fun savePatientData(isUpdating: Boolean = false) {
        val ma = edtMaHoSo.text.toString().trim()
        val ten = edtHoTen.text.toString().trim()
        val gt = if (radNam.isChecked) "Nam" else if (radNu.isChecked) "Nữ" else ""
        val ns = edtNgaySinh.text.toString().trim()
        val qq = edtQueQuan.text.toString().trim()
        val cccd = edtCCCD.text.toString().trim()
        val sdt = edtSDT.text.toString().trim()

        if (ma.isEmpty() || ten.isEmpty() || gt.isEmpty() || ns.isEmpty() || qq.isEmpty() || cccd.isEmpty() || sdt.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }
        if (!cccd.matches(Regex("^[0-9]+$")) || !sdt.matches(Regex("^[0-9]+$"))) {
            Toast.makeText(this, "CCCD và SĐT chỉ được chứa số!", Toast.LENGTH_SHORT).show()
            return
        }

        val patient = if (isUpdating) {
            selectedPatient!!.copy(maHoSo = ma, hoTen = ten, gioiTinh = gt, ngaySinh = ns, queQuan = qq, cccd = cccd, sdt = sdt)
        } else {
            Patient(maHoSo = ma, hoTen = ten, gioiTinh = gt, ngaySinh = ns, queQuan = qq, cccd = cccd, sdt = sdt)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            if (isUpdating) {
                patientDao.update(patient)
            } else {
                patientDao.insert(patient)
            }
            withContext(Dispatchers.Main) {
                val message = if (isUpdating) "Cập nhật thành công!" else "Thêm mới thành công!"
                Toast.makeText(this@MainQuanLyThongTinBenhNhan, message, Toast.LENGTH_SHORT).show()
                clearInput()
            }
        }
    }

    private fun loadPatientToForm(patient: Patient) {
        selectedPatient = patient
        edtMaHoSo.setText(patient.maHoSo)
        edtHoTen.setText(patient.hoTen)
        if (patient.gioiTinh == "Nam") radNam.isChecked = true else radNu.isChecked = true
        edtNgaySinh.setText(patient.ngaySinh)
        edtQueQuan.setText(patient.queQuan)
        edtCCCD.setText(patient.cccd)
        edtSDT.setText(patient.sdt)
        edtMaHoSo.isEnabled = true
        Toast.makeText(this, "Đã chọn bệnh nhân để sửa/xóa", Toast.LENGTH_SHORT).show()
    }

    private fun clearInput() {
        edtHoTen.text.clear()
        edtNgaySinh.text.clear()
        edtQueQuan.text.clear()
        edtCCCD.text.clear()
        edtSDT.text.clear()
        edtMaHoSo.text.clear()
        radioGroup.clearCheck()
        selectedPatient = null
        edtMaHoSo.isEnabled = true
    }
}