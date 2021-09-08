package com.shakib_mansoori.Activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.shakib_mansoori.Activities.databinding.ActivityMainBinding
import com.shakib_mansoori.models.Current
import com.shakib_mansoori.util.CheckInternet

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var checkInternet: CheckInternet
    private val PERMISSION_ID = 101
    private var latT: Double = 0.0
    private var longT: Double = 0.0
    private lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress)
        dialog.setCancelable(false)
        dialog.show()


        checkInternet = CheckInternet()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        init()

    }

    private fun init() {
        if (!checkInternet.isConected(this)) {
            showCustomDialog()
        } else {
            getLastLocation()
            viewModel.getCurrentWeaterData().observe(this, Observer {
                if (it != null) {

                    dialog.hide()
                    setValues(it)

                }
            })
        }

    }


    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Please connect to the internet to proceed further")
            .setCancelable(false)
            .setPositiveButton(
                "Connect"
            ) { dialog, which -> startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
            .setNegativeButton("Cancel") { dialog, which ->

                dialog.dismiss()
                init()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setValues(current: Current) {

        var tempVal = (current.temp - 273.15).toInt()

        var temp: String = tempVal.toString() + "\u2103"
        var feel_like: String =
            "Feels like " + (current.feels_like - 273.15).toInt().toString() + "\u2103"

        val bed = (tempVal - 2).toString() + "\u2103"
        val meet = (tempVal - 3).toString() + "\u2103"

        binding.bedroomTv.text = bed
        binding.meetingRoomTv.text = meet

        binding.mainTemTv.text = temp
        binding.mainTemFeel.text = feel_like

        binding.currentDesTv.text = current.weather[0].description

        binding.longTv.text = longT.toInt().toString()
        binding.latitudeTv.text = latT.toInt().toString()

        viewModel.getCityName().observe(this, {
            if (it != null) {
                binding.cityTv.text = it
            }
        })

    }


    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener { p0 ->
                    val location = p0.getResult();
                    if (location == null)
                        requestNewLocationData()
                    else {

                        latT = location.getLatitude()
                        longT = location.getLongitude()

                        viewModel.fetchWeatherData(
                            latT, longT
                        );
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5
            fastestInterval = 0
            numUpdates = 1
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission())
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )

    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation

            Log.d(
                "TAGAGTAG",
                "onComplete: " + mLastLocation.getLatitude() + mLastLocation.getLongitude()
            )
            viewModel.fetchWeatherData(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation()
        }
    }

    override fun onResume() {
        super.onResume()

        if (!checkInternet.isConected(this)) {
            showCustomDialog()
        } else {
            if (checkPermission())
                getLastLocation()
        }


    }
}