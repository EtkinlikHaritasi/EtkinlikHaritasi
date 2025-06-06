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
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.localdb.entity.Event
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.app.NotificationManagerCompat
import com.github.EtkinlikHaritasi.EtkinlikHaritasi.R

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
        private const val NOTIFICATION_ID = 1001

    }

    override suspend fun doWork(): Result {
        Log.d("EventSyncWorker", "Etkinlik senkronizasyonu başladı")
        val token = inputData.getString("LOGIN_TOKEN")
        Log.e("token ", token.toString())
        return try {

            val remoteEvents = repository.getEvents(token.toString())

            if (remoteEvents == null) {
                showNotification("Etkinlik bilgileri alınamadı (boş veri)")
                return Result.retry()
            }

            val localEvents = repository.getLocalEvents()
            val localIds = localEvents.map { it.eventId }.toSet()
            Log.e("local events ", localEvents.toString())


            val remoteIds = remoteEvents.map { it.eventId }.toSet()
            Log.e("remote events ", remoteEvents.toString())

            val newEventIds = remoteIds - localIds

            if (newEventIds.isNotEmpty()) {
                showNotification("${newEventIds.size} yeni etkinlik bulundu")
            }
            else{
                showNotification(" yeni etkinlik bulunmadı")
                val eventTitles = remoteEvents?.joinToString("\n") { event: Event -> event.title } ?: "Etkinlik bulunamadı."

                val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setContentTitle("Yakındaki Etkinlikler")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(eventTitles))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(applicationContext)) {
                    notify(NOTIFICATION_ID, builder.build())
                }
            }

            val newEvents = remoteEvents.filter { it.eventId in newEventIds }
            repository.insertEvents(newEvents)

            val now = System.currentTimeMillis()
            val oneHourMillis = 3600 * 1000L
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            remoteEvents.forEach { event ->
                try {
                    val eventDateTimeStr = "${event.date} ${event.time}"
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
            Result.retry()
        }
    }


    private fun showNotification(message: String) {
        createChannelIfNeeded()

        val intent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)?.apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        } ?: return  // intent null ise bildirimi gösterme

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_MUTABLE
        } else {
            android.app.PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Etkinlik Güncellemesi")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2001, notification)
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
