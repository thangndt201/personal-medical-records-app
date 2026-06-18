package com.example.appquanlybenhancanhan.UI.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.KetQuaXetNghiem

class KetQuaXetNghiemAdapter : RecyclerView.Adapter<KetQuaXetNghiemAdapter.ViewHolder>() {

    private var data = listOf<KetQuaXetNghiem>()

    fun setData(newData: List<KetQuaXetNghiem>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ket_qua_xet_nghiem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMaHoSo = itemView.findViewById<TextView>(R.id.tvMaHoSo)
        private val tvNgayXN = itemView.findViewById<TextView>(R.id.tvNgayXN)
        private val tvLoaiXN = itemView.findViewById<TextView>(R.id.tvLoaiXN)
        private val tvChiSo = itemView.findViewById<TextView>(R.id.tvChiSo)
        private val tvKetQua = itemView.findViewById<TextView>(R.id.tvKetQua)
        private val tvGhiChu = itemView.findViewById<TextView>(R.id.tvGhiChu)

        fun bind(item: KetQuaXetNghiem) {
            tvMaHoSo.text = "Mã hồ sơ: ${item.maHoSo}"
            tvNgayXN.text = "Ngày xét nghiệm: ${item.ngayXetNghiem}"
            tvLoaiXN.text = "Loại xét nghiệm: ${item.loaiXetNghiem}"
            tvChiSo.text = "Chỉ số: ${item.chiSo}"
            tvKetQua.text = "Kết quả: ${item.ketQua}"

            if (item.ghiChu.isNullOrBlank()) {
                tvGhiChu.visibility = View.GONE
            } else {
                tvGhiChu.text = "Ghi chú: ${item.ghiChu}"
                tvGhiChu.visibility = View.VISIBLE
            }
        }
    }
}