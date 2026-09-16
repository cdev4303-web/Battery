package com.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.json.JSONObject

class ChargingAssistantBridge(
    private val activity: Activity,
    private val settingsManager: SettingsManager,
    private val ttsHelper: BengaliTTSHelper,
    private val notificationHelper: BatteryNotificationHelper
) {

    @JavascriptInterface
    fun isNativeAndroid(): Boolean = true

    @JavascriptInterface
    fun isServiceRunning(): Boolean = ChargingForegroundService.isRunning

    @JavascriptInterface
    fun startForegroundService() {
        activity.runOnUiThread {
            try {
                val intent = Intent(activity, ChargingForegroundService::class.java)
                ContextCompat.startForegroundService(activity, intent)
                Toast.makeText(activity, "চার্জিং সহকারী ফোরগ্রাউন্ড সার্ভিস চালু হয়েছে", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, "সার্ভিস চালু করা সম্ভব হয়নি: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JavascriptInterface
    fun stopForegroundService() {
        activity.runOnUiThread {
            try {
                val intent = Intent(activity, ChargingForegroundService::class.java)
                activity.stopService(intent)
                Toast.makeText(activity, "ফোরগ্রাউন্ড সার্ভিস বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JavascriptInterface
    fun getBatteryInfo(): String {
        val json = JSONObject()
        try {
            val bm = activity.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = activity.registerReceiver(null, ifilter)

            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = if (level >= 0 && scale > 0) (level * 100) / scale else level
            json.put("level", batteryPct)

            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            json.put("isCharging", isCharging)

            when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> json.put("status", "charging")
                BatteryManager.BATTERY_STATUS_DISCHARGING -> json.put("status", "discharging")
                BatteryManager.BATTERY_STATUS_FULL -> json.put("status", "full")
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> json.put("status", "not_charging")
                else -> json.put("status", "unknown")
            }

            // Temperature
            val tempTenths = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
            if (tempTenths > 0) {
                json.put("temperature", tempTenths / 10.0)
            } else {
                json.put("temperature", JSONObject.NULL)
            }

            // Voltage in mV
            val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
            if (voltage > 0) {
                json.put("voltage", voltage)
            } else {
                json.put("voltage", JSONObject.NULL)
            }

            // Plugged type
            val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
            when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> json.put("pluggedType", "ac")
                BatteryManager.BATTERY_PLUGGED_USB -> json.put("pluggedType", "usb")
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> json.put("pluggedType", "wireless")
                else -> json.put("pluggedType", if (isCharging) "ac" else "none")
            }

            // Battery Health
            val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
            val healthStr = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "ভাল (Good)"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "অতিরিক্ত গরম (Overheat)"
                BatteryManager.BATTERY_HEALTH_DEAD -> "ত্রুটিপূর্ণ (Dead)"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "উচ্চ ভোল্টেজ (Over Voltage)"
                else -> "স্বাভাবিক"
            }
            json.put("health", healthStr)

            // Technology
            val tech = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            json.put("technology", if (!tech.isNullOrBlank()) tech else "Li-ion")

            // Current in mA (if hardware supports)
            val currentNow = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            if (currentNow != Int.MIN_VALUE && currentNow != 0) {
                // If in microamperes (e.g. > 10000), convert to mA
                val currentMA = if (Math.abs(currentNow) > 10000) currentNow / 1000 else currentNow
                json.put("current", currentMA)
            } else {
                json.put("current", JSONObject.NULL)
            }

            // Capacity in mAh (if hardware supports)
            val chargeCounter = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
            if (chargeCounter > 0) {
                // Usually in microampere-hours
                val capMah = chargeCounter / 1000
                json.put("capacity", capMah)
            } else {
                json.put("capacity", JSONObject.NULL)
            }

            // Charging time remaining (API 28+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val timeRemainingMs = bm.computeChargeTimeRemaining()
                if (timeRemainingMs > 0) {
                    json.put("chargingTime", timeRemainingMs / 1000)
                } else {
                    json.put("chargingTime", JSONObject.NULL)
                }
            } else {
                json.put("chargingTime", JSONObject.NULL)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return json.toString()
    }

    @JavascriptInterface
    fun requestBatteryOptimization() {
        activity.runOnUiThread {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                    val packageName = activity.packageName
                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:$packageName")
                        }
                        activity.startActivity(intent)
                    } else {
                        Toast.makeText(activity, "ইতিমধ্যেই ব্যাটারি অপটিমাইজেশন বন্ধ রয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                openBatterySettings()
            }
        }
    }

    @JavascriptInterface
    fun openBatterySettings() {
        activity.runOnUiThread {
            try {
                val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                activity.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${activity.packageName}")
                    }
                    activity.startActivity(intent)
                } catch (e2: Exception) {
                    Toast.makeText(activity, "সেটিংস খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @JavascriptInterface
    fun speak(text: String) {
        ttsHelper.speak(text)
    }

    @JavascriptInterface
    fun showNotification(title: String, message: String) {
        notificationHelper.showAlertNotification(title, message)
    }

    @JavascriptInterface
    fun syncSettings(settingsJson: String) {
        settingsManager.updateFromJson(settingsJson)
    }
}
