package com.example.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BatteryNotificationHelper
import com.example.BengaliTTSHelper
import com.example.ChargingForegroundService
import com.example.SettingsManager
import com.example.model.BatteryData
import com.example.model.ChargingSessionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class SettingsUiState(
    val serviceActive: Boolean = false,
    val voiceEnabled: Boolean = true,
    val targetPercentage: Int = 80,
    val lowBatteryWarning: Int = 20,
    val alertOn80: Boolean = true,
    val alertOn90: Boolean = true,
    val alertOn100: Boolean = true,
    val alertOnPlugged: Boolean = true,
    val alertOnUnplugged: Boolean = true,
    val highTempWarning: Boolean = true,
    val highTempThreshold: Int = 42,
    val repeatReminders: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isBatteryOptimizationIgnored: Boolean = false
)

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    val settingsManager = SettingsManager(context)
    val ttsHelper = BengaliTTSHelper(context)
    val notificationHelper = BatteryNotificationHelper(context)

    private val _batteryData = MutableStateFlow(BatteryData())
    val batteryData: StateFlow<BatteryData> = _batteryData.asStateFlow()

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<SettingsUiState> = _settings.asStateFlow()

    private val _chargingHistory = MutableStateFlow<List<ChargingSessionItem>>(loadHistory())
    val chargingHistory: StateFlow<List<ChargingSessionItem>> = _chargingHistory.asStateFlow()

    private var currentSessionStartTime: Long? = null
    private var currentSessionStartLevel: Int? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            if (intent == null) return
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> updateBatteryFromIntent(intent)
                Intent.ACTION_POWER_CONNECTED -> {
                    currentSessionStartTime = System.currentTimeMillis()
                    currentSessionStartLevel = _batteryData.value.level
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    recordSessionEnd()
                }
            }
        }
    }

    init {
        // Query sticky battery intent immediately for synchronous initial state
        try {
            val stickyFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val stickyIntent = context.registerReceiver(null, stickyFilter)
            if (stickyIntent != null) {
                updateBatteryFromIntent(stickyIntent)
            }
        } catch (_: Exception) {}

        // Register dynamic battery updates with Android 14+ safe export flags
        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(batteryReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(batteryReceiver, filter)
            }
        } catch (_: Exception) {}

        try {
            refreshBatteryOptimizationStatus()
        } catch (_: Exception) {}
    }

    private fun loadSettings(): SettingsUiState {
        val prefs = context.getSharedPreferences("charging_assistant_prefs", Context.MODE_PRIVATE)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isIgnored = powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false

        return SettingsUiState(
            serviceActive = prefs.getBoolean("service_active", true),
            voiceEnabled = prefs.getBoolean("voice_enabled", true),
            targetPercentage = prefs.getInt("target_percentage", 80),
            lowBatteryWarning = prefs.getInt("low_battery_warning", 20),
            alertOn80 = prefs.getBoolean("alert_on_80", true),
            alertOn90 = prefs.getBoolean("alert_on_90", true),
            alertOn100 = prefs.getBoolean("alert_on_100", true),
            alertOnPlugged = prefs.getBoolean("alert_on_plugged", true),
            alertOnUnplugged = prefs.getBoolean("alert_on_unplugged", true),
            highTempWarning = prefs.getBoolean("high_temp_warning", true),
            highTempThreshold = prefs.getInt("high_temp_threshold", 42),
            repeatReminders = prefs.getBoolean("repeat_reminders", false),
            notificationsEnabled = prefs.getBoolean("notifications_enabled", true),
            isBatteryOptimizationIgnored = isIgnored
        )
    }

    fun refreshBatteryOptimizationStatus() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isIgnored = powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
        _settings.update { it.copy(isBatteryOptimizationIgnored = isIgnored) }
    }

    private fun updateBatteryFromIntent(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level >= 0 && scale > 0) (level * 100) / scale else 100

        val statusInt = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = statusInt == BatteryManager.BATTERY_STATUS_CHARGING ||
                statusInt == BatteryManager.BATTERY_STATUS_FULL

        val statusStr = when (statusInt) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
            BatteryManager.BATTERY_STATUS_FULL -> "full"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not_charging"
            else -> "unknown"
        }

        val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        val tempC = if (rawTemp > 0) rawTemp / 10.0f else null

        val rawVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val voltageMv = if (rawVoltage > 0) rawVoltage else null

        val pluggedInt = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val pluggedType = when (pluggedInt) {
            BatteryManager.BATTERY_PLUGGED_AC -> "ac"
            BatteryManager.BATTERY_PLUGGED_USB -> "usb"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "wireless"
            else -> "none"
        }

        val tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

        val healthInt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthStr = when (healthInt) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "স্বাভাবিক (Good)"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "অতিরিক্ত গরম (Overheat)"
            BatteryManager.BATTERY_HEALTH_DEAD -> "ত্রুটিপূর্ণ (Dead)"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "উচ্চ ভোল্টেজ (Over Voltage)"
            else -> "স্বাভাবিক (Good)"
        }

        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val currentMicroAmp = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) ?: 0
        val currentMa = if (currentMicroAmp != Int.MIN_VALUE && currentMicroAmp != 0) {
            currentMicroAmp / 1000
        } else null

        val powerW = if (voltageMv != null && currentMa != null && currentMa != 0) {
            kotlin.math.abs((voltageMv / 1000.0f) * (currentMa / 1000.0f))
        } else null

        val chargeCounter = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) ?: 0
        val capacityMah = if (chargeCounter > 0) chargeCounter / 1000 else 4500

        val timeRemainingSec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && bm != null) {
            val ms = bm.computeChargeTimeRemaining()
            if (ms > 0) ms / 1000 else null
        } else null

        _batteryData.value = BatteryData(
            level = percentage,
            isCharging = isCharging,
            status = statusStr,
            temperature = tempC,
            voltage = voltageMv,
            current = currentMa,
            power = powerW,
            capacity = capacityMah,
            pluggedType = pluggedType,
            health = healthStr,
            technology = tech,
            chargingTimeRemainingSec = timeRemainingSec
        )
    }

    fun toggleForegroundService(enabled: Boolean) {
        settingsManager.setServiceActive(enabled)
        _settings.update { it.copy(serviceActive = enabled) }
        val serviceIntent = Intent(context, ChargingForegroundService::class.java)
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } else {
            context.stopService(serviceIntent)
        }
    }

    fun updateTargetPercentage(target: Int) {
        settingsManager.setTargetPercentage(target)
        _settings.update { it.copy(targetPercentage = target) }
    }

    fun updateLowBatteryWarning(level: Int) {
        settingsManager.setLowBatteryWarning(level)
        _settings.update { it.copy(lowBatteryWarning = level) }
    }

    fun updateHighTempThreshold(temp: Int) {
        settingsManager.setHighTempThreshold(temp)
        _settings.update { it.copy(highTempThreshold = temp) }
    }

    fun toggleVoice(enabled: Boolean) {
        settingsManager.setVoiceEnabled(enabled)
        _settings.update { it.copy(voiceEnabled = enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        settingsManager.setNotificationsEnabled(enabled)
        _settings.update { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleAlertSetting(key: String, enabled: Boolean) {
        val prefs = context.getSharedPreferences("charging_assistant_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, enabled).apply()
        _settings.value = loadSettings()
    }

    fun speakPhrase(textBn: String) {
        ttsHelper.speak(textBn)
    }

    private fun recordSessionEnd() {
        val startTime = currentSessionStartTime ?: return
        val startLvl = currentSessionStartLevel ?: return
        val durationMins = ((System.currentTimeMillis() - startTime) / 60000).toInt().coerceAtLeast(1)
        val endLvl = _batteryData.value.level

        val newSession = ChargingSessionItem(
            id = System.currentTimeMillis().toString(),
            startTime = startTime,
            startLevel = startLvl,
            endLevel = endLvl,
            durationMinutes = durationMins,
            pluggedType = _batteryData.value.pluggedType
        )

        val updated = listOf(newSession) + _chargingHistory.value
        _chargingHistory.value = updated.take(20)
        saveHistory(_chargingHistory.value)

        currentSessionStartTime = null
        currentSessionStartLevel = null
    }

    private fun loadHistory(): List<ChargingSessionItem> {
        val prefs = context.getSharedPreferences("charging_assistant_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("charging_sessions_json", null) ?: return emptyList()
        val list = mutableListOf<ChargingSessionItem>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ChargingSessionItem(
                        id = obj.getString("id"),
                        startTime = obj.getLong("startTime"),
                        startLevel = obj.getInt("startLevel"),
                        endLevel = if (obj.has("endLevel")) obj.getInt("endLevel") else null,
                        durationMinutes = if (obj.has("durationMinutes")) obj.getInt("durationMinutes") else null,
                        pluggedType = obj.optString("pluggedType", "ac")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun saveHistory(list: List<ChargingSessionItem>) {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("startTime", item.startTime)
                put("startLevel", item.startLevel)
                item.endLevel?.let { put("endLevel", it) }
                item.durationMinutes?.let { put("durationMinutes", it) }
                put("pluggedType", item.pluggedType)
            }
            arr.put(obj)
        }
        val prefs = context.getSharedPreferences("charging_assistant_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("charging_sessions_json", arr.toString()).apply()
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {}
    }
}
