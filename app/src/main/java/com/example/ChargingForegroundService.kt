package com.example

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log

class ChargingForegroundService : Service() {

    companion object {
        private const val TAG = "ChargingService"
        var isRunning = false
            private set
    }

    private lateinit var settingsManager: SettingsManager
    private lateinit var notificationHelper: BatteryNotificationHelper
    private lateinit var ttsHelper: BengaliTTSHelper
    private var wakeLock: PowerManager.WakeLock? = null

    // Track state to avoid redundant repeated alerts
    private var lastChargingState: Boolean? = null
    private var lastAlertedLevel: Int? = null
    private var lastReminderTimeMs: Long = 0
    private var hasAlertedComplete = false
    private var hasAlertedHighTemp = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> handleBatteryChanged(intent)
                Intent.ACTION_POWER_CONNECTED -> handlePowerConnected()
                Intent.ACTION_POWER_DISCONNECTED -> handlePowerDisconnected()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "ChargingForegroundService created")
        settingsManager = SettingsManager(this)
        notificationHelper = BatteryNotificationHelper(this)
        ttsHelper = BengaliTTSHelper(this)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ChargingAssistant::WakeLock")

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(batteryReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                registerReceiver(batteryReceiver, filter)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register battery receiver", e)
        }

        isRunning = true
        settingsManager.isServiceActive = true

        // Start foreground immediately with proper Android 14+ specialUse type
        val initialNotification = notificationHelper.buildForegroundNotification(
            level = 100,
            isCharging = false,
            targetPercentage = settingsManager.targetPercentage
        )
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    BatteryNotificationHelper.NOTIFICATION_ID_FOREGROUND,
                    initialNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(BatteryNotificationHelper.NOTIFICATION_ID_FOREGROUND, initialNotification)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to startForeground", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "ChargingForegroundService onStartCommand")
        return START_STICKY
    }

    private fun handleBatteryChanged(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = if (level >= 0 && scale > 0) (level * 100) / scale else level

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        val temperature = if (tempTenths > 0) tempTenths / 10.0f else null

        // Update persistent notification
        notificationHelper.updateForegroundNotification(
            level = batteryPct,
            isCharging = isCharging,
            targetPercentage = settingsManager.targetPercentage,
            temperature = temperature
        )

        // Evaluate alert conditions
        evaluateAlertRules(batteryPct, isCharging, temperature)
    }

    private fun handlePowerConnected() {
        if (settingsManager.alertConnected) {
            triggerAlert("চার্জার সংযুক্ত হয়েছে", "চার্জিং শুরু হয়েছে")
        }
        hasAlertedComplete = false
    }

    private fun handlePowerDisconnected() {
        if (settingsManager.alertDisconnected) {
            triggerAlert("চার্জার খুলে নেওয়া হয়েছে", "চার্জার বিচ্ছিন্ন করা হয়েছে")
        }
        hasAlertedComplete = false
    }

    private fun evaluateAlertRules(level: Int, isCharging: Boolean, temperature: Float?) {
        val now = System.currentTimeMillis()

        // 1. High temperature alert
        if (temperature != null && settingsManager.alertHighTemp) {
            val threshold = settingsManager.highTempThreshold
            if (temperature >= threshold && !hasAlertedHighTemp) {
                val bnTemp = BatteryNotificationHelper.toBn(temperature.toInt())
                triggerAlert("ব্যাটারির তাপমাত্রা বেশি, ${bnTemp} ডিগ্রি", "উচ্চ তাপমাত্রা সতর্কতা")
                hasAlertedHighTemp = true
            } else if (temperature < threshold - 2) {
                hasAlertedHighTemp = false
            }
        }

        // 2. Target Level & 80% / 90% / 100% alerts
        if (isCharging) {
            // Charging complete (100%)
            if (level >= 100 && settingsManager.alertChargingComplete && !hasAlertedComplete) {
                triggerAlert("চার্জ সম্পূর্ণ হয়েছে", "১০০% চার্জ সম্পন্ন হয়েছে, চার্জার খুলে নিন")
                hasAlertedComplete = true
            } else if (level < 99) {
                hasAlertedComplete = false
            }

            // 80% alert
            if (level == 80 && settingsManager.alert80 && lastAlertedLevel != 80) {
                triggerAlert("ব্যাটারি ৮০ শতাংশ হয়েছে", "ব্যাটারি ৮০% চার্জ সম্পন্ন")
                lastAlertedLevel = 80
                lastReminderTimeMs = now
            }

            // 90% alert
            if (level == 90 && settingsManager.alert90 && lastAlertedLevel != 90) {
                triggerAlert("ব্যাটারি ৯০ শতাংশ হয়েছে", "ব্যাটারি ৯০% চার্জ সম্পন্ন")
                lastAlertedLevel = 90
                lastReminderTimeMs = now
            }

            // Custom target percentage
            val target = settingsManager.targetPercentage
            if (level == target && target != 80 && target != 90 && target != 100 && lastAlertedLevel != target) {
                val bnTarget = BatteryNotificationHelper.toBn(target)
                triggerAlert("ব্যাটারি ${bnTarget} শতাংশ হয়েছে", "লক্ষ্য ${target}% পূরণ হয়েছে")
                lastAlertedLevel = target
                lastReminderTimeMs = now
            }

            // Repeated reminder if enabled and still charging above target
            if (settingsManager.repeatedReminder && level >= target) {
                val intervalMs = settingsManager.repeatIntervalMinutes * 60 * 1000L
                if (now - lastReminderTimeMs >= intervalMs) {
                    val bnLevel = BatteryNotificationHelper.toBn(level)
                    triggerAlert("স্মারক: ব্যাটারি ${bnLevel} শতাংশ", "চার্জার বিচ্ছিন্ন করার অনুস্মারক")
                    lastReminderTimeMs = now
                }
            }
        } else {
            // Low battery alert when discharging
            val lowThreshold = settingsManager.lowBatteryPercentage
            if (level <= lowThreshold && lastAlertedLevel != -1) {
                val bnLevel = BatteryNotificationHelper.toBn(level)
                triggerAlert("ব্যাটারি কম, ${bnLevel} শতাংশ অবশিষ্ট আছে", "অনুগ্রহ করে চার্জার সংযুক্ত করুন")
                lastAlertedLevel = -1
            } else if (level > lowThreshold + 5 && lastAlertedLevel == -1) {
                lastAlertedLevel = null
            }
        }
    }

    private fun triggerAlert(spokenText: String, notificationMessage: String) {
        // Acquire brief wake-lock to guarantee CPU execution when screen is off
        try {
            wakeLock?.acquire(3000)
        } catch (e: Exception) {
            Log.w(TAG, "WakeLock acquire failed", e)
        }

        if (settingsManager.voiceAlerts) {
            ttsHelper.speak(spokenText)
        }

        if (settingsManager.notificationAlerts) {
            notificationHelper.showAlertNotification("চার্জিং সহকারী", notificationMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ChargingForegroundService onDestroy")
        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "Receiver not registered", e)
        }
        ttsHelper.shutdown()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        isRunning = false
        settingsManager.isServiceActive = false
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
