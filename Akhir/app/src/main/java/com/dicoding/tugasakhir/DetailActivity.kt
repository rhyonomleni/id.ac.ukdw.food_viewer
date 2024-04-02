package com.dicoding.tugasakhir

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DetailActivity : AppCompatActivity() {
    private lateinit var textNamaResto: TextView
    private lateinit var textDescription: TextView
    private lateinit var imagePhoto: ImageView
    private lateinit var textAlamat: TextView
    private lateinit var textJamBuka: TextView
    private lateinit var textKontak: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        val actionBar = supportActionBar

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val restaurant: Restaurant? = intent.getParcelableExtra("anime")

        textNamaResto = findViewById(R.id.tv_restaurant_name2)
        textDescription = findViewById(R.id.tv_restaurant_isi)
        imagePhoto = findViewById(R.id.imageView)
        textAlamat = findViewById(R.id.tv_restaurant_isialamat)
        textJamBuka = findViewById(R.id.tv_restaurant_isiJam)
        textKontak = findViewById(R.id.tv_restaurant_isiKontak)

        if (restaurant != null) {
            textNamaResto.text = restaurant.restaurantName
            textDescription.text = restaurant.description
            imagePhoto.setImageResource(restaurant.photo)
            textAlamat.text = restaurant.alamat
            textJamBuka.text = restaurant.jamBuka
            textKontak.text = restaurant.noTelp
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}