package com.example

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("charging_assistant_prefs", Context.MODE_PRIVATE)

    var targetPercentage: Int
        get() = prefs.getInt("targetPercentage", 80)
        set(value) = prefs.edit().putInt("targetPercentage", value).apply()

    var lowBatteryPercentage: Int
        get() = prefs.getInt("lowBatteryPercentage", 20)
        set(value) = prefs.edit().putInt("lowBatteryPercentage", value).apply()

    var alert80: Boolean
        get() = prefs.getBoolean("alert80", true)
        set(value) = prefs.edit().putBoolean("alert80", value).apply()

    var alert90: Boolean
        get() = prefs.getBoolean("alert90", true)
        set(value) = prefs.edit().putBoolean("alert90", value).apply()

    var alert100: Boolean
        get() = prefs.getBoolean("alert100", true)
        set(value) = prefs.edit().putBoolean("alert100", value).apply()

    var alertConnected: Boolean
        get() = prefs.getBoolean("alertConnected", true)
        set(value) = prefs.edit().putBoolean("alertConnected", value).apply()

    var alertDisconnected: Boolean
        get() = prefs.getBoolean("alertDisconnected", true)
        set(value) = prefs.edit().putBoolean("alertDisconnected", value).apply()

    var alertChargingComplete: Boolean
        get() = prefs.getBoolean("alertChargingComplete", true)
        set(value) = prefs.edit().putBoolean("alertChargingComplete", value).apply()

    var alertHighTemp: Boolean
        get() = prefs.getBoolean("alertHighTemp", true)
        set(value) = prefs.edit().putBoolean("alertHighTemp", value).apply()

    var highTempThreshold: Int
        get() = prefs.getInt("highTempThreshold", 42)
        set(value) = prefs.edit().putInt("highTempThreshold", value).apply()

    var repeatedReminder: Boolean
        get() = prefs.getBoolean("repeatedReminder", false)
        set(value) = prefs.edit().putBoolean("repeatedReminder", value).apply()

    var repeatIntervalMinutes: Int
        get() = prefs.getInt("repeatIntervalMinutes", 3)
        set(value) = prefs.edit().putInt("repeatIntervalMinutes", value).apply()

    var voiceAlerts: Boolean
        get() = prefs.getBoolean("voiceAlerts", true)
        set(value) = prefs.edit().putBoolean("voiceAlerts", value).apply()

    var notificationAlerts: Boolean
        get() = prefs.getBoolean("notificationAlerts", true)
        set(value) = prefs.edit().putBoolean("notificationAlerts", value).apply()

    var autoStartOnBoot: Boolean
        get() = prefs.getBoolean("autoStartOnBoot", true)
        set(value) = prefs.edit().putBoolean("autoStartOnBoot", value).apply()

    var isServiceActive: Boolean
        get() = prefs.getBoolean("isServiceActive", false)
        set(value) = prefs.edit().putBoolean("isServiceActive", value).apply()

    fun updateFromJson(jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            val editor = prefs.edit()
            if (json.has("targetPercentage")) editor.putInt("targetPercentage", json.getInt("targetPercentage"))
            if (json.has("lowBatteryPercentage")) editor.putInt("lowBatteryPercentage", json.getInt("lowBatteryPercentage"))
            if (json.has("alert80")) editor.putBoolean("alert80", json.getBoolean("alert80"))
            if (json.has("alert90")) editor.putBoolean("alert90", json.getBoolean("alert90"))
            if (json.has("alert100")) editor.putBoolean("alert100", json.getBoolean("alert100"))
            if (json.has("alertConnected")) editor.putBoolean("alertConnected", json.getBoolean("alertConnected"))
            if (json.has("alertDisconnected")) editor.putBoolean("alertDisconnected", json.getBoolean("alertDisconnected"))
            if (json.has("alertChargingComplete")) editor.putBoolean("alertChargingComplete", json.getBoolean("alertChargingComplete"))
            if (json.has("alertHighTemp")) editor.putBoolean("alertHighTemp", json.getBoolean("alertHighTemp"))
            if (json.has("highTempThreshold")) editor.putInt("highTempThreshold", json.getInt("highTempThreshold"))
            if (json.has("repeatedReminder")) editor.putBoolean("repeatedReminder", json.getBoolean("repeatedReminder"))
            if (json.has("repeatIntervalMinutes")) editor.putInt("repeatIntervalMinutes", json.getInt("repeatIntervalMinutes"))
            if (json.has("voiceAlerts")) editor.putBoolean("voiceAlerts", json.getBoolean("voiceAlerts"))
            if (json.has("notificationAlerts")) editor.putBoolean("notificationAlerts", json.getBoolean("notificationAlerts"))
            if (json.has("autoStartOnBoot")) editor.putBoolean("autoStartOnBoot", json.getBoolean("autoStartOnBoot"))
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
