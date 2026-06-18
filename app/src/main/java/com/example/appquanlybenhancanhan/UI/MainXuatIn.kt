package com.example.appquanlybenhancanhan.UI

import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainXuatIn : AppCompatActivity() {

    private lateinit var rgLoaiDuLieu: RadioGroup
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_xuat_in)

        rgLoaiDuLieu = findViewById(R.id.rgLoaiDuLieu)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<Button>(R.id.btnXuatPDF).setOnClickListener {
            xuatPDF()
        }

        findViewById<Button>(R.id.btnIn).setOnClickListener {
            inTrucTiep()
        }
    }

    private fun xuatPDF() {
        val selectedId = rgLoaiDuLieu.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Vui lòng chọn loại dữ liệu!", Toast.LENGTH_SHORT).show()
            return
        }

        val type = when (selectedId) {
            R.id.rbBenhNhan -> "Danh sách bệnh nhân"
            R.id.rbLichHen -> "Lịch tái khám"
            R.id.rbDonThuoc -> "Đơn thuốc"
            R.id.rbKetQuaXN -> "Kết quả xét nghiệm"
            R.id.rbBenhAn -> "Bệnh án"
            else -> return
        }

        lifecycleScope.launch {
            val content = taoNoiDungPDF(type)

            val safeType = type.replace(" ", "_").replace("[^a-zA-Z0-9_\\-]".toRegex(), "")
            val fileName = "${safeType}_${SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(Date())}.pdf"
            val file = File(getExternalFilesDir(null), fileName)
            try {
                val document = Document()
                PdfWriter.getInstance(document, FileOutputStream(file))
                document.open()

                document.add(Paragraph("HỆ THỐNG QUẢN LÝ BỆNH ÁN CÁ NHÂN\n\n"))
                document.add(Paragraph("BÁO CÁO: $type\n"))
                document.add(Paragraph("Ngày xuất: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}\n\n"))
                document.add(Paragraph(content))

                document.close()

                // Hiển thị Toast trên Main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainXuatIn, "Đã lưu PDF thành công!\n$fileName", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainXuatIn, "Lỗi xuất PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }
        }
    }

    private suspend fun taoNoiDungPDF(type: String): String {
        return when (type) {
            "Danh sách bệnh nhân" -> {
                val list = db.userDao().getAllUsers().first()
                list.joinToString("\n\n") { "Họ tên: ${it.fullName}\nSĐT: ${it.phone ?: "Chưa có"}" }
            }
            "Lịch tái khám" -> {
                val list = db.lichHenDao().getAllLichHen().first()
                list.joinToString("\n\n") { "Bệnh viện: ${it.benhVien}\nNgày: ${it.ngay} - Giờ: ${it.gio}\nTrạng thái: ${it.trangThai}" }
            }
            "Đơn thuốc" -> {
                val list = db.donThuocDao().getAllDonThuoc().first()
                list.joinToString("\n\n") { "Tên thuốc: ${it.tenThuoc}\nLiều: ${it.lieuDung}" }
            }
            "Kết quả xét nghiệm" -> {
                val list = db.ketQuaXetNghiemDao().getAllKetQua().first()
                list.joinToString("\n\n") { "Loại: ${it.loaiXetNghiem}\nChỉ số: ${it.chiSo}\nKết quả: ${it.ketQua}" }
            }
            "Bệnh án" -> {
                val list = db.benhAnDao().getAllBenhAn().first()
                list.joinToString("\n\n") { "Bệnh: ${it.tenBenh}\nMô tả: ${it.moTa}" }
            }
            else -> "Không có dữ liệu"
        }
    }

    private fun inTrucTiep() {
        // Tương tự xuất PDF nhưng dùng PrintManager
        Toast.makeText(this, "Chức năng in trực tiếp đang phát triển", Toast.LENGTH_SHORT).show()
    }
}