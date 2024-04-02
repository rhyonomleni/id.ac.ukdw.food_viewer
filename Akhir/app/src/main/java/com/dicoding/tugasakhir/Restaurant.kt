package com.dicoding.tugasakhir
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val restaurantName: String,
    val description: String,
    val photo: Int,
    val alamat: String,
    val noTelp: String,
    val jamBuka : String
) : Parcelable