package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.theme.NeonRedGlow

@Composable
fun CineLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 48.dp,
    animate: Boolean = true,
    showText: Boolean = true,
    subtitle: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")
    val rotation by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val glowIntensity by if (animate) {
        infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    Row(
        modifier = modifier.testTag("cine_logo_row"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(logoSize)
                .graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                    clip = false
                },
            contentAlignment = Alignment.Center
        ) {
            // Glow effect behind the logo icon
            Box(
                modifier = Modifier
                    .size(logoSize * 0.9f)
                    .blur(16.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                AccentRed.copy(alpha = 0.5f * glowIntensity),
                                AccentBlue.copy(alpha = 0.4f * glowIntensity),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Drawing the combination of letter "C" and Play Button in custom canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2, size.height / 2)
                val radius = (size.width / 2) * 0.85f

                // 1. Draw glowing background accent arc (The stylized futuristic letter C)
                val pathC = Path().apply {
                    addArc(
                        oval = androidx.compose.ui.geometry.Rect(
                            center = centerOffset,
                            radius = radius
                        ),
                        startAngleDegrees = 45f,
                        sweepAngleDegrees = 270f
                    )
                }

                drawPath(
                    path = pathC,
                    brush = Brush.linearGradient(
                        colors = listOf(AccentRed, AccentBlue),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    ),
                    style = Stroke(
                        width = radius * 0.22f,
                        cap = StrokeCap.Round
                    )
                )

                // 2. Draw modern inside Play triangle facing right
                val playWidth = radius * 0.75f
                val playHeight = radius * 0.75f
                val startX = centerOffset.x - (playWidth * 0.28f)
                val startY = centerOffset.y

                val pathPlay = Path().apply {
                    moveTo(startX, startY - playHeight / 2) // Top left point
                    lineTo(startX + playWidth, startY) // Sharp right point
                    lineTo(startX, startY + playHeight / 2) // Bottom left point
                    close()
                }

                drawPath(
                    path = pathPlay,
                    brush = Brush.verticalGradient(
                        colors = listOf(AccentBlue, AccentRed)
                    )
                )
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CINE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = (logoSize.value * 0.45f).sp,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "VORTEX",
                        color = AccentRed,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = (logoSize.value * 0.45f).sp,
                        letterSpacing = 2.sp,
                        modifier = Modifier.drawBehind {
                            // Subtle blue text shadow glow
                            drawCircle(
                                color = AccentBlue.copy(alpha = 0.3f),
                                radius = size.height * 0.7f,
                                center = Offset(size.width / 2, size.height / 2)
                            )
                        }
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = (logoSize.value * 0.2f).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cornerRadius = 16.dp
    val cardModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick)
    } else {
        modifier
    }

    Box(
        modifier = cardModifier
            .graphicsLayer {
                clip = true
                shape = RoundedCornerShape(cornerRadius)
            }
            .background(Color(0x0CFFFFFF)) // Glassmorphism translucent layer
            .border(
                1.dp,
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.03f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        if (glowColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(20.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = 0.08f), Color.Transparent)
                        )
                    )
            )
        }
        Column {
            content()
        }
    }
}

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    glowColors: List<Color> = listOf(AccentRed.copy(alpha = 0.15f), AccentBlue.copy(alpha = 0.15f)),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glowing halo
        Box(
            modifier = Modifier
                .fillMaxSize(1.05f)
                .blur(16.dp)
                .background(
                    Brush.radialGradient(
                        colors = glowColors + Color.Transparent
                    )
                )
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = onClick != null) { onClick?.invoke() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF111827)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}
