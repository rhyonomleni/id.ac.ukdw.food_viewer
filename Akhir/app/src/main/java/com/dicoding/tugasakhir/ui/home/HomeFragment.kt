package com.dicoding.tugasakhir.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.tugasakhir.DetailActivity
import com.dicoding.tugasakhir.ListRestaurantAdapter
import com.dicoding.tugasakhir.R
import com.dicoding.tugasakhir.Results
import com.dicoding.tugasakhir.databinding.FragmentHomeBinding
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var listRestaurantAdapter: ListRestaurantAdapter
    private var originalRestaurantList = mutableListOf<Results>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Set up RecyclerView
        binding.idRestaurant.layoutManager = LinearLayoutManager(requireContext())
        binding.idRestaurant.setHasFixedSize(true)

        // Initialize data from strings.xml
        val restaurantNames = resources.getStringArray(R.array.data_restaurant_name)
        val restaurantAddresses = resources.getStringArray(R.array.data_alamat_restaurant)
        val restaurantDescriptions = resources.getStringArray(R.array.data_restaurant_description)
        val restaurantOpeningHours = resources.getStringArray(R.array.data_alamat_jamBuka)
        val restaurantContacts = resources.getStringArray(R.array.data_noTelp_restaurant)
        val restaurantPhotos = resources.obtainTypedArray(R.array.data_photo)

        for (i in restaurantNames.indices) {
            originalRestaurantList.add(
                Results(
                    name = restaurantNames[i],
                    address = restaurantAddresses[i],
                    description = restaurantDescriptions[i],
                    openingHours = restaurantOpeningHours[i],
                    contact = restaurantContacts[i],
                    photo = restaurantPhotos.getResourceId(i, -1)
                )
            )
        }

        // Set up Adapter
        listRestaurantAdapter = ListRestaurantAdapter { result ->
            val intentToDetail = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("restaurant", result)
            }
            startActivity(intentToDetail)
        }
        binding.idRestaurant.adapter = listRestaurantAdapter
        listRestaurantAdapter.submitList(originalRestaurantList)

        // Set up SearchView
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            binding.searchView.hide()
            filterRestaurants(query)
            true
        }

        return binding.root
    }

    private fun filterRestaurants(query: String) {
        val filteredList = originalRestaurantList.filter { restaurant ->
            restaurant.name.contains(query, ignoreCase = true) ||
                    restaurant.address.contains(query, ignoreCase = true) ||
                    restaurant.description.contains(query, ignoreCase = true)
        }
        listRestaurantAdapter.submitList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
