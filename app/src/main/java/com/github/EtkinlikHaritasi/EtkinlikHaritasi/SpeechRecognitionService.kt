package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechRecognitionService : Service() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private val TAG = "SpeechRecognitionService"

    override fun onCreate() {
        super.onCreate()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "Hazır")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Konuşma başladı")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "Konuşma bitti")
            }

            override fun onError(error: Int) {
                Log.d(TAG, "Hata: $error")
                restartListeningWithDelay()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.get(0) ?: ""
                Log.d(TAG, "Sonuç: $text")

                val intent = Intent("SPEECH_RESULT")
                intent.putExtra("result", text)
                sendBroadcast(intent)

                restartListeningWithDelay()
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    private fun restartListeningWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            speechRecognizer.startListening(speechIntent)
        }, 500) // Yarım saniye gecikme
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        speechRecognizer.startListening(speechIntent)
        return START_STICKY
    }

    override fun onDestroy() {
        speechRecognizer.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
