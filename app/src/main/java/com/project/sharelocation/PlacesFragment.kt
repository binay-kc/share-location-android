package com.project.sharelocation


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.project.sharelocation.databinding.FragmentPlacesBinding

class PlacesFragment: Fragment() {

    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var binding: FragmentPlacesBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var placesAdapter: PlacesAdapter

    companion object {
        fun newInstance(latitude: Double, longitude: Double): PlacesFragment {
            val fragment = PlacesFragment()
            val args = Bundle()
            args.putDouble("lat", latitude)
            args.putDouble("long", longitude)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.let{
            latitude = it.getDouble("lat", 0.0)
            longitude = it.getDouble("long", 0.0)
        }

        binding = FragmentPlacesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        placesAdapter = PlacesAdapter()
        binding.recyclerView.adapter = placesAdapter

        Places.initialize(requireContext(), "AIzaSyBboJrU8Ni25rBa5nzlQXs5XGx56w3PBZA")
        placesClient = Places.createClient(requireContext())

        fetchNearbyPlaces()
    }

    private fun fetchNearbyPlaces() {
        val queries = listOf("establishment", "college", "cafe", "restaurant", "park", "bank", "hospital", "grocery", "gym")

        val placesList = mutableListOf<Place>()
        for (query in queries) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setLocationRestriction(RectangularBounds.newInstance(
                    LatLng(latitude - 0.1, longitude - 0.1),
                    LatLng(latitude + 0.1, longitude + 0.1)
                ))
                .build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions

                val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

                for (prediction in predictions) {
                    val placeId = prediction.placeId
                    val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()

                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener { fetchPlaceResponse ->
                        val place = fetchPlaceResponse.place
                        // Add the place to the list if it's not already present
                        if (!placesList.contains(place)) {
                            placesList.add(place)
                        }

                        // Check if we have processed all the predicted places

                            // Update the RecyclerView with the list of landmarks
                            placesAdapter.setPlaces(placesList)

                    }.addOnFailureListener { exception ->
                        Log.e("Error", "fetchNearbyPlaces: " +exception.message )
                        // Handle the failure to fetch place details
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle the error if the search fails
                Log.e("Error",""+exception.message)
            }
        }
    }

}