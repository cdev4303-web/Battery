package com.example

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class BatteryNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_MONITOR_ID = "charging_monitor_channel"
        const val CHANNEL_ALERTS_ID = "charging_alerts_channel"
        const val NOTIFICATION_ID_FOREGROUND = 1001
        const val NOTIFICATION_ID_ALERT = 1002

        private val BN_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

        fun toBn(num: Int): String {
            return num.toString().map { if (it in '0'..'9') BN_DIGITS[it - '0'] else it }.joinToString("")
        }
    }

    private val notificationManager: NotificationManager? =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    init {
        try {
            createNotificationChannels()
        } catch (_: Exception) {}
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel 1: Ongoing background monitor status
            val monitorChannel = NotificationChannel(
                CHANNEL_MONITOR_ID,
                "চার্জিং সহকারী স্থিতি (Charging Monitor)",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "লাইভ ব্যাটারি স্তর ও চার্জিং পর্যবেক্ষণ"
                setShowBadge(false)
            }
            notificationManager?.createNotificationChannel(monitorChannel)

            // Channel 2: High priority alerts
            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS_ID,
                "ব্যাটারি সতর্কবার্তা (Battery Alerts)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "চার্জ সম্পূর্ণ, লো ব্যাটারি ও অতিরিক্ত তাপমাত্রা সতর্কতা"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager?.createNotificationChannel(alertsChannel)
        }
    }

    fun buildForegroundNotification(
        level: Int,
        isCharging: Boolean,
        targetPercentage: Int,
        temperature: Float? = null
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val chargingStatusText = if (isCharging) "সক্রিয় (Active)" else "সংযুক্ত নয় (Discharging)"
        val tempText = if (temperature != null && temperature > 0) " • ${toBn(temperature.toInt())}°C" else ""

        // English & Bengali bilingual notification text as specified in prompt
        val title = "চার্জিং সহকারী (Charging Assistant)"
        val content = "Battery: ${level}% (${toBn(level)}%) • Charging: ${if (isCharging) "Active" else "Off"} • Target: ${targetPercentage}%${tempText}"

        return NotificationCompat.Builder(context, CHANNEL_MONITOR_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    fun showAlertNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            notificationManager?.notify(NOTIFICATION_ID_ALERT, notification)
        } catch (_: Exception) {}
    }

    fun updateForegroundNotification(
        level: Int,
        isCharging: Boolean,
        targetPercentage: Int,
        temperature: Float? = null
    ) {
        try {
            val notification = buildForegroundNotification(level, isCharging, targetPercentage, temperature)
            notificationManager?.notify(NOTIFICATION_ID_FOREGROUND, notification)
        } catch (_: Exception) {}
    }
}
