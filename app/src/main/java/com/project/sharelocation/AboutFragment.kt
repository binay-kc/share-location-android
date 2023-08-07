package com.project.sharelocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.sharelocation.databinding.FragmentAboutBinding

class AboutFragment: Fragment() {

    private lateinit var mBinding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        mBinding = FragmentAboutBinding.inflate(inflater, container, false)
        return mBinding.root
    }
}