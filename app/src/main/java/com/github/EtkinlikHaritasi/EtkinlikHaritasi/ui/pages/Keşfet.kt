package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.*

class Keşfet
{
    val açıkken_simge = Icons.Filled.Map
    val kapalıyken_simge = Icons.Outlined.Map
    val başlık = "Keşfet"

    @Composable
    fun İçerik(modifier: Modifier = Modifier)
    {
        val context = LocalContext.current
        val fusedLocationProviderClient = remember {
            LocationUtils.getFusedLocationProviderClient(context)
        }

        Box(
            modifier = modifier
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties(isMyLocationEnabled = true),
                onMyLocationButtonClick = { konumum_düğmesi(context, fusedLocationProviderClient) }
            )
        }
    }

    fun konumum_düğmesi(context: Context,
                        fusedLocationProviderClient: FusedLocationProviderClient):Boolean
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
        return false
    }
}