package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.TealAccent
import com.example.util.BengaliFormatters

@Composable
fun BatteryCanvasIndicator(
    level: Int,
    isCharging: Boolean,
    targetPercentage: Int,
    temperature: Float?,
    status: String,
    modifier: Modifier = Modifier
) {
    val clampedLevel = level.coerceIn(0, 100)

    val (primaryLiquidColor, secondaryLiquidColor) = when {
        isCharging -> EmeraldLight to EmeraldPrimary
        clampedLevel <= 20 -> RoseError to Color(0xFFBE123C)
        clampedLevel <= 45 -> AmberWarning to Color(0xFFD97706)
        else -> TealAccent to EmeraldPrimary
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveOffset"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("battery_canvas_indicator"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Battery Top Terminal (Anode)
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF94A3B8), Color(0xFFE2E8F0), Color(0xFF64748B))
                    )
                )
        )

        // Main 3D Glass Battery Container
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(230.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Slate900)
                .border(3.dp, Slate700, RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Background grid lines / segments inside battery
            Canvas(modifier = Modifier.matchParentSize()) {
                val segmentHeight = size.height / 4
                for (i in 1..3) {
                    val y = segmentHeight * i
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(16f, y),
                        end = Offset(size.width - 16f, y),
                        strokeWidth = 2f
                    )
                }
            }

            // Liquid Energy Fill (Canvas drawn with smooth gradient)
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                val totalH = size.height
                val fillH = totalH * (clampedLevel / 100f)
                val topY = totalH - fillH

                // Liquid fill
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryLiquidColor, secondaryLiquidColor),
                        startY = topY,
                        endY = totalH
                    ),
                    topLeft = Offset(0f, topY),
                    size = Size(size.width, fillH),
                    cornerRadius = CornerRadius(16f, 16f)
                )

                // Shimmer glow wave overlay
                if (fillH > 10f) {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.25f + 0.15f * waveOffset),
                                Color.Transparent
                            )
                        ),
                        topLeft = Offset(0f, topY),
                        size = Size(size.width, fillH)
                    )
                }

                // Target level dotted indicator line
                val targetY = totalH - (totalH * (targetPercentage / 100f))
                drawLine(
                    color = AmberWarning.copy(alpha = 0.85f),
                    start = Offset(0f, targetY),
                    end = Offset(size.width, targetY),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )

                // Outer glass inner rim highlight
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.12f),
                    topLeft = Offset(4f, 4f),
                    size = Size(size.width - 8f, size.height - 8f),
                    cornerRadius = CornerRadius(22f, 22f),
                    style = Stroke(width = 1.5f)
                )
            }

            // Target marker badge
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                val topPaddingRatio = (100 - targetPercentage) / 100f
                Column(
                    modifier = Modifier.padding(top = (230 * topPaddingRatio).coerceIn(12f, 200f).dp)
                ) {
                    Surface(
                        color = AmberWarning.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "লক্ষ্য ${BengaliFormatters.toBnInt(targetPercentage)}%",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate950,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            // Charging Animated Bolt in center
            if (isCharging) {
                Box(
                    modifier = Modifier
                        .matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Slate950.copy(alpha = 0.65f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.6f)),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Charging Bolt",
                                tint = AmberWarning,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bengali Percentage Numerical Display
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = BengaliFormatters.toBnInt(clampedLevel),
                fontSize = 46.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 46.sp
            )
            Text(
                text = "%",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = primaryLiquidColor,
                modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Status & Temperature Badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Charging/Discharging Status Badge
            Surface(
                color = if (isCharging) EmeraldPrimary.copy(alpha = 0.15f) else Slate800,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCharging) EmeraldPrimary.copy(alpha = 0.4f) else Slate700
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isCharging) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(14.dp)
                        )
                    } else if (clampedLevel <= 20) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RoseError,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = BengaliFormatters.getStatusBn(status, isCharging),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCharging) EmeraldLight else Slate200
                    )
                }
            }

            // Temperature Badge
            if (temperature != null && temperature > 0) {
                Surface(
                    color = if (temperature >= 40f) RoseError.copy(alpha = 0.15f) else Slate800,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (temperature >= 40f) RoseError.copy(alpha = 0.4f) else Slate700
                    )
                ) {
                    Text(
                        text = BengaliFormatters.formatTemperatureBn(temperature),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (temperature >= 40f) RoseError else Slate200,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
