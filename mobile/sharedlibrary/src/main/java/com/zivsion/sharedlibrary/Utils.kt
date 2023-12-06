package com.zivsion.sharedlibrary

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.MessageEvent
import com.zivsion.handlerservice.R
import com.zivsion.sharedlibrary.Constants.Companion.PATH

class Utils {
    companion object {
        fun createNotification(context: Context): Notification {
            val notificationChannelId = "com.zivsion.notificationChannelId"
            val notificationChannelName = "com.zivsion.notificationChannelName"
            val notificationChannelDescription = "com.zivsion.notificationChannelDescription"
            val notificationChannelImportance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel(
                notificationChannelId, notificationChannelName, notificationChannelImportance
            )
            notificationChannel.description = notificationChannelDescription

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
            notificationBuilder.setContentTitle("Service is running")
            notificationBuilder.setContentText("Service is running")
            notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT

            return notificationBuilder.build()
        }

        fun relevantMessage(messageEvent: MessageEvent) = messageEvent.path == PATH
    }
}