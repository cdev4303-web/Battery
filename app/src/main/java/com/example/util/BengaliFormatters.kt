package com.example.util

import java.util.Locale

object BengaliFormatters {
    private val BN_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    const val NOT_AVAILABLE_BN = "ডিভাইসে উপলব্ধ নয়"

    fun toBn(num: Number): String {
        val str = if (num is Float || num is Double) {
            String.format(Locale.US, "%.1f", num.toDouble())
        } else {
            num.toString()
        }
        return str.map { ch ->
            if (ch in '0'..'9') BN_DIGITS[ch - '0'] else if (ch == '.') '.' else ch
        }.joinToString("")
    }

    fun toBnInt(num: Int): String {
        return num.toString().map { ch ->
            if (ch in '0'..'9') BN_DIGITS[ch - '0'] else ch
        }.joinToString("")
    }

    fun formatTemperatureBn(tempC: Float?): String {
        if (tempC == null || tempC <= 0) return NOT_AVAILABLE_BN
        return "${toBn(tempC)}°সে"
    }

    fun formatVoltageBn(voltageMv: Int?): String {
        if (voltageMv == null || voltageMv <= 0) return NOT_AVAILABLE_BN
        val volts = voltageMv / 1000.0f
        return "${toBn(volts)} V (${toBnInt(voltageMv)} mV)"
    }

    fun formatCurrentBn(currentMa: Int?): String {
        if (currentMa == null || currentMa == 0) return NOT_AVAILABLE_BN
        val sign = if (currentMa > 0) "+" else ""
        return "$sign${toBnInt(currentMa)} mA"
    }

    fun formatPowerBn(powerW: Float?): String {
        if (powerW == null || powerW <= 0) return NOT_AVAILABLE_BN
        return "${toBn(powerW)} W"
    }

    fun formatCapacityBn(capacityMah: Int?): String {
        if (capacityMah == null || capacityMah <= 0) return NOT_AVAILABLE_BN
        return "${toBnInt(capacityMah)} mAh"
    }

    fun formatDurationBn(seconds: Long?): String {
        if (seconds == null || seconds <= 0) return "গণনা করা হচ্ছে..."
        val mins = seconds / 60
        val hours = mins / 60
        val remainingMins = mins % 60
        return if (hours > 0) {
            "${toBnInt(hours.toInt())} ঘণ্টা ${toBnInt(remainingMins.toInt())} মিনিট"
        } else {
            "${toBnInt(mins.toInt())} মিনিট"
        }
    }

    fun getPluggedTypeBn(pluggedType: String): String {
        return when (pluggedType.lowercase()) {
            "ac" -> "এসি অ্যাডাপ্টার (দ্রুত চার্জার)"
            "usb" -> "ইউএসবি ক্যাবল"
            "wireless" -> "ওয়্যারলেস চার্জিং"
            else -> "সংযুক্ত নয়"
        }
    }

    fun getStatusBn(status: String, isCharging: Boolean): String {
        if (isCharging) return "চার্জ হচ্ছে"
        return when (status.lowercase()) {
            "full" -> "চার্জ সম্পূর্ণ"
            "discharging" -> "ডিসচার্জিং"
            "not_charging" -> "চার্জ হচ্ছে না"
            else -> "ব্যাটারি স্বাভাবিক"
        }
    }
}
