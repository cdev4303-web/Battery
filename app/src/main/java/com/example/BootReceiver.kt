package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            val settingsManager = SettingsManager(context)
            if (settingsManager.autoStartOnBoot && settingsManager.isServiceActive) {
                val serviceIntent = Intent(context, ChargingForegroundService::class.java)
                try {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
