package com.project.sharelocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.sharelocation.databinding.FragmentMapsBinding
import com.project.sharelocation.viewModel.AddressViewModel
import java.io.IOException
import java.util.Locale

class MapsFragment: Fragment(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private var locationManager: LocationManager? = null
    private var geocoder: Geocoder? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var binding: FragmentMapsBinding

    private lateinit var viewModel : AddressViewModel

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        viewModel = (ViewModelProvider(requireActivity())[AddressViewModel::class.java])

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (isLocationPermissionGranted()) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            fetchCurrentLocation()
        } else {
            // Request location permissions
            requestLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        if (isLocationPermissionGranted()) {
            // Use FusedLocationProviderClient to get the last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationChanged(location)
                } ?: run {
                    // Last known location is not available, request location updates
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000L,
                        10f,
                        this
                    )
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        locationManager?.removeUpdates(this)

        // Get latitude and longitude from the received location
        val latitude = location.latitude
        val longitude = location.longitude

        val intent = Intent("location-data")
        intent.putExtra("lat", latitude)
        intent.putExtra("long", longitude)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        // Place a marker at the current location
        val currentLocation = LatLng(latitude, longitude)
        val address = getAddressFromLatLng(latitude, longitude)

        val markerOptions = MarkerOptions()
            .position(currentLocation)
            .title("$address Lat: $latitude Lng: $longitude")
        mMap.addMarker(markerOptions)

        // Animate the camera to the current location and set an appropriate zoom level
        val cameraPosition = CameraPosition.Builder()
            .target(currentLocation)
            .zoom(15f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null)

        viewModel.setCurrentAddress(address)
        viewModel.setCurrentLatLng(currentLocation)
    }

    //get the address from the latitude and longitude
    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        return try {
            val addresses: List<Address> = geocoder?.getFromLocation(latitude, longitude, 1) ?: emptyList()
            if (addresses.isNotEmpty()) {
                val address = addresses[0]

                // Extract the street name (thoroughfare) from the Address object
                val streetName = address.thoroughfare ?: ""

                // Handle the case where thoroughfare is not available (e.g., rural areas)
                streetName.ifEmpty {
                    ""
                }
            } else {
                ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permissions granted, enable location tracking and request location updates
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
                fetchCurrentLocation()
            } else {
                // Location permissions denied, handle this case if needed
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup: Remove location updates if the fragment is destroyed
        locationManager?.removeUpdates(this)
    }
}