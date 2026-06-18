package com.example.appquanlybenhancanhan.UI.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.DonThuoc

class DonThuocAdapter : RecyclerView.Adapter<DonThuocAdapter.ViewHolder>() {

    private var dataList = listOf<DonThuoc>()

    fun setData(newList: List<DonThuoc>) {
        dataList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_don_thuoc_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMaDon = itemView.findViewById<TextView>(R.id.tvMaDon)
        private val tvTenThuoc = itemView.findViewById<TextView>(R.id.tvTenThuoc)
        private val tvLieuDung = itemView.findViewById<TextView>(R.id.tvLieuDung)
        private val tvGhiChu = itemView.findViewById<TextView>(R.id.tvGhiChu)

        fun bind(item: DonThuoc) {
            tvMaDon.text = "Mã đơn thuốc: ${item.maThuoc}"
            tvTenThuoc.text = "Tên thuốc: ${item.tenThuoc}"
            tvLieuDung.text = "Liều dùng: ${item.lieuDung}"

            if (item.ghiChu.isNullOrBlank()) {
                tvGhiChu.visibility = View.GONE
            } else {
                tvGhiChu.text = "Ghi chú: ${item.ghiChu}"
                tvGhiChu.visibility = View.VISIBLE
            }
        }
    }
}