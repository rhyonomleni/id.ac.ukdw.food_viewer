package com.dicoding.tugasakhir

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class UbahActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    private val REQUEST_IMAGE_CAPTURE = 101
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            // Handle the selected image URI
            Toast.makeText(this, "Gambar terpilih: $uri", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah)

        // Dapatkan user ID dari Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val foto = findViewById<Button>(R.id.btnAmbilFoto)
        foto.setOnClickListener {
            openCamera()
        }

        val galeri = findViewById<Button>(R.id.btnPilihFoto)
        galeri.setOnClickListener {
            openGaleri()
        }

        // Panggil fungsi untuk mengambil data dari Firestore
        getUserDataFromFirestore(userId)
        getUsernameFromFirestore(userId)

    }

    private fun getUserDataFromFirestore(userId: String) {
        // Akses koleksi 'users' di Firestore dan ambil dokumen berdasarkan userID
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Dokumen ditemukan, ambil data dari Firestore
                    val profilePictureUrl = document.getString("profilePicture")

                    // Memuat gambar profil ke dalam ImageView menggunakan Glide
                    profilePictureUrl?.let {
                        Glide.with(this@UbahActivity)
                            .load(profilePictureUrl)
                            .placeholder(R.drawable.account)
                            .error(R.drawable.account)
                            .into(findViewById(R.id.circleImageView))
                    }
                } else {
                    // Dokumen tidak ditemukan atau kosong
                    // Tambahkan penanganan jika dokumen tidak ditemukan
                }

            }
            .addOnFailureListener { exception ->
                // Gagal mengambil data dari Firestore
                // Tambahkan penanganan jika terjadi kesalahan
            }
    }

    private fun getUsernameFromFirestore(userId: String) {
        // Akses koleksi 'users' di Firestore dan ambil dokumen berdasarkan userID
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Dokumen ditemukan, ambil data username dari Firestore
                    val username = document.getString("username")

                    // Set nilai username ke dalam EditText
                    val usernameEditText = findViewById<EditText>(R.id.editText)
                    usernameEditText.setText(username)
                } else {
                    // Dokumen tidak ditemukan atau kosong
                    // Tambahkan penanganan jika dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Gagal mengambil data dari Firestore
                // Tambahkan penanganan jika terjadi kesalahan
            }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
    private fun openGaleri() {
        // Launch gallery intent with appropriate MIME type for selecting an image
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"  // Ensure only images are selectable
        if (galleryIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(galleryIntent, REQUEST_IMAGE_CAPTURE) // Use a distinct request code
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Lakukan sesuatu dengan gambar yang diambil (imageBitmap) di sini
            imageBitmap?.let {
                Glide.with(this@UbahActivity)
                    .load(imageBitmap)
                    .placeholder(R.drawable.account)
                    .error(R.drawable.account)
                    .into(findViewById(R.id.circleImageView))
            }
        }
    }



//    private fun openGaleri() {
//        // Gunakan intent untuk membuka aplikasi galeri foto
//        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(galleryIntent, REQUEST_IMAGE_CAPTURE)
//        val imageBitmap = intent?.extras?.get("data") as Bitmap
//        // Lakukan sesuatu dengan gambar yang diambil (imageBitmap) di sini
//        imageBitmap?.let {
//            Glide.with(this@UbahActivity)
//                .load(imageBitmap)
//                .placeholder(R.drawable.account)
//                .error(R.drawable.account)
//                .into(findViewById(R.id.circleImageView))
//        }
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            val selectedImageUri = data?.data ?: return  // Handle potential null URI
//            val imageBitmap = try {
//                // Attempt to safely decode the image from the URI
//                val inputStream = contentResolver.openInputStream(selectedImageUri)
//                BitmapFactory.decodeStream(inputStream)
//            } catch (e: Exception) {
//                // Handle potential exceptions during image decoding (optional)
//                e.printStackTrace()
//                null
//            }
//
//            imageBitmap?.let {
//                // Use Glide to load the image into the circleImageView
//                Glide.with(this@UbahActivity)
//                    .load(it)
//                    .placeholder(R.drawable.account)
//                    .error(R.drawable.account)
//                    .into(findViewById(R.id.circleImageView))
//            }
//        }
//    }

}