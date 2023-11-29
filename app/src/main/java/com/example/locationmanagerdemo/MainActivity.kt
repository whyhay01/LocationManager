package com.example.locationmanagerdemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private var currentLocation: Location? = null

    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    /**
     * This is used for getting periodic location update from location Manager
     * */
    private val locationListener = LocationListener { location ->
        currentLocation = location
        Log.d(
            "MainActivity",
            "Longitude:${currentLocation?.longitude} \n Latitude: ${currentLocation?.latitude} "
        )
    }

    /***
     *  This is used for getting periodic location update from google play service location API
     * */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult != null) {
                super.onLocationResult(locationResult)
            }

            locationResult?.let {
                for (location in locationResult.locations) {
                    currentLocation = location
                    Log.d(
                        "GoogleLocationApiResult",
                        "Longitude: ${location.longitude} \n Latitude: ${location.latitude}"
                    )
                }
            }
        }
    }

    private val locationRequest = LocationRequest().apply {
        interval = LOCATION_UPDATE_INTERVAL
        fastestInterval = LOCATION_FASTEST_UPDATE_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getDeviceLocationWithLocationManager()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Log.d(
            "MainActivity Oncreate",
            "Longitude:${currentLocation?.longitude} \n Latitude: ${currentLocation?.latitude} "
        )

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_FINE_LOCATION_ACCESS
            )
        }
    }

    private fun startGooglePeriodicLocationUpdates() {
        checkLocationPermission()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopGooglePeriodicLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun getDeviceLocationWithLocationManager() {
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        checkLocationPermission()
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_UPDATE_INTERVAL,
                10_000F,
                locationListener
            )
        } else {
            enableLocationProvider()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (
            requestCode == REQUEST_FINE_LOCATION_ACCESS
            && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getDeviceLocationWithLocationManager()
            startGooglePeriodicLocationUpdates()
        }
    }

    private fun enableLocationProvider() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun stopPeriodicLocationUpdate() {
        locationManager.removeUpdates(locationListener)
    }

    override fun onResume() {
        super.onResume()
        startGooglePeriodicLocationUpdates()
        getDeviceLocationWithLocationManager()
    }
    override fun onDestroy() {
        super.onDestroy()
        stopPeriodicLocationUpdate()
        stopGooglePeriodicLocationUpdates()
    }

    companion object {
        const val REQUEST_FINE_LOCATION_ACCESS = 1

        private const val LOCATION_UPDATE_INTERVAL = 20_000L

        private const val LOCATION_FASTEST_UPDATE_INTERVAL = 10_000L

    }
}
