package com.dicoding.tugasakhir.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.tugasakhir.DetailActivity
import com.dicoding.tugasakhir.ListRestaurantAdapter
import com.dicoding.tugasakhir.R
import com.dicoding.tugasakhir.Restaurant
import com.dicoding.tugasakhir.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvAnime: RecyclerView
    private val list = ArrayList<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root = binding.root

        rvAnime = binding.idRestaurant
        rvAnime.setHasFixedSize(true)

        list.addAll(getListAnime())
        showRecyclerList()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getListAnime(): ArrayList<Restaurant> {
        val dataName = resources.getStringArray(R.array.data_restaurant_name)
        val dataDescription = resources.getStringArray(R.array.data_restaurant_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val dataAlamat = resources.getStringArray(R.array.data_alamat_restaurant)
        val dataNoTelp = resources.getStringArray(R.array.data_noTelp_restaurant)
        val dataJamBuka = resources.getStringArray(R.array.data_alamat_jamBuka)
        val listRestaurant = ArrayList<Restaurant>()
        for (i in dataName.indices){
            val restaurant = Restaurant(dataName[i], dataDescription[i], dataPhoto.getResourceId(i,-1), dataAlamat[i], dataNoTelp[i].toString(), dataJamBuka[i])
            listRestaurant.add(restaurant)
        }
        return listRestaurant
    }

    private fun showRecyclerList() {
        rvAnime.layoutManager = LinearLayoutManager(requireContext())
        val listRestaurantAdapter = ListRestaurantAdapter(list, onClick = {})
        rvAnime.adapter = listRestaurantAdapter

        listRestaurantAdapter.setOnItemClickCallback(object : ListRestaurantAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Restaurant) {
                val intentToDetail = Intent(requireContext(), DetailActivity::class.java)
                intentToDetail.putExtra("anime", data)
                startActivity(intentToDetail)
            }
        })
    }
}
