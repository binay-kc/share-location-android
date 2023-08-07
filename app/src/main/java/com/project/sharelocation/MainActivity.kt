package com.project.sharelocation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.project.sharelocation.viewModel.AddressViewModel


class MainActivity : AppCompatActivity() {

    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var viewModel : AddressViewModel


    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            latitude = intent.getDoubleExtra("lat", 0.0)
            longitude = intent.getDoubleExtra("long", 0.0)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
            IntentFilter("location-data")
        )

        supportActionBar?.show()
        openFragment(MapsFragment())
        viewModel = (ViewModelProvider(this)[AddressViewModel::class.java])

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.map -> {
                openFragment(MapsFragment())
                true
            }
            R.id.places -> {
                val placesFragment = PlacesFragment.newInstance(latitude, longitude)
                supportFragmentManager.beginTransaction().replace(R.id.fragment, placesFragment).commit()
                true
            }
            R.id.email -> {
                openFragment(EmailFragment())
                true
            }
            R.id.about -> {
                openFragment(AboutFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
    }
}