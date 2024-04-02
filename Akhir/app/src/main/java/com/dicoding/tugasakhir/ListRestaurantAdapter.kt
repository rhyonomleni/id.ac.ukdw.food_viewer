package com.dicoding.tugasakhir

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

class ListRestaurantAdapter(private val listRestaurant : ArrayList<Restaurant>, private val onClick: (Restaurant) -> Unit) : RecyclerView.Adapter<ListRestaurantAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback : OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_restaurant_name)
        val tvAlamat: TextView = itemView.findViewById(R.id.tv_item_alamat)
        val tvNoTelp: TextView = itemView.findViewById(R.id.tv_item_noTelp)
        fun bind(restaurant: Restaurant){
            itemView.setOnClickListener{
                onClick(restaurant)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_restaurant, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listRestaurant.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val restaurant = listRestaurant[position]
        holder.imgPhoto.setImageResource(restaurant.photo)
        holder.tvName.text = restaurant.restaurantName
        holder.tvAlamat.text = restaurant.alamat
        holder.tvNoTelp.text = restaurant.noTelp
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(restaurant)
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(data : Restaurant)
    }

}