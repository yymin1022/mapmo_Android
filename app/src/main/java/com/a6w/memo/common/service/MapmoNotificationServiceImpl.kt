package com.a6w.memo.common.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.a6w.memo.R
import com.a6w.memo.domain.service.MapmoNotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Mapmo Notification Service Implementation
 * - Implements [MapmoNotificationService]
 */
class MapmoNotificationServiceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
): MapmoNotificationService {
    // Android Notification Manager
    private val notificationManager
        = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        // Channel Infos
        private const val CHANNEL_ID = "mapmo_notification_channel"
        private const val CHANNEL_NAME = "Mapmo Notification"
        private const val CHANNEL_DESCRIPTION = "Notify when user is at mapmo location"
    }

    init {
        createNotificationChannel()
    }

    override fun showNotification(title: String, content: String) {
        // Generate notification instance
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Send notification with unique ID (System time millis)
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        // Create mapmo notification channel
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        // Register mapmo notification channel
        notificationManager.createNotificationChannel(channel)
    }
}