package com.dicoding.tugasakhir

import android.app.Activity
import android.app.appsearch.exceptions.AppSearchException
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.tugasakhir.ui.home.HomeFragment
import com.dicoding.tugasakhir.ui.profile.ProfileFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var login: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    companion object {
        private const val RC_SIGN_IN = 1001
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, redirect to another activity (e.g., HomeActivity)
            redirectToHomeScreen()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        val login = findViewById<Button>(R.id.login) // Memanggil findViewById di sini setelah setContentView

        login.setOnClickListener{
            signIn()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            val intent = googleSignInClient.signInIntent
//            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun redirectToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }



    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if (result.resultCode == RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                val user = auth.currentUser
                if (user != null) {
                    val email = user.email // Mendapatkan email pengguna
                    val googleId = user.uid // Mendapatkan Google ID pengguna
                    val username = user.displayName
                    val profilePictureUrl =
                        user.photoUrl?.toString() // Mendapatkan URL gambar profil pengguna

                    // Mengakses Firestore
                    val db = FirebaseFirestore.getInstance()

                    // Membuat objek untuk disimpan di Firestore
                    val userMap = hashMapOf(
                        "email" to email,
                        "googleId" to googleId,
                        "profilePicture" to profilePictureUrl,
                        "username" to username
                    )

                    // Menyimpan data ke Firestore
                    db.collection("users").document(user.uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                            // Tambahkan logika atau tindakan lain yang perlu dilakukan setelah berhasil menyimpan ke Firestore
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error writing document", e)
                            // Tambahkan penanganan kesalahan jika gagal menyimpan ke Firestore
                        }
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                e.printStackTrace()
                Toast.makeText(applicationContext, e.localizedMessage, LENGTH_SHORT).show()
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener{ error ->
                Toast.makeText(applicationContext, error.localizedMessage, LENGTH_SHORT).show()
            }
    }

//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, check if user email matches expected email
//                    val user = auth.currentUser
//                    if (user != null) {
//                        val expectedEmail = "example@example.com" // Ganti dengan email yang diharapkan
//                        if (user.email == expectedEmail) {
//                            // Email sesuai, lanjutkan ke halaman utama
//                            redirectToHomeScreen()
//                        } else {
//                            // Email tidak sesuai, sign out pengguna
//                            signOutAndRedirectToSignIn()
//                        }
//                    }
//                } else {
//                    // Sign in failed, handle error
//                    // ...
//                }
//            }
//    }


}
