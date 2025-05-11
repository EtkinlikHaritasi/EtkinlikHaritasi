package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import QRCodeScanner
import androidx.activity.result.contract.ActivityResultContracts

class Bilet
{
    val açıkken_simge = Icons.Filled.LocalActivity
    val kapalıyken_simge = Icons.Outlined.LocalActivity
    val başlık = "Bilet"

    @Composable
    fun İçerik(modifier: Modifier)
    {
        MainScreen(modifier = modifier)
    }

    @OptIn(ExperimentalGetImage::class)
    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var showQrCodeScanner by remember { mutableStateOf(false) }
        var qrCodeResult by remember { mutableStateOf<String?>(null) }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showQrCodeScanner = true
            } else {
            }
        }

        Column(modifier = modifier.padding(16.dp)) {
            Button(onClick = {
                val permissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (permissionGranted) {
                    showQrCodeScanner = true
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("QR Kodunu Tarayın")
            }

            if (showQrCodeScanner) {
                QRCodeScanner(onQrCodeScanned = { result ->
                    qrCodeResult = result
                    showQrCodeScanner = false
                })
            }

            qrCodeResult?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Tarama Sonucu: $it")
            }
        }
    }
}