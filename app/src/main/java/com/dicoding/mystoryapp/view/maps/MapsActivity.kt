package com.dicoding.mystoryapp.view.maps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityMapsBinding
import com.dicoding.mystoryapp.util.Status
import com.dicoding.mystoryapp.view.ViewModelFactory
import com.dicoding.mystoryapp.view.welcome.WelcomeActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        mapsViewModel = getViewModel(this)
        setContentView(binding.root)

        setAction(savedInstanceState)
    }

    private fun setAction(savedInstanceState: Bundle?) {

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = getString(R.string.menu_maps)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        if (savedInstanceState === null) {
            mapsViewModel.getSession()
        }

        mapsViewModel.token.observe(this) { token ->
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
                return@observe
            }
            mapsViewModel.getStoryWithLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val bali = LatLng(-8.409518, 115.188919)
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bali, 7f))

        mapsViewModel.location.observe(this) { status ->
            when(status) {
                is Status.Failure -> {
                    if (!isFinishing) {
                        showDialog(status.throwable ?: resources.getString(R.string.maps_fail))
                    }
                }
                is Status.Loading -> showLoading(status.state)
                is Status.Success -> {
                    status.data.listStory?.forEach { data ->
                        mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(data?.lat ?: 0.0, data?.lon ?: 0.0))
                                .title(data?.name)
                                .snippet(data?.description)
                        )
                    }
                }
            }
        }
    }

    private fun getViewModel(context: Context): MapsViewModel {
        val factory = ViewModelFactory.getInstance(context)
        return ViewModelProvider(this, factory)[MapsViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps_normal -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }
            R.id.maps_satellite -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return true
            }
            R.id.maps_terrain -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                return true
            }
            R.id.maps_hybrid -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        mapFragment.view?.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Alert")
            setMessage(message)
            setPositiveButton("OK") {_, _ ->
                val intent = Intent(context, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
}