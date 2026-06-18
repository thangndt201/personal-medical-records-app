package com.example.appquanlybenhancanhan.UI.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanlybenhancanhan.R
import com.example.appquanlybenhancanhan.data.User

class UserAdapter(
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var users = listOf<User>()

    fun setUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvFullName: TextView = itemView.findViewById(R.id.tvFullName)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvGender: TextView = itemView.findViewById(R.id.tvGender)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(user: User) {
            tvUsername.text = user.username
            tvFullName.text = user.fullName
            tvPhone.text = "SĐT: ${user.phone ?: "-"}"
            tvEmail.text = "Email: ${user.email ?: "-"}"
            tvGender.text = "Giới tính: ${user.gender ?: "Nam"}"

            btnDelete.setOnClickListener {
                onDeleteClick(user)
            }
        }
    }
}