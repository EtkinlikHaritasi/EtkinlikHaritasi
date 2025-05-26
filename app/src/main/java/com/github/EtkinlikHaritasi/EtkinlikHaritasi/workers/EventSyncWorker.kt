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
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.database.AppDatabaseInstance

class EventSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val repository = EventRepository(
        AppDatabaseInstance.getDatabase(applicationContext).eventDao()
    )

    private val CHANNEL_ID = "event_update_channel"

    companion object {
        private var lastIds: Set<Int> = emptySet()
    }

    override suspend fun doWork(): Result {
        return try {
            val response = repository.getEvents()
            if (response.isSuccessful) {
                val events = response.body()
                if (events != null) {
                    val newIds = events.map { it.eventId }.toSet()
                    val diffCount = (newIds - lastIds).size
                    lastIds = newIds

                    val message = if (diffCount > 0) {
                        "$diffCount yeni etkinlik bulundu"
                    } else {
                        "Etkinliklerde değişiklik yok"
                    }
                    showNotification(message)
                    Result.success()
                } else {
                    showNotification("Etkinlik bilgileri alınamadı (boş veri)")
                    Result.retry()
                }
            } else {
                showNotification("Etkinlik bilgileri alınamadı (hata: ${response.code()})")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("EventSyncWorker", "Exception in doWork", e)
            showNotification("Etkinlik bilgileri alınamadı")
            Result.retry()
        }
    }

    private fun showNotification(message: String) {
        createChannelIfNeeded()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Etkinlik Güncellemesi")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(2001, notification)
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Etkinlik Bildirimleri",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
}
