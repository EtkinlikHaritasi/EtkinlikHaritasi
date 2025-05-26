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
import android.R
import android.app.Activity
import android.content.Context
import android.text.Layout
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.CornerRadius
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
    fun İçerik(modifier: Modifier) {
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


            var context = LocalContext.current
            var QRScannerOn = remember { mutableStateOf(false) }
            var qrCodeResult by remember { mutableStateOf<String?>(null) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {}

            Column(modifier = modifier)
            {

                Column(modifier = Modifier.fillMaxHeight(0.7f).fillMaxWidth())
                {
                    Box(
                        modifier = Modifier.fillMaxSize(0.9f).clip(RoundedCornerShape(16.dp))
                            .align(Alignment.CenterHorizontally)
                    )
                    {
                        if (!QRScannerOn.value)
                        {
                            Icon(
                                Icons.Outlined.Contactless,
                                "NFC ile bilet okuma",
                                modifier = Modifier.fillMaxSize(0.7f)
                                    .align(Alignment.Center)
                            )
                        }
                        else
                        {
                            QRCodeScanner { result ->
                                qrCodeResult = result
                                Log.d("QR Scanner", result)
                                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth())
                {
                    Text(
                        text = if (!QRScannerOn.value) "NFC bilet okunuyor"
                        else "QR bilet okunuyor",
                        style = Typography.headlineLarge,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                    FilledTonalButton(
                        onClick = {
                            nfqSwitch(QRScannerOn, activity, context, permissionLauncher)
                        },
                        modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.Center)
                    )
                    {
                        Icon(
                            if (!QRScannerOn.value) Icons.Outlined.QrCodeScanner
                            else Icons.Outlined.Nfc,
                            if (!QRScannerOn.value) "QR kod okuyucu"
                            else "NFC okuyucu",
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                        Text(
                            text = if (!QRScannerOn.value) "QR bilet oku"
                            else "NFC bilet oku",
                            style = Typography.bodyLarge,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }


    fun nfqSwitch(QRScannerOn: MutableState<Boolean>, activity: Activity, context: Context,
                  permissionLauncher: ManagedActivityResultLauncher<String, Boolean>)
    {
        if (QRScannerOn.value)
        {
            // Bu kısım niyeyse çalışmıyor.
            NFCUtils.enableForegroundDispatch(activity)
        }
        else
        {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!permissionGranted)
            {
                permissionLauncher.launch(Manifest.permission.CAMERA)
                return
            }

            NFCUtils.disableForegroundDispatch(activity)
        }

        QRScannerOn.value = !QRScannerOn.value

    }

}