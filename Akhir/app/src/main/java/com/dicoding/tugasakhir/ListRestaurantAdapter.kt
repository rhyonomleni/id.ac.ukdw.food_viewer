package com.dicoding.tugasakhir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tugasakhir.databinding.ItemRowRestaurantBinding

class ListRestaurantAdapter(
    private val onItemClick: (Results) -> Unit
) : RecyclerView.Adapter<ListRestaurantAdapter.ListViewHolder>() {

    private val restaurantList = mutableListOf<Results>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(restaurantList[position])
    }

    override fun getItemCount(): Int = restaurantList.size

    fun submitList(list: List<Results>) {
        restaurantList.clear()
        restaurantList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ListViewHolder(private val binding: ItemRowRestaurantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Results) {
            binding.tvRestaurantName.text = result.name
            binding.tvItemAlamat.text = result.address
            binding.imgItemPhoto.setImageResource(result.photo)

            itemView.setOnClickListener {
                onItemClick(result)
            }
        }
    }
}