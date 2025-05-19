package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.*
import java.util.Locale

class SpeechRecognitionService : Service() {

    companion object {
        private const val TAG = "SpeechRecognitionService"
        private const val CHANNEL_ID = "speech_recognition_channel"
        private const val NOTIFICATION_ID = 1
        private const val WINDOW_DURATION = 5 * 60 * 1000L // 5 dakika
    }

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private lateinit var generativeModel: GenerativeModel

    private val anahtarKelimelerListesi =
        listOf("etkinlik", "harita", "sıkıldım", "ne var", "konser", "ara")

    private var windowStart: Long? = null

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var prevcommand: String = ""
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e(TAG, "SpeechRecognizer desteklenmiyor, servisi durduruyorum.")
            stopSelf()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(recognitionListener)

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            if (BuildConfig.GEMINI_API_KEY.isEmpty()) {
                Log.e(TAG, "Gemini API Anahtarı eksik!")
            } else {
                generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )
                Log.i(TAG, "Gemini başarıyla başlatıldı.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini başlatma hatası: ${e.localizedMessage}")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Servis başlatıldı.")
        restartListening()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        try {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        } catch (e: Exception) {
            Log.e(TAG, "Recognizer kapatma hatası: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, " Hazır")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, " Konuşma başladı")
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d(TAG, "Konuşma bitti")
        }

        override fun onError(error: Int) {
            val msg = getErrorText(error)
            Log.e(TAG, "Hata: $msg (Kod: $error)")
            when (error) {
                SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> restartListening()
                SpeechRecognizer.ERROR_CLIENT, SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    Log.e(TAG, "Kritik hata.")
                }
                else -> restartListeningWithDelay()
            }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognized = matches?.getOrNull(0)?.trim() ?: ""
            Log.i(TAG, " Algılanan: \"$recognized\"")

            val now = System.currentTimeMillis()
            val inWindow = windowStart?.let { now - it < WINDOW_DURATION } ?: false

            if (recognized.isNotBlank()) {
                sendBroadcast(Intent("SPEECH_RESULT").apply {
                    putExtra("result", recognized)
                })

                if (metindeAnahtarKelimeVarMi(recognized)) {
                    // Anahtar kelime bulundu: pencereyi başlat ve ilk isteği gönder
                    windowStart = now
                    checkKeywordsAndProcessWithGemini(recognized)
                } else if (inWindow) {
                    // Anahtar kelime sonrası 5 dk içerisindeki tüm metinler
                    checkKeywordsAndProcessWithGemini(recognized)
                } else if (!inWindow) {
                    // Anahtar kelime yok ve pencere aktif değil: normal dinleme
                    restartListening()
                }
            } else {
                restartListening()
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun checkKeywordsAndProcessWithGemini(text: String) {

        val prompt = """
            Kullanıcı şöyle dedi:
            önce şöyle dedi "$prevcommand" en sonda şöyle dedi bir önceki söylediğini de dikkate alarak enson söylediği ni 
            "$text"

            kullanıcıya etkinlikler veya aktiviteler öner dost canlısı olacak şekilde diyalog kuararmısın .
        """.trimIndent()
        prevcommand=text
        serviceScope.launch {
            Log.d(TAG, "Gemini isteği gönderiliyor...")
            val reply = geminiIleCevapUret(prompt)
            if (!reply.isNullOrBlank()) {
                Log.i(TAG, " Gemini: \"$reply\"")
                sendBroadcast(Intent("GEMINI_RESPONSE").apply {
                    putExtra("response", reply)
                })
                sendBroadcast(Intent("OUTPUT_TEXT").apply {
                    putExtra("output", reply)
                })
            } else {
                Log.w(TAG, " Gemini cevabı alınamadı.")
            }
            Handler(Looper.getMainLooper()).post { restartListening() }
        }
    }

    private fun metindeAnahtarKelimeVarMi(text: String): Boolean {
        val lower = text.lowercase(Locale.getDefault())
        return anahtarKelimelerListesi.any { lower.contains(it.lowercase(Locale.getDefault())) }
    }

    private suspend fun geminiIleCevapUret(prompt: String): String? {
        return try {
            if (!::generativeModel.isInitialized) {
                Log.e(TAG, "Gemini modeli başlatılmamış!")
                null
            } else {
                generativeModel.generateContent(prompt).text
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API hatası: ${e.localizedMessage}", e)
            null
        }
    }

    private fun restartListening() {
        Handler(Looper.getMainLooper()).post {
            try {
                speechRecognizer.startListening(speechIntent)
                Log.d(TAG, "🎧 Dinleme başlatıldı.")
            } catch (e: SecurityException) {
                Log.e(TAG, "İzin hatası: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Dinleme hatası: ${e.message}")
            }
        }
    }

    private fun restartListeningWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({ restartListening() }, 1000)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                "Speech Recognition",
                NotificationManager.IMPORTANCE_LOW
            ).also { channel ->
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(channel)
            }
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Mikrofon Aktif")
        .setContentText("Konuşma tanıma servisi çalışıyor")
        .setSmallIcon(R.drawable.mic)
        .setOngoing(true)
        .build()

    private fun getErrorText(code: Int): String = when (code) {
        SpeechRecognizer.ERROR_AUDIO -> "Ses problemi"
        SpeechRecognizer.ERROR_CLIENT -> "İstemci hatası"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "İzin hatası"
        SpeechRecognizer.ERROR_NETWORK -> "Ağ hatası"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Ağ zaman aşımı"
        SpeechRecognizer.ERROR_NO_MATCH -> "Eşleşme yok"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Meşgul"
        SpeechRecognizer.ERROR_SERVER -> "Sunucu hatası"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Zaman aşımı"
        else -> "Bilinmeyen hata"
    }
}
