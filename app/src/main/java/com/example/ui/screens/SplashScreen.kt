package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CineLogo
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkBg
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    var loadingProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val steps = 20
        for (i in 1..steps) {
            delay(90)
            loadingProgress = i.toFloat() / steps
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBg, Color(0xFF030712))
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic abstract mesh background line/glow effects
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .scale(pulseScale)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CineLogo(
                    logoSize = 100.dp,
                    animate = true,
                    showText = true,
                    subtitle = "ENTER THE FUTURE OF ENTERTAINMENT"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Premium loader
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(
                    progress = { loadingProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = AccentRed,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "INITIALIZING CINEMATIC HUD...",
                color = Color.White.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp
            )
        }
    }
}
