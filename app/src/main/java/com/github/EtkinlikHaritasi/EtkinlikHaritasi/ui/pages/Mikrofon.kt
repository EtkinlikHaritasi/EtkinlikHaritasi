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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.SmartToy
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape

class Mikrofon {

    val açıkken_simge = Icons.Filled.SmartToy
    val kapalıyken_simge = Icons.Outlined.SmartToy
    val başlık = "Etkinlik asistanı"

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
                modifier = modifier.fillMaxSize(),
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
        var speechText by remember { mutableStateOf("Mikrofon Açıldı") }
        var geminiResponseText by remember { mutableStateOf("Etkinlik Asistanı") }

        val receiver = remember {
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.action) {
                        "SPEECH_RESULT" -> {
                            val result = intent.getStringExtra("result")
                            if (!result.isNullOrEmpty()) {
                                speechText = result
                            }
                        }
                        "GEMINI_RESPONSE" -> {
                            val response = intent.getStringExtra("response")
                            if (!response.isNullOrEmpty()) {
                                geminiResponseText = response
                            }
                        }
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            val filter = IntentFilter().apply {
                addAction("SPEECH_RESULT")
                addAction("GEMINI_RESPONSE")
            }

            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )

            val serviceIntent = Intent(context, SpeechRecognitionService::class.java)
            context.startService(serviceIntent)

            onDispose {
                context.unregisterReceiver(receiver)
            }
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // ⭐️ Kaydırma eklendi
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Etkinlik Asistanı",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.mic),
                contentDescription = "Microphone",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    val intent = Intent("START_LISTENING")
                    context.sendBroadcast(intent)
                }
            ) {
                Icon(imageVector = Icons.Outlined.Mic, contentDescription = "Dinlemeyi Başlat")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mikrofona Konuş")
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .background(
                        color = colorResource(id = R.color.white),
                        shape = CircleShape
                    )
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Algılanan Konuşma:",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = speechText,
                        fontSize = 20.sp,
                        color = colorResource(id = R.color.black)
                    )
                }
            }

            if (geminiResponseText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .background(
                            color = colorResource(id = R.color.white),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Asistan Yanıtı:",
                            fontSize = 18.sp,
                            color = colorResource(id = R.color.black)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = geminiResponseText,
                            fontSize = 20.sp,
                            color = colorResource(id = R.color.black)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Alt boşluk
        }
    }


}
