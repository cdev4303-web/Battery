package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanInfo
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.viewmodel.SettingsUiState

@Composable
fun DiagnosticsScreen(
    settings: SettingsUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "সিস্টেম ডায়াগনস্টিকস",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Android ফোরগ্রাউন্ড সার্ভিস ও পারমিশন নিরীক্ষা",
                fontSize = 12.sp,
                color = Slate400
            )
        }

        // Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "সিস্টেম কম্পোনেন্ট স্থিতি",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    DiagnosticRow(
                        title = "ফোরগ্রাউন্ড সার্ভিস",
                        description = "Android স্পেশাল-ইউজ ব্যাকগ্রাউন্ড প্রক্রিয়া",
                        isOk = settings.serviceActive,
                        okText = "সক্রিয়",
                        failText = "বন্ধ"
                    )

                    DiagnosticRow(
                        title = "নোটিফিকেশন চ্যানেল",
                        description = "সিস্টেম স্ট্যাটাস বার এলার্ট",
                        isOk = settings.notificationsEnabled,
                        okText = "চালু",
                        failText = "বন্ধ"
                    )

                    DiagnosticRow(
                        title = "ব্যাটারি অপ্টিমাইজেশন",
                        description = "Doze মোডে সার্ভিস চালু রাখা",
                        isOk = settings.isBatteryOptimizationIgnored,
                        okText = "অব্যাহতি প্রাপ্ত",
                        failText = "প্রয়োজন হতে পারে"
                    )

                    DiagnosticRow(
                        title = "বাংলা TTS ইঞ্জিন",
                        description = "TextToSpeech ভয়েস মডিউল",
                        isOk = settings.voiceEnabled,
                        okText = "সক্রিয়",
                        failText = "বন্ধ"
                    )
                }
            }
        }

        // System Settings Shortcuts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(1.dp, Slate800),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ডিভাইস সেটিংস শর্টকাট",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "নিরবচ্ছিন্ন নজরদারির জন্য সিস্টেম সেটিংস পরীক্ষা করুন:",
                        fontSize = 12.sp,
                        color = Slate400,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    try {
                                        context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                                    } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shortcut_battery_optimization"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Slate700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
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
                                        imageVector = Icons.Default.BatteryAlert,
                                        contentDescription = null,
                                        tint = AmberWarning,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(text = "ব্যাটারি অপ্টিমাইজেশন বন্ধ করুন", fontSize = 13.sp)
                                }
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shortcut_notifications"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Slate700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
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
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = CyanInfo,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(text = "অ্যাপ নোটিফিকেশন সেটিংস", fontSize = 13.sp)
                                }
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_POWER_USAGE_SUMMARY))
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shortcut_power_usage"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Slate700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
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
                                        imageVector = Icons.Default.Power,
                                        contentDescription = null,
                                        tint = EmeraldLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(text = "ডিভাইসের আসল ব্যাটারি ইউসেজ", fontSize = 13.sp)
                                }
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Architecture Information Card
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
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "আর্কিটেকচার ও নির্ভরযোগ্যতা",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• 100% Native Jetpack Compose UI (কোনো ওয়েবভিউ বা ক্রোমিয়াম ক্র্যাশ নেই)\n" +
                                "• Android BatteryManager সরাসরি হার্ডওয়্যার থেকে ভোল্টেজ, কারেন্ট, তাপমাত্রা পড়ে\n" +
                                "• Android 14+ বিশেষ Foreground Service (SpecialUse subtype) ব্যাকগ্রাউন্ড প্রসেস নিরাপত্তা প্রদান করে\n" +
                                "• গুগল টেক্সট-টু-স্পিচ (TTS) বাংলা ভাষার ভয়েস মডেল (bn-BD / bn-IN) ব্যবহার করে",
                        fontSize = 12.sp,
                        color = Slate400,
                        lineHeight = 18.sp
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
fun DiagnosticRow(
    title: String,
    description: String,
    isOk: Boolean,
    okText: String,
    failText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Slate400
            )
        }

        Surface(
            color = if (isOk) EmeraldPrimary.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                if (isOk) EmeraldPrimary.copy(alpha = 0.5f) else AmberWarning.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = if (isOk) okText else failText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOk) EmeraldLight else AmberWarning,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
