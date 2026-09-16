package com.example.model

data class BatteryData(
    val level: Int = 100,
    val isCharging: Boolean = false,
    val status: String = "discharging",
    val temperature: Float? = null,
    val voltage: Int? = null,           // in mV
    val current: Int? = null,           // in mA
    val power: Float? = null,           // in Watts
    val capacity: Int? = null,          // in mAh
    val pluggedType: String = "none",   // ac, usb, wireless, none
    val health: String = "স্বাভাবিক (Good)",
    val technology: String = "Li-ion",
    val chargingTimeRemainingSec: Long? = null
)

data class ChargingSessionItem(
    val id: String,
    val startTime: Long,
    val startLevel: Int,
    val endLevel: Int? = null,
    val durationMinutes: Int? = null,
    val pluggedType: String = "ac"
)
