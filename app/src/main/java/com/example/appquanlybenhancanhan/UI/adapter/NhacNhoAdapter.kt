package com.example.appquanlybenhancanhan.UI.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.AppDatabase
import com.example.appquanlybenhancanhan.data.NhacNho
import kotlinx.coroutines.launch

class NhacNhoAdapter(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<NhacNhoAdapter.ViewHolder>() {

    private var dataList = listOf<NhacNho>()

    fun setData(newList: List<NhacNho>) {
        dataList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nhac_nho_uong_thuoc, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMedicineName = itemView.findViewById<TextView>(R.id.tvMedicineName)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
        private val switchTaken = itemView.findViewById<SwitchCompat>(R.id.switchTaken)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)

        fun bind(item: NhacNho) {
            tvMedicineName.text = item.tenThuoc
            tvTime.text = "⏰ ${item.gio} - ${item.lieuLuong}"

            // Set trạng thái switch và text
            switchTaken.isChecked = item.daUong
            updateStatusText(item.daUong)

            // Khi user gạt switch → cập nhật vào Room
            switchTaken.setOnCheckedChangeListener { _, isChecked ->
                lifecycleOwner.lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(itemView.context)
                    val dao = db.nhacNhoDao()
                    val updated = item.copy(daUong = isChecked)
                    dao.update(updated)
                }
                updateStatusText(isChecked)
            }
        }

        private fun updateStatusText(isTaken: Boolean) {
            if (isTaken) {
                tvStatus.text = "Đã uống"
                tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                tvStatus.text = "Chưa uống"
                tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            }
        }
    }
}