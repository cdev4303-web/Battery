package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ui.ChargingAssistantApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BatteryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BatteryViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startForegroundServiceIfActive()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set native Jetpack Compose content immediately so UI displays
        setContent {
            MyApplicationTheme {
                ChargingAssistantApp(viewModel = viewModel)
            }
        }

        // Request POST_NOTIFICATIONS on Android 13+ (Tiramisu) smoothly
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } catch (_: Exception) {}
    }

    private fun startForegroundServiceIfActive() {
        try {
            if (viewModel.settingsManager.isServiceActive) {
                val serviceIntent = Intent(this, ChargingForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onResume() {
        super.onResume()
        try {
            viewModel.refreshBatteryOptimizationStatus()
        } catch (_: Exception) {}
    }
}
