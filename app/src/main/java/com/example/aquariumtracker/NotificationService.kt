package com.example.aquariumtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyWorker(private val context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationText = inputData.getString("NOTIF_TEXT") ?: return Result.failure()
        val notificationService = NotificationService(context)
        notificationService.setNotificationText(notificationText)
        notificationService.showNotification(0)
        return Result.success()
    }

}

class NotificationService(private val context: Context) {

    val CHANNEL_ID = "default"

    private var builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_fish)
        .setContentTitle("test title")
        .setContentText("test text")
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "test name"
            val descriptionText = "test description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(context, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(notificationId: Int) {
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    fun setNotificationText(text: String) {
        builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fish)
            .setContentTitle(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

}