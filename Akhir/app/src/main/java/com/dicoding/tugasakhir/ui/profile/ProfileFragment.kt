package com.dicoding.tugasakhir.ui.profile

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.tugasakhir.LoginActivity
import com.dicoding.tugasakhir.R
import com.dicoding.tugasakhir.UbahActivity
import com.dicoding.tugasakhir.databinding.FragmentProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var userId: String

    private lateinit var auth : FirebaseAuth

    private var _binding: FragmentProfileBinding? = null

    private lateinit var usernameText: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Dokumen ditemukan, ambil data username dari Firestore
                    // Inflate the layout for this fragment
                    val view = inflater.inflate(R.layout.fragment_profile, container, false)

                    val profilePicture = document.getString("username")

                    // Dapatkan referensi ke TextView dengan ID "username" dari layout fragment
                    usernameText = view.findViewById<TextView>(R.id.username)

                    // Contoh penggunaan setText() untuk mengatur teks pada TextView
                    usernameText.setText(profilePicture)
                    if (profilePicture != null) {
                        Log.d(ContentValues.TAG, profilePicture)
                    }
                    Log.d(ContentValues.TAG, "Hahahahahaha")
                } else {
                    // Dokumen tidak ditemukan atau kosong
                    // Tambahkan penanganan jika dokumen tidak ditemukan
                }
            }
        getUserDataFromFirestore(userId)
        val db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        val buttonLogout: Button = binding.btnLogout
        buttonLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            // Hapus token atau data lokal terkait sign-in
            // Misalnya:
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
            googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        val buttonUbahProfile: Button = binding.btnUbahProfile
        buttonUbahProfile.setOnClickListener{
            val intent = Intent(requireContext(), UbahActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun getUserDataFromFirestore(
        userId: String,
    ) {
        // Akses koleksi 'users' di Firestore dan ambil dokumen berdasarkan userID
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Dokumen ditemukan, ambil data dari Firestore
                    val profilePicture = document.getString("profilePicture")

                    // Tampilkan data di UI (contoh: TextView di layout fragment_profile.xml)
                    profilePicture?.let {
                        view?.let { it1 ->
                            Glide.with(requireContext())
                                .load(profilePicture)
                                .placeholder(androidx.appcompat.R.drawable.abc_btn_default_mtrl_shape)
                                .error(R.drawable.account)
                                .into(it1.findViewById(R.id.circleImageView))
                        }
                    }
                    // Load gambar profil menggunakan library seperti Glide atau Picasso
                    // Contoh: Glide.with(requireContext()).load(profilePictureUrl).into(profileImageView)
                } else {
                    // Dokumen tidak ditemukan
                    // Tambahkan penanganan jika dokumen tidak ditemukan
                }
            }
            .addOnFailureListener { exception ->
                // Gagal mengambil data dari Firestore
                // Tambahkan penanganan jika terjadi kesalahan
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}