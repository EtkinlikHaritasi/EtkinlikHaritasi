package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.*
import java.util.*
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.EventRepository
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabase
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabaseInstance
class SpeechRecognitionService : Service() {

    companion object {
        private const val TAG = "SpeechRecognitionService"
        private const val CHANNEL_ID = "speech_recognition_channel"
        private const val NOTIFICATION_ID = 1
        private const val WINDOW_DURATION = 5 * 60 * 1000L
        private const val ACTION_START_LISTENING = "START_LISTENING"
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

    private val listenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_START_LISTENING) {
                restartListening()
            }
        }
    }
    private fun getEventRepository(): EventRepository {
        val eventDao = AppDatabaseInstance.getDatabase(applicationContext).eventDao()
        return EventRepository(eventDao)
    }





    override fun onCreate() {
        super.onCreate()

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
            val gemini_key = BuildConfig.GEMINI_API_KEY
            generativeModel = GenerativeModel("gemini-1.5-flash", gemini_key)
            Log.i(TAG, "Gemini başarıyla başlatıldı.")
        } catch (e: Exception) {
            Log.e(TAG, "Gemini başlatma hatası: ${e.localizedMessage}")
        }

        registerReceiver(listenReceiver, IntentFilter(ACTION_START_LISTENING))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Servis başlatıldı.")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(listenReceiver)
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
        override fun onReadyForSpeech(params: Bundle?) { Log.d(TAG, "Hazır") }
        override fun onBeginningOfSpeech() { Log.d(TAG, "Konuşma başladı") }
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() { Log.d(TAG, "Konuşma bitti") }

        override fun onError(error: Int) {
            val msg = getErrorText(error)
            Log.e(TAG, "Hata: $msg (Kod: $error)")
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognized = matches?.getOrNull(0)?.trim() ?: ""
            Log.i(TAG, " Algılanan: \"$recognized\"")

            if (recognized.isNotBlank()) {
                sendBroadcast(Intent("SPEECH_RESULT").apply {
                    putExtra("result", recognized)
                })

                checkKeywordsAndProcessWithGemini(recognized)
            }
        }


        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun checkKeywordsAndProcessWithGemini(text: String) {
        val repository = getEventRepository()
        serviceScope.launch {
            val events = repository.getAllEventsList()

            val eventTitles = events?.joinToString("\n") { it.title } ?: "Etkinlik bulunamadı."
            Log.d(TAG,  eventTitles)
            val prompt = """
            Kullanıcı şöyle dedi: "$text"
            kullanıcıya etkinlikler veya aktiviteler öner  kısaca diyalog kurarmısın.
            İşte mevcut etkinlikler: ve zamanları 
            $eventTitles
        """.trimIndent()

            prevcommand = text
            Log.d(TAG, "Gemini isteği gönderiliyor...")

            val reply = geminiIleCevapUret(prompt)
            if (!reply.isNullOrBlank()) {
                sendBroadcast(Intent("GEMINI_RESPONSE").putExtra("response", reply))
                sendBroadcast(Intent("OUTPUT_TEXT").putExtra("output", reply))
            } else {
                Log.w(TAG, "Gemini cevabı alınamadı.")
            }
        }
    }


    private fun metindeAnahtarKelimeVarMi(text: String): Boolean {
        val lower = text.lowercase(Locale.getDefault())
        return anahtarKelimelerListesi.any { lower.contains(it.lowercase(Locale.getDefault())) }
    }

    private suspend fun geminiIleCevapUret(prompt: String): String? {
        return try {
            if (!::generativeModel.isInitialized) null
            else generativeModel.generateContent(prompt).text
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
            } catch (e: Exception) {
                Log.e(TAG, "Dinleme hatası: ${e.message}")
            }
        }
    }





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
