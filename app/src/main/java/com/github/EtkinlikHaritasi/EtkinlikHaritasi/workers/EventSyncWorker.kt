package com.github.EtkinlikHaritasi.EtkinlikHaritasi.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.repository.EventRepository
import com.google.gson.Gson

class EventSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = EventRepository()
    private val CHANNEL_ID = "event_update_channel"

    companion object {
        private var lastIds: Set<Int> = emptySet()
    }

    override suspend fun doWork(): Result {
        Log.d("EventSyncWorker", "Worker çalıştı")
        val repository = EventRepository()
        return try {
            val events = repository.getEvents()
            val rawJson = events.errorBody()?.string() ?: events.body().toString()
            Log.d("RAW_JSON", rawJson)
            // Önce response başarılı mı kontrol edelim
            if (events.isSuccessful) {
                // Gelen event listesini al
                val newEvents = events.body() ?: emptyList()

                // JSON string olarak Gson ile çevir
                val json = Gson().toJson(newEvents)
                Log.d("API_RESPONSE", json)

                val newIds = newEvents.map { it.eventId }.toSet()

                val diff = (newIds - lastIds).size
                val hasChanged = newIds != lastIds
                lastIds = newIds

                if (hasChanged) {
                    showNotification("$diff yeni etkinlik bulundu")
                } else {
                    showNotification("Etkinliklerde değişiklik yok")
                }
                Result.success()
            } else {
                showNotification("Etkinlik bilgileri alınamadı")
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showNotification("Etkinlik bilgileri alınamadı")
            Result.retry()
        }
    }


    private fun showNotification(message: String) {
        createChannel()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Etkinlik Güncellemesi")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2001, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Etkinlik Bildirimleri",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
