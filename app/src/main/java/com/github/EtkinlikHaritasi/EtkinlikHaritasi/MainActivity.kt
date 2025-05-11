package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import QRCodeScanner
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.EtkinlikHaritasiTheme
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtkinlikHaritasiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EtkinlikHaritasiTheme {
        MainScreen()
    }
}
