package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardBg
import com.example.ui.theme.DarkBg
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.viewmodel.CineViewModel
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerOverlay(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val allMovies by viewModel.allMovies.collectAsState()

    val movie = allMovies.find { it.id == uiState.activeVideoPlayingId } ?: return

    var activeQuality by remember { mutableStateOf("4K HDR Dolby Vision") }
    var showQualityMenu by remember { mutableStateOf(false) }

    // Simulating progress tick when movie is playing
    LaunchedEffect(uiState.activeVideoPlayingId, uiState.videoIsPaused) {
        while (uiState.activeVideoPlayingId != null && !uiState.videoIsPaused) {
            delay(1000)
            val currentProgress = uiState.videoPlayingProgress
            if (currentProgress < 1.0f) {
                viewModel.setVideoProgress(currentProgress + 0.005f)
            } else {
                viewModel.closeVideoPlayer()
                viewModel.showToast("Stream finalized!")
                break
            }
        }
    }

    // Audio Visualizer spectrum tickers
    val infiniteTransition = rememberInfiniteTransition(label = "audio_spec")
    val heightsRatio = List(12) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.15f + (i % 3) * 0.2f,
            targetValue = 0.85f - (i % 2) * 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(200 + i * 110, easing = EaseInOutBounce),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$i"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE6030712)) // Dark translucent backdrop
            .testTag("video_player_overlay")
    ) {
        // Futuristic Glowing Halo behind video
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(360.dp)
                .blur(90.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AccentRed.copy(alpha = 0.14f),
                            AccentBlue.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Screen Header: Back, movie title, codec info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.closeVideoPlayer() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .testTag("video_player_close")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close stream", tint = Color.White)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "STREAMING IN PROJECTION ZONE",
                        color = AccentRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                // Video Codec Resolution pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .clickable { showQualityMenu = !showQualityMenu }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activeQuality,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Quality Selection Dropdown Menu
            if (showQualityMenu) {
                Card(
                    modifier = Modifier
                        .align(Alignment.End)
                        .width(180.dp)
                        .padding(top = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Column {
                        listOf("4K HDR Dolby Vision", "1080p FHD (Medium)", "720p Mobile (Low)").forEach { q ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeQuality = q
                                        showQualityMenu = false
                                        viewModel.showToast("Stream modified to $q")
                                    }
                                    .padding(14.dp)
                            ) {
                                Text(q, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Immersive Video Center: Animated Audio Spectrum Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!uiState.videoIsPaused) {
                    // Draw customized visual responsive lines in Canvas representing soundtrack beats
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val spacing = size.width / (heightsRatio.size + 1)
                        val startY = size.height / 2

                        for (i in heightsRatio.indices) {
                            val ratio = heightsRatio[i].value
                            val barHeight = size.height * 0.70f * ratio
                            val x = spacing * (i + 1)

                            // Linear gradient colors from electric red to cyan blue
                            val gradientBrush = Brush.verticalGradient(
                                colors = listOf(AccentRed, AccentBlue, AccentRed)
                            )

                            // Top line segment
                            drawLine(
                                brush = gradientBrush,
                                start = Offset(x, startY + barHeight / 2),
                                end = Offset(x, startY - barHeight / 2),
                                strokeWidth = size.width * 0.015f,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                    }
                } else {
                    Icon(
                        Icons.Default.PauseCircleFilled,
                        contentDescription = "Paused stream",
                        tint = AccentRed.copy(alpha = 0.5f),
                        modifier = Modifier.size(96.dp)
                    )
                }
            }

            // HUD Bottom Section: Scrub bar and dynamic buttons
            Column(modifier = Modifier.fillMaxWidth()) {
                // Time Duration Ticks
                val totalSeconds = 2 * 3600 + 15 * 60 // 2h 15m
                val watchedSeconds = (totalSeconds * uiState.videoPlayingProgress).toInt()

                fun formatTime(sec: Int): String {
                    val h = sec / 3600
                    val m = (sec % 3600) / 60
                    val s = sec % 60
                    return String.format("%02d:%02d:%02d", h, m, s)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(watchedSeconds),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "VORTEX 4K HDR BUFFER: SECURE",
                        color = ToneAlphaColor(Color.White, 0.4f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = formatTime(totalSeconds),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Custom scrub slider
                Slider(
                    value = uiState.videoPlayingProgress,
                    onValueChange = { viewModel.setVideoProgress(it) },
                    colors = SliderDefaults.colors(
                        thumbColor = AccentRed,
                        activeTrackColor = AccentRed,
                        inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("video_scrub_bar")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stream audio and video controllers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Backward button
                    IconButton(
                        onClick = { viewModel.setVideoProgress(uiState.videoPlayingProgress - 0.05f) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Replay10, contentDescription = "Backward 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    // Play/Pause circular primary button
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(AccentRed)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .clickable { viewModel.toggleVideoPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.videoIsPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Play/Pause toggle",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(28.dp))

                    // Forward button
                    IconButton(
                        onClick = { viewModel.setVideoProgress(uiState.videoPlayingProgress + 0.05f) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ToneAlphaColor(color: Color, alpha: Float): Color {
    return color.copy(alpha = alpha)
}
