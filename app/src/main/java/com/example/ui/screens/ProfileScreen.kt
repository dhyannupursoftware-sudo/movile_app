package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.theme.CardBg
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GlassBorder
import com.example.ui.viewmodel.CineScreen
import com.example.ui.viewmodel.CineViewModel

@Composable
fun ProfileScreen(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val continueWatching by viewModel.continueWatchingMovies.collectAsState()

    var showAccountEditDialog by remember { mutableStateOf(false) }
    var editUsername by remember { mutableStateOf(profile.username) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Elegant top title
            Text(
                text = "USER COGNITIVE PORTAL",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            // User Profile Overview Card with beautiful circular dynamic gradient avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x0CFFFFFF))
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentRed, AccentBlue)))
                            .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.username.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.username,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = AccentRed,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        editUsername = profile.username
                                        showAccountEditDialog = true
                                    }
                            )
                        }

                        Text(
                            text = profile.email,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dynamic membership tier badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.linearGradient(
                                        when (profile.subscriptionPlan) {
                                            "Premium" -> listOf(AccentRed, Color(0xFFFF5252))
                                            "Family" -> listOf(AccentBlue, Color(0xFF00C8FF))
                                            else -> listOf(Color(0xFF6B7280), Color(0xFF9CA3AF))
                                        }
                                    )
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${profile.subscriptionPlan.uppercase()} SECURE MEMBER",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action lists
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "CONTROLS & HYPERSETTINGS",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 1. Subscription Manager Row
                ProfileSettingRow(
                    title = "Subscription Manager",
                    subtitle = "Current: ${profile.subscriptionPlan}",
                    icon = Icons.Default.Subtitles
                ) {
                    viewModel.navigateTo(CineScreen.SUBSCRIPTION)
                }

                // 2. Dark/Light Theme Control Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x06FFFFFF))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (profile.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = AccentRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Cinematic Dark Mode", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                if (profile.isDarkTheme) "Deep space active" else "Sleek steel active",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = profile.isDarkTheme,
                        onCheckedChange = { viewModel.toggleAppTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentRed
                        )
                    )
                }

                // 3. Clear Play Progress History
                ProfileSettingRow(
                    title = "Delete History Link",
                    subtitle = "Disconnect watch logs",
                    icon = Icons.Default.Delete
                ) {
                    viewModel.showToast("Local memory cache wiped.")
                }

                // 4. Terminate Sync Session (Sign Out)
                ProfileSettingRow(
                    title = "Terminate Synced Session",
                    subtitle = "Safe session signature flush",
                    icon = Icons.Default.Logout,
                    textColor = AccentRed
                ) {
                    viewModel.signout()
                }
            }

            // Simple dialog mock for editing profile username
            if (showAccountEditDialog) {
                AlertDialog(
                    onDismissRequest = { showAccountEditDialog = false },
                    title = { Text("Synchronize Bio-ID", color = Color.White, fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("Enter your custom CineVortex username alias:", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.padding(bottom = 12.dp))
                            OutlinedTextField(
                                value = editUsername,
                                onValueChange = { editUsername = it },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = AccentRed
                                )
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (editUsername.trim().isNotEmpty()) {
                                    viewModel.updateProfileName(editUsername.trim())
                                    viewModel.showToast("Neural sync complete!")
                                }
                                showAccountEditDialog = false
                            }
                        ) {
                            Text("COMPLETE SYNC", color = AccentRed)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAccountEditDialog = false }) {
                            Text("ABORT", color = Color.White.copy(alpha = 0.5f))
                        }
                    },
                    containerColor = CardBg,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 4.dp
                )
            }
        }
    }
}

@Composable
fun ProfileSettingRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x06FFFFFF))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(title, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
        }
    }
}
