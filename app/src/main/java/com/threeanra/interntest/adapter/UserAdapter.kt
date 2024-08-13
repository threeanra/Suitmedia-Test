package com.threeanra.interntest.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.threeanra.interntest.R
import com.threeanra.interntest.data.network.response.DataItem
import com.threeanra.interntest.databinding.UserListBinding

class UserAdapter(private val itemClickListener: (String) -> Unit) : ListAdapter<DataItem, UserAdapter.MyViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = UserListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, itemClickListener)
    }

    class MyViewHolder(private val binding: UserListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: DataItem, itemClickListener: (String) -> Unit) {
            val fullname = "${user.firstName} ${user.lastName}"
            binding.tvFullname.text = fullname
            binding.tvEmail.text = user.email
            Glide.with(binding.imageUser.context)
                .load(user.avatar)
                .apply(RequestOptions().error(R.drawable.rv_photo))
                .into(binding.imageUser)

            binding.root.setOnClickListener {
                itemClickListener(fullname)
            }
        }
    }

    private class UserDiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val FULLNAME = "fullname"
        const val RESULT_CODE = 1
    }
}
