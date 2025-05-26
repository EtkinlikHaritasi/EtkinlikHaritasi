package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import com.github.EtkinlikHaritasi.EtkinlikHaritasi.NFCUtils
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.*
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
import android.app.Activity
import android.text.Layout
import androidx.activity.compose.LocalActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.theme.Typography

class Bilet
{
    val açıkken_simge = Icons.Filled.LocalActivity
    val kapalıyken_simge = Icons.Outlined.LocalActivity
    val başlık = "Bilet"

    @ExperimentalGetImage
    @Composable
    fun İçerik(modifier: Modifier)
    {
        if (LocalActivity.current != null)
        {
            var activity = LocalActivity.current as Activity
            LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
                NFCUtils.initialize(activity)
            }

            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                NFCUtils.enableForegroundDispatch(activity)
            }

            LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
                NFCUtils.disableForegroundDispatch(activity)
            }

        }

        var context = LocalContext.current
        var showQrCodeScanner by remember { mutableStateOf(false) }
        var qrCodeResult by remember { mutableStateOf<String?>(null) }

        Column(modifier = modifier)
        {
            Column(modifier = Modifier.fillMaxHeight(0.7f).fillMaxWidth())
            {
                if (!showQrCodeScanner)
                {
                    Icon(
                        Icons.Outlined.Contactless,
                        "NFC ile bilet okuma",
                        modifier = Modifier.fillMaxSize(0.5f).align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "NFC ile bilet kontrolü",
                        style = Typography.headlineLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else
                {
                    Box(modifier = Modifier.fillMaxSize(0.9f).clip(RoundedCornerShape(5))
                        .align(Alignment.CenterHorizontally))
                    {
                        QRCodeScanner { result ->
                            qrCodeResult = result
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth())
            {
                FilledTonalButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.Center)
                )
                {
                    Icon(
                        Icons.Outlined.QrCodeScanner,
                        "QR kod okuyucu",
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                    Text(
                        text = "QR bilet oku",
                        style = Typography.bodyLarge,
                        modifier = Modifier
                    )
                }
            }
            //MainScreen(modifier = modifier)
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
}