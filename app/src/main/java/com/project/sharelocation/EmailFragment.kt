package com.project.sharelocation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.sharelocation.databinding.FragmentEmailBinding
import com.project.sharelocation.viewModel.AddressViewModel

class EmailFragment: Fragment() {

    private lateinit var mBinding: FragmentEmailBinding

    private  var address : String = ""
    private  var latitude  : Double = -1.0
    private  var longitude : Double = -1.0

    private lateinit var viewModel : AddressViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEmailBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (ViewModelProvider(requireActivity())[AddressViewModel::class.java])

        viewModel.currentAddress.observe(viewLifecycleOwner) {
            address = it
        }
        viewModel.currentLatLng.observe(viewLifecycleOwner){
            latitude = it.latitude
            longitude = it.longitude
        }

        mBinding.btnSend.setOnClickListener {
           if(isValid()){
                val addressTxt = "Address: $address\nLatitude: $latitude \nLongitude: $longitude"
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_SUBJECT, "My current address")
                            putExtra(Intent.EXTRA_TEXT, addressTxt)
                        }
               if (context?.packageManager?.let { it1 -> intent.resolveActivity(it1) } != null) {
                   startActivity(intent)
               }
            }
        }

    }

    private fun isValid():Boolean{
        if(mBinding.etEmail.text.isNullOrEmpty()){
            Toast.makeText(context,"Email cannot be empty!!",Toast.LENGTH_SHORT).show()
            return false
        }
        if(address.isEmpty() || latitude == -1.0 || longitude == -1.0){
            Toast.makeText(context,"Current Location isn't fetched. Please try again later", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}