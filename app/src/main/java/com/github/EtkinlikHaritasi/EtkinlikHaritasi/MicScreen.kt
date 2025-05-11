package com.github.EtkinlikHaritasi.EtkinlikHaritasi
import android.content.Intent

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.R
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.CircleShape

@Composable
fun MicScreen(navController: NavHostController) {
    var outputText by remember { mutableStateOf("Output Text Here...") }
    var micEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as Activity

    // Mikrofon izni kontrolü
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
    }

    val micColor = if (micEnabled) colorResource(id = R.color.mic_enabled_color) else colorResource(id = R.color.mic_disabled_color)

    Column(
        modifier = Modifier
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
                        startSpeechToText(activity) { result ->
                            outputText = result
                        }
                    } else {
                        outputText = "Mikrofon Kapalı"
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
            Text(
                text = outputText,
                fontSize = 20.sp,
                color = if (micEnabled) colorResource(id = R.color.black) else colorResource(id = R.color.white)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            micEnabled = !micEnabled
            outputText = if (micEnabled) "Mikrofon Açıldı" else "Mikrofon Kapalı"
            if (micEnabled) {
                startSpeechToText(activity) { result ->
                    outputText = result
                }
            } else {
                outputText = "Mikrofon Kapalı"
            }
        }) {
            Text(text = if (micEnabled) "Mikrofonu Kapat" else "Mikrofonu Aç")
        }
    }
}

private fun startSpeechToText(activity: Activity, onResult: (String) -> Unit) {
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)
    val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    speechRecognizerIntent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(bundle: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(v: Float) {}
        override fun onBufferReceived(bytes: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(i: Int) {
            onResult("Mikrofon Hatası: $i")
        }

        override fun onResults(bundle: Bundle) {
            val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (result != null) {
                onResult(result[0])
            }
        }

        override fun onPartialResults(bundle: Bundle) {}
        override fun onEvent(i: Int, bundle: Bundle?) {}
    })

    speechRecognizer.startListening(speechRecognizerIntent)
}
