package com.project.sharelocation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place

class PlacesAdapter: RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {

    private val placesList: MutableList<Place> = mutableListOf()

    fun setPlaces(newList: List<Place>) {
        placesList.clear()
        placesList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placesList[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPlaceName: TextView = itemView.findViewById(R.id.textViewPlaceName)
        private val textViewPlaceAddress: TextView = itemView.findViewById(R.id.textViewPlaceAddress)

        fun bind(place: Place) {
            textViewPlaceName.text = place.name
            textViewPlaceAddress.text = place.address
        }
    }
}