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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.locationmanagerdemo.location.FallbackLocationTracker
import com.example.locationmanagerdemo.location.LocationTracker

class MainActivity : AppCompatActivity(), LocationListener {

    private var currentLocation: Location? = null

    private lateinit var locationManager: LocationManager
//    private lateinit var fusedLocationClient: FusedLocationProviderClient


    /**
     * This is used for getting periodic location update from location Manager
     * */
//   private val locationListener: LocationListener = object : LocationListener{
//        override fun onLocationChanged(location: Location) {
//
//        }
//
//    }

    /***
     *  This is used for getting periodic location update from google play service location API
     * */
//    private val locationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            super.onLocationResult(locationResult)
//            locationResult?.let {
//                for (location in locationResult.locations) {
//                    currentLocation = location
//                    Log.d(
//                        "GoogleLocationApiResult",
//                        "Longitude: ${location.longitude} \n Latitude: ${location.latitude}"
//                    )
//                }
//            }
//        }
//    }

//    private val locationRequest = LocationRequest().apply {
//        interval = LOCATION_UPDATE_INTERVAL
//        fastestInterval = LOCATION_FASTEST_UPDATE_INTERVAL
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getLocationWithLocationManager()

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        val tracker = FallbackLocationTracker(this)
//        tracker.start(object : LocationTracker.LocationUpdateListener {
//            override fun onUpdate(
//                oldLoc: Location?,
//                oldTime: Long,
//                newLoc: Location?,
//                newTime: Long
//            ) {
//                if (newLoc == null) {
//                    if (oldLoc != null) {
//                        //youLocationVariable = oldLoc
//                    }
//                } else {
//                    newLoc?.latitude
//                    Toast.makeText(this@MainActivity, "Lat : " + newLoc?.latitude + " Lng : " + newLoc?.longitude, Toast.LENGTH_LONG).show();
//                }
//            }
//        })


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

//    private fun startGooglePeriodicLocationUpdates() {
//        checkLocationPermission()
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//    }

//    private fun stopGooglePeriodicLocationUpdates() {
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }


//    private fun getDeviceLocationWithLocationManager() {
//        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        checkLocationPermission()
//        if (hasGps || hasNetwork) {
//            if (hasGps) {
//                locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    LOCATION_UPDATE_INTERVAL,
//                    0F,
//                    locationListener
//                )
//                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            } else if (hasNetwork) {
//                locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER,
//                    LOCATION_UPDATE_INTERVAL,
//                    0F,
//                    locationListener
//                )
//                currentLocation =
//                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                Log.d(
//                    "getDeviceLocationWithLocationManagerWithNetwork",
//                    "Longitude: ${currentLocation?.longitude} \n Latitude: ${currentLocation?.latitude} "
//                )
//            } else {
//                enableLocationProvider()
//            }
//        }
//
//    }


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
            getLocationWithLocationManager()
//            startGooglePeriodicLocationUpdates()
        }
    }

    private fun enableLocationProvider() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun stopPeriodicLocationUpdate() {
        locationManager.removeUpdates(this)
    }

    override fun onResume() {
        super.onResume()
//        startGooglePeriodicLocationUpdates()
        getLocationWithLocationManager()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPeriodicLocationUpdate()
//        stopGooglePeriodicLocationUpdates()
    }

    //Location Manager Implementation
    private fun getLocationWithLocationManager() {

        if (::locationManager.isInitialized) {
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            checkLocationPermission()

            if (isGpsEnabled || isNetworkEnabled) {
                if (isGpsEnabled) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_INTERVAL,
                        0F,
                        this
                    )
                    currentLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                } else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_INTERVAL,
                        0F,
                        this
                    )
                    currentLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            }

        }

    }

    companion object {
        const val REQUEST_FINE_LOCATION_ACCESS = 1

        const val LOCATION_UPDATE_INTERVAL = 20_000L

        const val LOCATION_FASTEST_UPDATE_INTERVAL = 10_000L

    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        Log.d("MainActivity onLocationChanged", "Longitude: ${location.longitude} \n Latitude: ${location.latitude}")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        super.onStatusChanged(provider, status, extras)

    }
}
