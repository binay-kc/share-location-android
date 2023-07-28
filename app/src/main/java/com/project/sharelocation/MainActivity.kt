package com.project.sharelocation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openFragment(MapsFragment())
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
                openFragment(PlacesFragment())
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