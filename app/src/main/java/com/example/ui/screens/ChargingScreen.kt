package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.model.BatteryData
import com.example.model.ChargingSessionItem
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanInfo
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.BengaliFormatters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChargingScreen(
    batteryData: BatteryData,
    historyList: List<ChargingSessionItem>,
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
                text = "চার্জিং ও ব্যাটারি স্বাস্থ্য",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "স্মার্ট চার্জিং সেশন ট্র্যাকিং ও দীর্ঘস্থায়ী ব্যাটারি টিপস",
                fontSize = 12.sp,
                color = Slate400
            )
        }

        // Active Session Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_session_card"),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                border = BorderStroke(
                    1.dp,
                    if (batteryData.isCharging) EmeraldPrimary.copy(alpha = 0.5f) else Slate800
                ),
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
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = null,
                                tint = if (batteryData.isCharging) EmeraldLight else Slate400,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (batteryData.isCharging) "বর্তমান চার্জিং সেশন" else "চার্জার সংযুক্ত নেই",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            color = if (batteryData.isCharging) EmeraldPrimary.copy(alpha = 0.2f) else Slate800,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${BengaliFormatters.toBnInt(batteryData.level)}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (batteryData.isCharging) EmeraldLight else Slate400,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "চার্জারের প্রকার", fontSize = 11.sp, color = Slate400)
                            Text(
                                text = BengaliFormatters.getPluggedTypeBn(batteryData.pluggedType),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "চার্জ পূর্ণ হতে সময়", fontSize = 11.sp, color = Slate400)
                            Text(
                                text = BengaliFormatters.formatDurationBn(batteryData.chargingTimeRemainingSec),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanInfo
                            )
                        }
                    }
                }
            }
        }

        // Battery Preservation Rules (The 20-80% Rule)
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
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "কেন ৮০% এ চার্জ থামাবেন?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PreservationPoint(
                        icon = Icons.Default.CheckCircle,
                        iconTint = EmeraldLight,
                        title = "২০% – ৮০% গোল্ডেন রুল",
                        description = "লিথিয়াম-আয়ন ব্যাটারিতে ৮০% এর উপরে চার্জ করলে ক্যাথোড ও অ্যানোডের ওপর অতিরিক্ত ভোল্টেজ চাপ পড়ে। ৮০% এ সীমাবদ্ধ রাখলে ব্যাটারির আয়ু ২-৩ গুণ পর্যন্ত বাড়ে।"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PreservationPoint(
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = RoseError,
                        title = "উচ্চ তাপমাত্রা থেকে রক্ষা",
                        description = "চার্জের সময় ৪০°সে এর উপরে গরম হলে ইলেকট্রোলাইটের ক্ষতি হয়। ভারী কভার খুলে চার্জ করুন এবং চার্জের সময় গেম খেলবেন না।"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PreservationPoint(
                        icon = Icons.Default.Lightbulb,
                        iconTint = AmberWarning,
                        title = "সারারাত চার্জে না রাখা",
                        description = "সারারাত প্লাগ করে রাখলে ট্রিকল চার্জিং এবং মাইক্রো-সাইক্লিংয়ের কারণে ব্যাটারির সেল ক্ষয়প্রাপ্ত হয়। বাংলা ভয়েস সতর্কতায় চার্জার খুলে ফেলুন।"
                    )
                }
            }
        }

        // Charging History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "সাম্প্রতিক চার্জিং ইতিহাস",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "${BengaliFormatters.toBnInt(historyList.size)} টি সেশন",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
        }

        if (historyList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    border = BorderStroke(1.dp, Slate800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Slate700,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "কোনো চার্জিং সেশন রেকর্ড নেই",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate400
                        )
                        Text(
                            text = "চার্জার লাগালে এবং চার্জ শেষে খুলে নিলে স্বয়ংক্রিয়ভাবে সেশন সংরক্ষণ হবে।",
                            fontSize = 11.sp,
                            color = Slate400,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(historyList, key = { it.id }) { session ->
                SessionHistoryItem(session = session)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PreservationPoint(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Slate400,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun SessionHistoryItem(session: ChargingSessionItem) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(session.startTime))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, Slate800),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = dateStr,
                    fontSize = 12.sp,
                    color = Slate400
                )
                Text(
                    text = "${BengaliFormatters.toBnInt(session.startLevel)}% হতে ${BengaliFormatters.toBnInt(session.endLevel ?: session.startLevel)}% পর্যন্ত",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Surface(
                color = EmeraldPrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${BengaliFormatters.toBnInt(session.durationMinutes ?: 1)} মিনিট",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldLight,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
