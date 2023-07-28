package com.project.sharelocation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.sharelocation.databinding.FragmentPlacesBinding

class PlacesFragment: Fragment() {

    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var binding: FragmentPlacesBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            latitude = it.getDouble("lat", 0.0)
            longitude = it.getDouble("long", 0.0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("place", "onCreateView: ")

        binding = FragmentPlacesBinding.inflate(inflater, container, false)

        return binding.root
    }

}