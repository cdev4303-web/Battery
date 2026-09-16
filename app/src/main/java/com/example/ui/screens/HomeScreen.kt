package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryData
import com.example.ui.components.BatteryCanvasIndicator
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanInfo
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.TealAccent
import com.example.util.BengaliFormatters
import com.example.viewmodel.SettingsUiState

@Composable
fun HomeScreen(
    batteryData: BatteryData,
    settings: SettingsUiState,
    onToggleService: (Boolean) -> Unit,
    onSpeakTestPhrase: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "চার্জিং সহকারী",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Android ফোরগ্রাউন্ড সার্ভিস ও বাংলা ভয়েস",
                        fontSize = 12.sp,
                        color = Slate400
                    )
                }

                Surface(
                    color = if (settings.serviceActive) EmeraldPrimary.copy(alpha = 0.2f) else Slate800,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (settings.serviceActive) EmeraldPrimary.copy(alpha = 0.5f) else Slate700
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (settings.serviceActive) EmeraldPrimary else Slate400)
                        )
                        Text(
                            text = if (settings.serviceActive) "মনিটরিং চালু" else "মনিটরিং বন্ধ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (settings.serviceActive) EmeraldLight else Slate400
                        )
                    }
                }
            }
        }

        // Foreground Service Control Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("service_status_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.serviceActive) Slate900 else Slate900.copy(alpha = 0.8f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (settings.serviceActive) EmeraldPrimary.copy(alpha = 0.4f) else Slate800
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = if (settings.serviceActive) EmeraldPrimary.copy(alpha = 0.15f) else Slate800,
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (settings.serviceActive) EmeraldPrimary else Slate400,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "ফোরগ্রাউন্ড সার্ভিস মনিটরিং",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (settings.serviceActive)
                                    "স্ক্রিন বন্ধ থাকলেও চার্জ পর্যবেক্ষণ করবে"
                                else
                                    "ব্যাকগ্রাউন্ড নজরদারি বন্ধ আছে",
                                fontSize = 12.sp,
                                color = Slate400
                            )
                        }
                    }

                    Switch(
                        checked = settings.serviceActive,
                        onCheckedChange = { onToggleService(it) },
                        modifier = Modifier.testTag("service_toggle_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldLight,
                            checkedTrackColor = EmeraldPrimary.copy(alpha = 0.5f),
                            uncheckedThumbColor = Slate400,
                            uncheckedTrackColor = Slate800
                        )
                    )
                }
            }
        }

        // 3D Visual Battery Canvas Indicator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(20.dp)
            ) {
                BatteryCanvasIndicator(
                    level = batteryData.level,
                    isCharging = batteryData.isCharging,
                    targetPercentage = settings.targetPercentage,
                    temperature = batteryData.temperature,
                    status = batteryData.status
                )
            }
        }

        // Real-time Technical Metrics Grid
        item {
            Text(
                text = "রিয়েল-টাইম হার্ডওয়্যার মেট্রিক্স",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricTile(
                        icon = Icons.Default.Thermostat,
                        iconTint = AmberWarning,
                        title = "তাপমাত্রা",
                        value = BengaliFormatters.formatTemperatureBn(batteryData.temperature),
                        subtext = if ((batteryData.temperature ?: 0f) >= 40f) "উচ্চ উষ্ণতা" else "নিরাপদ মাত্রা",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        icon = Icons.Default.Bolt,
                        iconTint = CyanInfo,
                        title = "ভোল্টেজ",
                        value = BengaliFormatters.formatVoltageBn(batteryData.voltage),
                        subtext = "ব্যাটারি টার্মিনাল",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricTile(
                        icon = Icons.Default.ElectricMeter,
                        iconTint = EmeraldLight,
                        title = "কারেন্ট ফ্লো",
                        value = BengaliFormatters.formatCurrentBn(batteryData.current),
                        subtext = if (batteryData.isCharging) "চার্জ ইনপুট" else "ড্রেন রেট",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        icon = Icons.Default.Speed,
                        iconTint = TealAccent,
                        title = "পাওয়ার",
                        value = BengaliFormatters.formatPowerBn(batteryData.power),
                        subtext = "ওয়াট ওয়াটেজ",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricTile(
                        icon = Icons.Default.Power,
                        iconTint = EmeraldPrimary,
                        title = "চার্জার ধরন",
                        value = BengaliFormatters.getPluggedTypeBn(batteryData.pluggedType),
                        subtext = if (batteryData.isCharging) "সংযোগ সক্রিয়" else "সংযুক্ত নয়",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        icon = Icons.Default.Favorite,
                        iconTint = RoseError,
                        title = "স্বাস্থ্য ও ধরন",
                        value = batteryData.health,
                        subtext = batteryData.technology,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Bengali Voice Alert Test Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "বাংলা ভয়েস অ্যালার্ট পরীক্ষা",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "নিচের বাটনে ট্যাপ করে যেকোনো অ্যালার্টের বাংলা উচ্চারণ পরীক্ষা করুন:",
                        fontSize = 12.sp,
                        color = Slate400,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VoiceTestChip(
                                label = "🔌 চার্জার সংযোগ",
                                textBn = "চার্জার সংযুক্ত হয়েছে",
                                onClick = { onSpeakTestPhrase("চার্জার সংযুক্ত হয়েছে") },
                                modifier = Modifier.weight(1f)
                            )
                            VoiceTestChip(
                                label = "⚡ ৮০% চার্জ সতর্কতা",
                                textBn = "ব্যাটারি ৮০ শতাংশ হয়েছে। অনুগ্রহ করে চার্জার খুলে ফেলুন।",
                                onClick = { onSpeakTestPhrase("ব্যাটারি ৮০ শতাংশ হয়েছে। অনুগ্রহ করে চার্জার খুলে ফেলুন।") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VoiceTestChip(
                                label = "✅ ১০০% পূর্ণ চার্জ",
                                textBn = "চার্জ সম্পূর্ণ হয়েছে। ব্যাটারি ফুল চার্জ।",
                                onClick = { onSpeakTestPhrase("চার্জ সম্পূর্ণ হয়েছে। ব্যাটারি ফুল চার্জ।") },
                                modifier = Modifier.weight(1f)
                            )
                            VoiceTestChip(
                                label = "❌ চার্জার বিচ্ছিন্ন",
                                textBn = "চার্জার খুলে নেওয়া হয়েছে",
                                onClick = { onSpeakTestPhrase("চার্জার খুলে নেওয়া হয়েছে") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MetricTile(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, Slate800),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Slate400,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = Slate400
            )
        }
    }
}

@Composable
fun VoiceTestChip(
    label: String,
    textBn: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("voice_test_chip_${label.take(6)}"),
        color = Slate800,
        border = BorderStroke(1.dp, Slate700)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = EmeraldLight,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        )
    }
}
