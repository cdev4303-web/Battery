package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.ChargingScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.viewmodel.BatteryViewModel

enum class AssistantTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("হোম", Icons.Default.BatteryChargingFull, "nav_home"),
    CHARGING("চার্জিং", Icons.Default.Bolt, "nav_charging"),
    ALERTS("অ্যালার্ট", Icons.Default.Notifications, "nav_alerts"),
    DIAGNOSTICS("ডায়াগনস্টিক", Icons.Default.Tune, "nav_diagnostics")
}

@Composable
fun ChargingAssistantApp(
    viewModel: BatteryViewModel,
    modifier: Modifier = Modifier
) {
    val batteryData by viewModel.batteryData.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val historyList by viewModel.chargingHistory.collectAsState()

    var currentTab by remember { mutableStateOf(AssistantTab.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate950,
        bottomBar = {
            NavigationBar(
                containerColor = Slate900,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                AssistantTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag(tab.tag),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Slate950,
                            selectedTextColor = EmeraldLight,
                            indicatorColor = EmeraldLight,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AssistantTab.HOME -> HomeScreen(
                    batteryData = batteryData,
                    settings = settings,
                    onToggleService = { viewModel.toggleForegroundService(it) },
                    onSpeakTestPhrase = { viewModel.speakPhrase(it) }
                )
                AssistantTab.CHARGING -> ChargingScreen(
                    batteryData = batteryData,
                    historyList = historyList
                )
                AssistantTab.ALERTS -> AlertsScreen(
                    settings = settings,
                    onUpdateTarget = { viewModel.updateTargetPercentage(it) },
                    onUpdateLowBattery = { viewModel.updateLowBatteryWarning(it) },
                    onUpdateHighTemp = { viewModel.updateHighTempThreshold(it) },
                    onToggleVoice = { viewModel.toggleVoice(it) },
                    onToggleNotifications = { viewModel.toggleNotifications(it) },
                    onToggleAlertSetting = { key, value -> viewModel.toggleAlertSetting(key, value) },
                    onTestVoice = {
                        viewModel.speakPhrase("ব্যাটারি ৮০ শতাংশ হয়েছে। অনুগ্রহ করে চার্জার খুলে ফেলুন।")
                    }
                )
                AssistantTab.DIAGNOSTICS -> DiagnosticsScreen(
                    settings = settings,
                    onRefresh = { viewModel.refreshBatteryOptimizationStatus() }
                )
            }
        }
    }
}
