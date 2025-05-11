package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
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

    //Önceki konum verisi çeken fonksiyon
    fun fetchLocation(context: Context, fusedLocationProviderClient: FusedLocationProviderClient) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            Toast.makeText(context, "İzin yok", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null)
            {
                Log.d("Konum", "${location.latitude}, ${location.longitude}")
                Toast.makeText(context, "${location.latitude} ${location.longitude}", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Log.d("Konum", "lastLocation null döndü, requestLocationUpdates çağrılıyor...")
                requestNewLocationData(context, fusedLocationProviderClient)
            }
        }.addOnFailureListener {
            Log.e("Konum", "lastLocation başarısız: ${it.message}")
        }
    }

    //Yeni konum verisi oluşturan fonksiyon
    private fun requestNewLocationData(context: Context, fusedLocationProviderClient: FusedLocationProviderClient)
    {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, // accuracy
            5000L // intervalMillis
        ).apply {
            setWaitForAccurateLocation(true)
            setMinUpdateIntervalMillis(2000L)
            setMaxUpdates(1)
        }.build()

        val locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult)
            {
                val location = locationResult.lastLocation
                if (location != null)
                {
                    Log.d("Konum", "Yeni konum: ${location.latitude}, ${location.longitude}")
                    Toast.makeText(context, "Yeni: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Log.d("Konum", "requestLocationUpdates ile de konum alınamadı")
                    Toast.makeText(context, "Konum alınamadı", Toast.LENGTH_SHORT).show()
                }

                fusedLocationProviderClient.removeLocationUpdates(this)
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
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
