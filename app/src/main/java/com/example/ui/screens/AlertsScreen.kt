package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.BengaliFormatters
import com.example.viewmodel.SettingsUiState

@Composable
fun AlertsScreen(
    settings: SettingsUiState,
    onUpdateTarget: (Int) -> Unit,
    onUpdateLowBattery: (Int) -> Unit,
    onUpdateHighTemp: (Int) -> Unit,
    onToggleVoice: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onToggleAlertSetting: (String, Boolean) -> Unit,
    onTestVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "অ্যালার্ট ও সতর্কতা সেটিংস",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "বাংলা ভয়েস অ্যানাউন্সমেন্ট ও লক্ষ্যমাত্রা কাস্টমাইজ করুন",
                fontSize = 12.sp,
                color = Slate400
            )
        }

        // Target Percentage Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_percentage_card"),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "টার্গেট চার্জ লেভেল",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "নির্ধারিত মাত্রায় পৌঁছালে বাংলা ভয়েস সতর্ক করবে",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }

                        Surface(
                            color = AmberWarning.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${BengaliFormatters.toBnInt(settings.targetPercentage)}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberWarning,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = settings.targetPercentage.toFloat(),
                        onValueChange = { onUpdateTarget(it.toInt()) },
                        valueRange = 50f..100f,
                        steps = 9,
                        modifier = Modifier.testTag("target_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = AmberWarning,
                            activeTrackColor = AmberWarning,
                            inactiveTrackColor = Slate800
                        )
                    )

                    // Quick percentage chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(80, 85, 90, 100).forEach { pct ->
                            val isSelected = settings.targetPercentage == pct
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onUpdateTarget(pct) },
                                color = if (isSelected) AmberWarning.copy(alpha = 0.2f) else Slate800,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AmberWarning else Slate700
                                )
                            ) {
                                Text(
                                    text = "${BengaliFormatters.toBnInt(pct)}%",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AmberWarning else Slate400,
                                    modifier = Modifier
                                        .padding(vertical = 6.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice and Notification Master Toggles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingToggleRow(
                        icon = Icons.Default.RecordVoiceOver,
                        iconTint = EmeraldPrimary,
                        title = "বাংলা ভয়েস সতর্কতা",
                        description = "চার্জ ৮০%, ৯০%, ১০০% ও সংযোগ বিচ্ছিন্ন হলে বাংলায় বলবে",
                        checked = settings.voiceEnabled,
                        onCheckedChange = { onToggleVoice(it) },
                        testTag = "toggle_voice"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        icon = Icons.Default.Notifications,
                        iconTint = CyanInfo,
                        title = "সিস্টেম নোটিফিকেশন",
                        description = "অ্যান্ড্রয়েড নোটিফিকেশন বারে লাইভ স্ট্যাটাস ও পপআপ এলার্ট",
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { onToggleNotifications(it) },
                        testTag = "toggle_notifications"
                    )
                }
            }
        }

        // Granular Alert Switches
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "স্বতন্ত্র অ্যালার্ট ইভেন্ট",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    GranularToggleRow(
                        title = "৮০% এ পৌঁছালে সতর্কতা",
                        subtext = "ব্যাটারি সুস্থ রাখতে ৮০% এ চার্জার খোলার পরামর্শ",
                        checked = settings.alertOn80,
                        onCheckedChange = { onToggleAlertSetting("alert_on_80", it) }
                    )

                    GranularToggleRow(
                        title = "৯০% এ পৌঁছালে সতর্কতা",
                        subtext = "৯০% চার্জ অ্যালার্ট ঘোষণা",
                        checked = settings.alertOn90,
                        onCheckedChange = { onToggleAlertSetting("alert_on_90", it) }
                    )

                    GranularToggleRow(
                        title = "১০০% ফুল চার্জ সতর্কতা",
                        subtext = "চার্জ সম্পূর্ণ হলে অব্যাহত ভয়েস ও অ্যালার্ম",
                        checked = settings.alertOn100,
                        onCheckedChange = { onToggleAlertSetting("alert_on_100", it) }
                    )

                    GranularToggleRow(
                        title = "চার্জার সংযুক্ত সতর্কতা",
                        subtext = "\"চার্জার সংযুক্ত হয়েছে\" ঘোষণা",
                        checked = settings.alertOnPlugged,
                        onCheckedChange = { onToggleAlertSetting("alert_on_plugged", it) }
                    )

                    GranularToggleRow(
                        title = "চার্জার বিচ্ছিন্ন সতর্কতা",
                        subtext = "\"চার্জার খুলে নেওয়া হয়েছে\" ঘোষণা",
                        checked = settings.alertOnUnplugged,
                        onCheckedChange = { onToggleAlertSetting("alert_on_unplugged", it) }
                    )

                    GranularToggleRow(
                        title = "পুনরাবৃত্তি নোটিফিকেশন",
                        subtext = "টার্গেট পৌঁছানোর পর চার্জার না খোলা পর্যন্ত ২ মিনিট পর পুনরায় ডাকবে",
                        checked = settings.repeatReminders,
                        onCheckedChange = { onToggleAlertSetting("repeat_reminders", it) }
                    )
                }
            }
        }

        // Thermal Protection Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = RoseError,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "উচ্চ তাপমাত্রা অ্যালার্ট",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "অতিরিক্ত উত্তাপে সুরক্ষা সতর্কবাণী",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }
                        }

                        Switch(
                            checked = settings.highTempWarning,
                            onCheckedChange = { onToggleAlertSetting("high_temp_warning", it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = RoseError,
                                checkedTrackColor = RoseError.copy(alpha = 0.4f),
                                uncheckedThumbColor = Slate400,
                                uncheckedTrackColor = Slate800
                            )
                        )
                    }

                    if (settings.highTempWarning) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "সর্বোচ্চ সহনীয় সীমা:", fontSize = 12.sp, color = Slate400)
                            Text(
                                text = "${BengaliFormatters.toBnInt(settings.highTempThreshold)}°সে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoseError
                            )
                        }

                        Slider(
                            value = settings.highTempThreshold.toFloat(),
                            onValueChange = { onUpdateHighTemp(it.toInt()) },
                            valueRange = 38f..50f,
                            steps = 11,
                            colors = SliderDefaults.colors(
                                thumbColor = RoseError,
                                activeTrackColor = RoseError,
                                inactiveTrackColor = Slate800
                            )
                        )
                    }
                }
            }
        }

        // Test Voice Button
        item {
            Button(
                onClick = onTestVoice,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_voice_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                    Text(
                        text = "বাংলা ভয়েস ইঞ্জিন টেস্ট করুন",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Slate400
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = EmeraldLight,
                checkedTrackColor = EmeraldPrimary.copy(alpha = 0.5f),
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Slate800
            )
        )
    }
}

@Composable
fun GranularToggleRow(
    title: String,
    subtext: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = Slate400
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EmeraldLight,
                checkedTrackColor = EmeraldPrimary.copy(alpha = 0.5f),
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Slate800
            )
        )
    }
}
