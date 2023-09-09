package com.example.TaskManager

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat


class YourForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationText = intent?.getStringExtra("notificationText")
        val notification = createNotification(notificationText ,this )
        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotification(text : String? , context: Context): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java) 
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, "IDhehe")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("You have a task!")
            .setContentText(text)
            .setOngoing(true) // Make the notification permanent
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent) // Set the PendingIntent to open the app

        return builder.build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
