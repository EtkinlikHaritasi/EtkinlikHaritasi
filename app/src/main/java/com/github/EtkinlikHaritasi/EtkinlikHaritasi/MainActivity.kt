package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

class MainActivity : ComponentActivity()
{
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?)
    {
        fusedLocationProviderClient = LocationUtils.getFusedLocationProviderClient(this)

        LocationUtils.checkLocationPermission(this)

        super.onCreate(savedInstanceState)

        setContent{
            MaterialTheme{
                Surface(color = MaterialTheme.colorScheme.background) {
                    MyButton()
                }
            }
        }
    }
}

@Composable
fun MyButton() {
    val context = LocalContext.current
    val fusedLocationProviderClient = remember {
        LocationUtils.getFusedLocationProviderClient(context)
    }

    Button(onClick =
    {
        if (!LocationUtils.isLocationEnabled(context))
        {
            LocationUtils.showLocationServicePrompt(context)
        }
        else
        {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
            {
                LocationUtils.fetchLocation(context, fusedLocationProviderClient)
            }
            else
            {
                Toast.makeText(context, "Konum izni verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    })
    {
        Text("Konum Al")
    }
}

