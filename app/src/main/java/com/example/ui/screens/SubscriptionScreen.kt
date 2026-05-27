package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.viewmodel.CineViewModel

data class SubscriptionPlan(
    val name: String,
    val price: String,
    val resolution: String,
    val screens: String,
    val features: List<String>,
    val glowColor: Color,
    val isPopular: Boolean = false
)

@Composable
fun SubscriptionScreen(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val plans = listOf(
        SubscriptionPlan(
            name = "Basic",
            price = "$8.99/mo",
            resolution = "1080p FHD",
            screens = "1 Active Stream",
            features = listOf(
                "Standard 1080p stream projection",
                "Full catalog of futuristic releases",
                "Offline sync link (Standard cache)"
            ),
            glowColor = Color(0xFF6B7280)
        ),
        SubscriptionPlan(
            name = "Premium",
            price = "$14.99/mo",
            resolution = "4K Ultra HDR",
            screens = "4 Active Streams",
            features = listOf(
                "Immersive 4K UHD stream projection",
                "Dolby Atmos Spatial Audio synch",
                "Priority high-speed offline coordinates sync",
                "Beta holographic VR avatar unlocked"
            ),
            glowColor = AccentRed,
            isPopular = true
        ),
        SubscriptionPlan(
            name = "Family Plan",
            price = "$19.99/mo",
            resolution = "8K Master projection",
            screens = "6 Active Streams",
            features = listOf(
                "Futuristic 8K Master holographic projection",
                "Dolby Atmos Space surround sound",
                "Unlimited offline synchronization caches",
                "6 custom bio-link profile characters"
            ),
            glowColor = AccentBlue
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("subscription_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header containing top back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x1BFFFFFF))
                        .testTag("sub_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "SELECT MEMBERSHIP TIER",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            // Scrollable package items column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ENTER THE FUTURE OF STREAMING",
                    color = AccentRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Sync your timeline with superior resolution, immersive multichannel spatial surround audio, and responsive neural performance.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 6.dp, bottom = 24.dp, start = 12.dp, end = 12.dp)
                )

                plans.forEach { plan ->
                    PricingCard(plan = plan) {
                        viewModel.selectSubscription(plan.name)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun PricingCard(
    plan: SubscriptionPlan,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x0CFFFFFF))
            .border(
                1.dp,
                if (plan.isPopular) AccentRed else GlassBorder,
                RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        // Subtle background radial glow highlight
        Box(
            modifier = Modifier
                .size(150.dp)
                .blur(40.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(plan.glowColor.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
                .align(Alignment.TopEnd)
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = plan.name.uppercase(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = plan.resolution,
                        color = if (plan.isPopular) AccentRed else Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (plan.isPopular) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentRed)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DWELLER FAVORITE",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = plan.price,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = plan.screens,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.White.copy(alpha = 0.05f)
            )

            // Features lists
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                plan.features.forEach { feature ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(plan.glowColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = plan.glowColor,
                                modifier = Modifier.size(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = feature,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action checkout simulator
            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (plan.isPopular) AccentRed else Color(0x1EFFFFFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "SYNCHRONIZE TIER",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
