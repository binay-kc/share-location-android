package com.project.sharelocation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class AddressViewModel : ViewModel() {

    private val mutableCurrentAddress = MutableLiveData<String>()
    val currentAddress : LiveData<String> get() =  mutableCurrentAddress

    private val mutableCurrentLatLng = MutableLiveData<LatLng>()
    val currentLatLng : LiveData<LatLng> get() =  mutableCurrentLatLng

    fun setCurrentAddress(address: String) {
        mutableCurrentAddress.value = address
    }

    fun setCurrentLatLng(latLng: LatLng) {
        mutableCurrentLatLng.value = latLng
    }



}