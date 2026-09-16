package com.example

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class BengaliTTSHelper(context: Context) : TextToSpeech.OnInitListener {
    private val TAG = "BengaliTTSHelper"
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val pendingSpeechQueue = mutableListOf<String>()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            // Try bn-BD first, then bn-IN, then default bn
            val bnLocales = listOf(
                Locale("bn", "BD"),
                Locale("bn", "IN"),
                Locale("bn")
            )

            var matchedLocale: Locale? = null
            for (loc in bnLocales) {
                val res = tts?.isLanguageAvailable(loc)
                if (res == TextToSpeech.LANG_AVAILABLE || res == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    matchedLocale = loc
                    break
                }
            }

            if (matchedLocale != null) {
                tts?.language = matchedLocale
                Log.d(TAG, "Bengali TTS Locale set to: $matchedLocale")
            } else {
                Log.w(TAG, "Bengali TTS not explicitly installed, attempting generic bn")
                tts?.language = Locale("bn")
            }

            // Set speech pitch and rate
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(0.95f)

            isInitialized = true

            // Flush pending speech
            synchronized(pendingSpeechQueue) {
                for (text in pendingSpeechQueue) {
                    speakInternal(text)
                }
                pendingSpeechQueue.clear()
            }
        } else {
            Log.e(TAG, "TextToSpeech initialization failed with status: $status")
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return

        if (!isInitialized || tts == null) {
            synchronized(pendingSpeechQueue) {
                pendingSpeechQueue.add(text)
            }
            playToneFallback()
            return
        }

        speakInternal(text)
    }

    private fun speakInternal(text: String) {
        try {
            val params = Bundle()
            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_NOTIFICATION)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "alert_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error speaking text", e)
            playToneFallback()
        }
    }

    private fun playToneFallback() {
        try {
            val toneG = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
        } catch (e: Exception) {
            Log.w(TAG, "Tone fallback failed", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        }
    }
}
