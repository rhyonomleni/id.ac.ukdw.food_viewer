package com.dicoding.tugasakhir

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.dicoding.tugasakhir.ui.profile.ProfileFragment
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import com.dicoding.tugasakhir.R
import com.dicoding.tugasakhir.databinding.ActivityUbahBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class UbahActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()
    private lateinit var binding : ActivityUbahBinding
    private var currentPhotoPath: String = ""
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

        val usernameEditText = findViewById<EditText>(R.id.editText)
        val simpan = findViewById<Button>(R.id.btnSimpan)
        simpan.setOnClickListener {
            val newName = usernameEditText.text.toString().trim()
            uploadFoto(currentPhotoPath)


            if (newName.isNotEmpty()) {
                // Memperbarui field 'name' di Firestore
                updateName(newName)
            } else {
                // Tanggapan jika EditText kosong
            }
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Panggil fungsi untuk mengambil data dari Firestore
        getUserDataFromFirestore(userId)
        getUsernameFromFirestore(userId)

    }

    private fun uploadFoto(namaFile: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.displayName
        // Menginisialisasi Firebase Storage
        val storage = FirebaseStorage.getInstance()


// Mengompres bitmap menjadi format JPG dengan kualitas tertentu (misalnya, 80)
        val baos = ByteArrayOutputStream()
        val foto = findViewById<CircleImageView>(R.id.circleImageView)
        // Mendapatkan gambar dari CircleImageView sebagai Bitmap
        val bitmap = (foto.drawable as BitmapDrawable).bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val imageData = baos.toByteArray()

// Melakukan upload gambar ke Firebase Storage
        val storageRef = storage.reference.child(currentUser.toString()+"/images/$namaFile")
        // File dengan nama yang sama tidak ditemukan
        if (namaFile != ""){
            val uploadTask = storageRef.putBytes(imageData)
            uploadTask.addOnSuccessListener {
                // Upload berhasil, dapatkan URL gambar yang diunggah
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Lakukan sesuatu dengan URL gambar, misalnya, menyimpannya ke Firestore
                    saveImageUrlToFirestore(imageUrl)
                }.addOnFailureListener { exception ->
                    // Gagal mendapatkan URL gambar
                }
            }.addOnFailureListener { exception ->
                // Gagal upload gambar
            }
        }
    }

//    private fun uploadImage(){
//        val progressDialog = ProgressDialog(this)
//        progressDialog.setMessage("Mengupdate Data...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//
//        val storageRef = FirebaseStorage.getInstance().getReference("images/")
//        storageRef.putFile(ImageUri).addOnSuccessListener {
//            binding.circleImageView.setImageURI(ImageUri)
//            Toast.makeText(this, "Berhasil Mengganti Foto", Toast.LENGTH_SHORT).show()
//            if (progressDialog.isShowing) progressDialog.dismiss()
//        }.addOnFailureListener{
//            if (progressDialog.isShowing) progressDialog.dismiss()
//            Toast.makeText(this, "Gagal Mengganti Foto", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        userId?.let {
            val userRef = db.collection("users").document(userId)
            userRef.update("profilePicture", imageUrl)
                .addOnSuccessListener {
                    // URL gambar berhasil disimpan di Firestore
                }.addOnFailureListener { exception ->
                    // Gagal menyimpan URL gambar di Firestore
                }
        }
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

                    // Set nilai username ke dalam EditTexta
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

    private fun updateName(newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val userData = hashMapOf(
            "username" to newName
        )

        db.collection("users").document(userId)
            .update(userData as Map<String, Any>)
            .addOnSuccessListener {
                // Field 'name' berhasil diperbarui
            }
            .addOnFailureListener { exception ->
                // Gagal memperbarui field 'name'
                // Tambahkan penanganan jika terjadi kesalahan
            }
    }

    private fun openCamera() {
        // Mendapatkan timestamp untuk nama file
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Membuat nama file berdasarkan timestamp
        val imageFileName = "Image_$timeStamp.jpg"

        currentPhotoPath = imageFileName

        // Menyiapkan intent untuk memanggil aplikasi kamera
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Menambahkan extra data untuk memberikan nama file pada foto yang diambil
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileName)
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
        Log.i("Uri awal", data.toString())
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                val imageBitmap = data?.extras?.get("data") as Bitmap
                // Lakukan sesuatu dengan gambar yang diambil (imageBitmap) di sini
                imageBitmap?.let {
                    Glide.with(this@UbahActivity)
                        .load(imageBitmap)
                        .placeholder(R.drawable.account)
                        .error(R.drawable.account)
                        .into(findViewById(R.id.circleImageView))
                }
                Log.i("Uri dari galeri", imageBitmap.toString())
            } catch (e: Exception){
                var imageUri: Uri? = data?.data
                val bitmap: Bitmap? = imageUri?.let { contentResolver.openInputStream(it)?.use(BitmapFactory::decodeStream) }
//                imageUri?.let { uri ->
//                    // Load the image from URI using Glide
//                    Glide.with(this@UbahActivity)
//                        .load(uri)
//                        .placeholder(R.drawable.account)
//                        .error(R.drawable.account)
//                        .into(findViewById(R.id.circleImageView))

                bitmap?.let {
                    Glide.with(this@UbahActivity)
                        .load(bitmap)
                        .placeholder(R.drawable.account)
                        .error(R.drawable.account)
                        .into(findViewById(R.id.circleImageView))
                    currentPhotoPath = bitmap.toString()
                }
                Log.i("Uri dari galeri", imageUri.toString())
//                }

            }


        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageUri: Uri? = data?.data
//            imageUri?.let { uri ->
//                // Load the image from URI using Glide
//                Glide.with(this@UbahActivity)
//                    .load(uri)
//                    .placeholder(R.drawable.account)
//                    .error(R.drawable.account)
//                    .into(findViewById(R.id.circleImageView))
//            }
//        }
//    }

}