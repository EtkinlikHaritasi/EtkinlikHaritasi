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
import java.text.SimpleDateFormat
import java.util.Locale

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
            val events = repository.getAllEventsList()
            if (events == null) {
                showNotification("Etkinlik bilgileri alınamadı (boş veri)")
                return Result.retry()
            }

            val newIds = events.map { it.eventId }.toSet()
            val diffCount = (newIds - lastIds).size
            lastIds = newIds

            if (diffCount > 0) {
                showNotification("$diffCount yeni etkinlik bulundu")
            }

            val now = System.currentTimeMillis()
            val oneHourMillis = 3600 * 1000L

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            events.forEach { event ->
                try {
                    val eventDateTimeStr = "${event.date} ${event.time}" // "2025-06-01 14:00"
                    val eventTimeMillis = formatter.parse(eventDateTimeStr)?.time ?: return@forEach

                    val diff = eventTimeMillis - now
                    if (diff in 0 until oneHourMillis) {
                        showNotification("${event.title}   ${diff / 60000} dk kaldı.")
                    }
                } catch (e: Exception) {
                    Log.e("EventSyncWorker", "Etkinlik zamanı parse edilemedi: ${event.title}", e)
                }
            }



            Result.success()
        } catch (e: Exception) {
            Log.e("EventSyncWorker", "Exception in doWork", e)
            showNotification("Etkinlik bilgileri alınamadı")
            return Result.retry()
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
