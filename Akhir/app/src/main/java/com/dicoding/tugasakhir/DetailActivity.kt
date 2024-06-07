package com.dicoding.tugasakhir

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {
    private lateinit var textNamaResto: TextView
    private lateinit var textDescription: TextView
    private lateinit var imagePhoto: ImageView
    private lateinit var textAlamat: TextView
    private lateinit var textJamBuka: TextView
    private lateinit var textKontak: TextView
    private lateinit var favoriteBtn: Button

    // firestore
    private val db = FirebaseFirestore.getInstance()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        textNamaResto = findViewById(R.id.tv_restaurant_name2)
        textDescription = findViewById(R.id.tv_restaurant_isi)
        imagePhoto = findViewById(R.id.imageView)
        textAlamat = findViewById(R.id.tv_restaurant_isialamat)
        textJamBuka = findViewById(R.id.tv_restaurant_isiJam)
        textKontak = findViewById(R.id.tv_restaurant_isiKontak)
        favoriteBtn = findViewById(R.id.buttonFav)

        val restaurant: Restaurant? = intent.getParcelableExtra("anime")
        if (restaurant != null) {
            textNamaResto.text = restaurant.restaurantName
            textDescription.text = restaurant.description
            imagePhoto.setImageResource(restaurant.photo)
            textAlamat.text = restaurant.alamat
            textJamBuka.text = restaurant.jamBuka
            textKontak.text = restaurant.noTelp

            // add to favorite
            favoriteBtn.setOnClickListener { saveToFirestore(userId) }
        }
    }

    private fun saveToFirestore(uid: String){
        val db = FirebaseFirestore.getInstance()

        val restaurantName = textNamaResto.text.toString()
        val description = textDescription.text.toString()
        val alamat = textAlamat.text.toString()
        val jamBuka = textJamBuka.text.toString()
        val noTelp = textKontak.text.toString()

//        val photoResId = (imagePhoto.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
//            val stream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            stream.toByteArray()
//        }

        val restaurant = hashMapOf(
            "name" to restaurantName,
            "description" to description,
            "alamat" to alamat,
            "jamBuka" to jamBuka,
            "noTelp" to noTelp,
            //"photo" to (photoResId ?: ByteArray(0))  // Menggunakan array kosong jika photoResId null
        )

        db.collection("users").document(uid).collection("favorites")
            .add(restaurant)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(this, "Gagal menyimpan data.", Toast.LENGTH_SHORT).show()
            }
    }

//    private fun addRestaurantToFavorites(googleId: String) {
//
//        val restaurantName = textNamaResto.text.toString()
//        val description = textDescription.text.toString()
//        val alamat = textAlamat.text.toString()
//        val jamBuka = textJamBuka.text.toString()
//        val noTelp = textKontak.text.toString()
//        Toast.makeText(this, restaurantName, Toast.LENGTH_SHORT).show()
//
//        val restaurant = hashMapOf(
//            "name" to restaurantName,
//            "description" to description,
//            "alamat" to alamat,
//            "jamBuka" to jamBuka,
//            "noTelp" to noTelp,
//            //"photo" to (photoResId ?: ByteArray(0))  // Menggunakan array kosong jika photoResId null
//        )
//        Toast.makeText(this, "hashMap created", Toast.LENGTH_SHORT).show()
//
//        val favoriteRef = db.collection("users").document(googleId).collection("favorites")
//        favoriteRef
//            .add(restaurant)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to add to favorites: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}