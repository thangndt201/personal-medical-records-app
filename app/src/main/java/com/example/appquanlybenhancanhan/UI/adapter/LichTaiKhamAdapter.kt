package com.example.appquanlybenhancanhan.UI.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.LichHen

class LichTaiKhamAdapter : RecyclerView.Adapter<LichTaiKhamAdapter.ViewHolder>() {

    private var data = listOf<LichHen>()

    fun setData(newData: List<LichHen>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lich_tai_kham_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBenhVien = itemView.findViewById<TextView>(R.id.tvBenhVien)
        private val tvNgayKham = itemView.findViewById<TextView>(R.id.tvNgayKham)
        private val tvGioKham = itemView.findViewById<TextView>(R.id.tvGioKham)
        private val tvTrangThai = itemView.findViewById<TextView>(R.id.tvTrangThai)

        fun bind(item: LichHen) {
            tvBenhVien.text = item.benhVien
            tvNgayKham.text = item.ngay
            tvGioKham.text = item.gio

            when (item.trangThai) {
                "Đã khám" -> {
                    tvTrangThai.text = "Đã khám"
                    tvTrangThai.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"))
                    tvTrangThai.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                }
                "Hủy" -> {
                    tvTrangThai.text = "Đã hủy"
                    tvTrangThai.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"))
                    tvTrangThai.setTextColor(android.graphics.Color.parseColor("#C62828"))
                }
                else -> {
                    tvTrangThai.text = "Chưa khám"
                    tvTrangThai.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"))
                    tvTrangThai.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
                }
            }
        }
    }
}