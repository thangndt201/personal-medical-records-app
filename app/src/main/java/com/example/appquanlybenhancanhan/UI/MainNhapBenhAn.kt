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
import com.example.appquanlybenhancanhan.data.BenhAn
import com.example.appquanlybenhancanhan.data.BenhAnDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts

class MainNhapBenhAn : AppCompatActivity() {
    private lateinit var edtTenBenh: EditText
    private lateinit var edtMoTa: EditText
    private lateinit var btnChonTep: Button
    private lateinit var btnLuu: Button
    private lateinit var btnSua: Button
    private lateinit var btnXoa: Button
    private lateinit var lvBenhAn: ListView
    private var fileUri: Uri? = null
    private lateinit var tvTenTep: TextView

    // Database components
    private lateinit var db: AppDatabase
    private lateinit var benhAnDao: BenhAnDao
    private var selectedBenhAn: BenhAn? = null

    // ListView Adapter and data sources
    private lateinit var benhAnAdapter: ArrayAdapter<String>
    private val benhAnDisplayList = mutableListOf<String>()
    private var currentBenhAnList = listOf<BenhAn>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_nhap_benh_an)
        setControl()

        // Init DB
        db = AppDatabase.getDatabase(applicationContext)
        benhAnDao = db.benhAnDao()

        // Setup Adapter
        benhAnAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, benhAnDisplayList)
        lvBenhAn.adapter = benhAnAdapter

        // Observe data changes from Room
        lifecycleScope.launch {
            benhAnDao.getAllBenhAn().collectLatest { benhAnList ->
                currentBenhAnList = benhAnList
                benhAnDisplayList.clear()
                benhAnList.forEach { benhAn ->
                    benhAnDisplayList.add("Bệnh án: ${benhAn.tenBenh}\nMô tả: ${benhAn.moTa}")
                }
                benhAnAdapter.notifyDataSetChanged()
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
        edtTenBenh = findViewById(R.id.edtTenBenh)
        edtMoTa = findViewById(R.id.edtMoTa)
        btnChonTep = findViewById(R.id.btnChonTep)
        tvTenTep = findViewById(R.id.tvTenTep)
        btnLuu = findViewById(R.id.btnLuu)
        btnSua = findViewById(R.id.btnSua)
        btnXoa = findViewById(R.id.btnXoa)
        lvBenhAn = findViewById(R.id.lvBenhAn)
    }
    // Launcher để chọn file
    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            //Lấy quyền truy cập lâu dài
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(it, takeFlags)

            fileUri = it
            val fileName = getFileNameFromUri(it)
            tvTenTep.text = "Tệp đã chọn: $fileName"
            tvTenTep.visibility = View.VISIBLE
        } ?: run {
            tvTenTep.visibility = View.GONE
            fileUri = null
        }
    }

    // Hàm lấy tên file từ URI
    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "Tệp không xác định"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    private fun setEvent() {
        //Chọn tệp
        btnChonTep.setOnClickListener {
            pickFileLauncher.launch(arrayOf("*/*"))
        }

        //lưu
        btnLuu.setOnClickListener {
            val tenBenh = edtTenBenh.text.toString().trim()
            val moTa = edtMoTa.text.toString().trim()

            if (tenBenh.isEmpty() || moTa.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val benhAn = BenhAn(
                tenBenh = tenBenh,
                moTa = moTa,
                duongDanTep = fileUri?.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                benhAnDao.insert(benhAn)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainNhapBenhAn, "Đã lưu bệnh án!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }

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

        btnSua.setOnClickListener {
            if (selectedBenhAn == null) {
                Toast.makeText(this, "Vui lòng chọn bệnh án để sửa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tenBenh = edtTenBenh.text.toString().trim()
            val moTa = edtMoTa.text.toString().trim()

            if (tenBenh.isEmpty() || moTa.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedBenhAn = selectedBenhAn!!.copy(
                tenBenh = tenBenh,
                moTa = moTa,
                duongDanTep = fileUri?.toString() ?: selectedBenhAn!!.duongDanTep  // Giữ nguyên nếu không chọn mới
            )

            lifecycleScope.launch(Dispatchers.IO) {
                benhAnDao.update(updatedBenhAn)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainNhapBenhAn, "Đã cập nhật bệnh án!", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }

        btnXoa.setOnClickListener {
            if (selectedBenhAn == null) {
                Toast.makeText(this, "Vui lòng chọn bệnh án để xóa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bệnh án này không?")
                .setPositiveButton("Có") { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        benhAnDao.delete(selectedBenhAn!!)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainNhapBenhAn, "Đã xóa bệnh án.", Toast.LENGTH_SHORT).show()
                            clearFields()
                        }
                    }
                }
                .setNegativeButton("Không", null)
                .show()
        }

        lvBenhAn.setOnItemClickListener { _, _, position, _ ->
            if (position < currentBenhAnList.size) {
                selectedBenhAn = currentBenhAnList[position]
                val benhAn = selectedBenhAn!!
                edtTenBenh.setText(benhAn.tenBenh)
                edtMoTa.setText(benhAn.moTa)

                if (benhAn.duongDanTep != null) {
                    tvTenTep.text = "Tệp đã chọn: ${getFileNameFromUri(Uri.parse(benhAn.duongDanTep))}"
                    tvTenTep.visibility = View.VISIBLE
                    fileUri = Uri.parse(benhAn.duongDanTep)
                } else {
                    tvTenTep.visibility = View.GONE
                    fileUri = null
                }
            }
        }

    }

    private fun clearFields() {
        edtTenBenh.setText("")
        edtMoTa.setText("")
        tvTenTep.visibility = View.GONE
        fileUri = null
        selectedBenhAn = null
        lvBenhAn.clearChoices()
    }
}
