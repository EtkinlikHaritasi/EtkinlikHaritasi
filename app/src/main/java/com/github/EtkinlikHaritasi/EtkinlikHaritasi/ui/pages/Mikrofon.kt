package com.github.EtkinlikHaritasi.EtkinlikHaritasi.ui.pages

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.R
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.SpeechRecognitionService
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Mic

class Mikrofon {

    val açıkken_simge = Icons.Filled.Mic
    val kapalıyken_simge = Icons.Outlined.Mic
    val başlık = "Mikrofon"

    @Composable
    fun İçerik(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val activity = context as Activity

        var permissionGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                permissionGranted = isGranted
            }
        )

        LaunchedEffect(Unit) {
            if (!permissionGranted) {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        if (!permissionGranted) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Mikrofon izni verilmedi. Lütfen izin verin.",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = {
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }) {
                    Text(text = "İzin İste")
                }
            }
            return
        }

        MicScreen(modifier)
    }

    @Composable
    private fun MicScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var outputText by remember { mutableStateOf("Mikrofon kapalı") }
        var micEnabled by remember { mutableStateOf(false) }

        val receiver = remember {
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val result = intent?.getStringExtra("result")
                    if (!result.isNullOrEmpty()) {
                        outputText = result
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            val filter = IntentFilter("SPEECH_RESULT")
            context.registerReceiver(receiver, filter)
            onDispose {
                context.unregisterReceiver(receiver)
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Mikrofon Durumu",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.mic),
                contentDescription = "Microphone",
                modifier = Modifier
                    .size(80.dp)
                    .clickable {
                        micEnabled = !micEnabled
                        outputText = if (micEnabled) "Mikrofon Açıldı" else "Mikrofon Kapalı"

                        if (micEnabled) {
                            val serviceIntent = Intent(context, SpeechRecognitionService::class.java)
                            ContextCompat.startForegroundService(context, serviceIntent)
                        } else {
                            val stopIntent = Intent(context, SpeechRecognitionService::class.java)
                            context.stopService(stopIntent)
                        }
                    }
                    .padding(bottom = 16.dp),
            )

            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .background(
                        color = if (micEnabled) colorResource(id = R.color.white) else colorResource(id = R.color.mic_enabled_color),
                        shape = CircleShape
                    )
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (micEnabled) "Algılanan Konuşma:" else "Durum:",
                        fontSize = 18.sp,
                        color = if (micEnabled) colorResource(id = R.color.black) else colorResource(id = R.color.white)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = outputText,
                        fontSize = 20.sp,
                        color = if (micEnabled) colorResource(id = R.color.black) else colorResource(id = R.color.white)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                micEnabled = !micEnabled
                outputText = if (micEnabled) "Mikrofon Açıldı" else "Mikrofon Kapalı"
                if (micEnabled) {
                    val serviceIntent = Intent(context, SpeechRecognitionService::class.java)
                    ContextCompat.startForegroundService(context, serviceIntent)
                } else {
                    val stopIntent = Intent(context, SpeechRecognitionService::class.java)
                    context.stopService(stopIntent)
                }
            }) {
                Text(text = if (micEnabled) "Mikrofonu Kapat" else "Mikrofonu Aç")
            }
        }
    }
}
