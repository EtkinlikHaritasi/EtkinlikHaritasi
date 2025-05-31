package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.*


object LocationUtils
{
    var lastKnownLocation: Location? = null

    //FusedLocationProvider oluşturmak için yaptım gerek olmayabilir
    fun getFusedLocationProviderClient(context: Context): FusedLocationProviderClient
    {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    //Konum bilgilerine erişim izinkerini kontrol edip ona göre izin isteyen fonksiyon
    fun checkLocationPermission(activity: Activity)
    {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
        }
    }

    //Yeni konum verisi oluşturan fonksiyon
    fun startContinuousLocationUpdates(
        context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L // 5 saniyede bir konum güncellemesi
        ).apply {
            setMinUpdateIntervalMillis(5000L) // Minimum 5 saniye arayla
            //setMaxUpdates(LocationRequest.UNLIMITED)
        }.build()

        val locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult)
            {
                val location = locationResult.lastLocation
                if (location != null)
                {

                    lastKnownLocation = location;
                    Log.d("Konum", "Sürekli konum: ${location.latitude}, ${location.longitude}")
                }
                else
                {
                    Log.d("Konum", "Konum alınamadı (null)")
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
        {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }


    fun isLocationEnabled(context: Context): Boolean
    {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showLocationServicePrompt(context: Context)
    {
        if (!isLocationEnabled(context)) {
            Toast.makeText(context, "Konum servislerini açmanız gerekiyor!", Toast.LENGTH_LONG).show()
        }
    }
}
