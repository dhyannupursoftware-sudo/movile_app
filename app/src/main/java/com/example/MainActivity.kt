package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.DarkBg
import com.example.ui.viewmodel.CineScreen
import com.example.ui.viewmodel.CineViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CineViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()
            val userProfile by viewModel.userProfile.collectAsState()

            MyApplicationTheme(darkTheme = userProfile.isDarkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (userProfile.isDarkTheme) DarkBg else MaterialTheme.colorScheme.background)
                ) {
                    if (userProfile.isDarkTheme) {
                        // Decorative ambient light effects matching the HTML design guidelines
                        // Top-right Red atmospheric blur
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 100.dp, y = (-100).dp)
                                .size(300.dp)
                                .blur(100.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(AccentRed.copy(alpha = 0.08f), Color.Transparent)
                                    )
                                )
                        )
                        // Bottom-left Blue atmospheric blur
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = (-100).dp, y = 100.dp)
                                .size(300.dp)
                                .blur(100.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(AccentBlue.copy(alpha = 0.1f), Color.Transparent)
                                    )
                                )
                        )
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        bottomBar = {
                            // Show custom glowing nav bar on dashboard screens only
                            val showNavBar = uiState.currentScreen in listOf(
                                CineScreen.HOME, CineScreen.SEARCH, CineScreen.WATCHLIST, CineScreen.PROFILE
                            )
                            if (showNavBar) {
                                CustomGlowingBottomNavBar(
                                    currentScreen = uiState.currentScreen,
                                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    bottom = if (uiState.currentScreen in listOf(
                                            CineScreen.HOME, CineScreen.SEARCH, CineScreen.WATCHLIST, CineScreen.PROFILE
                                        )) 0.dp else innerPadding.calculateBottomPadding()
                                )
                        ) {
                            // Dynamic Animated Screen Routing Stack
                            AnimatedContent(
                                targetState = uiState.currentScreen,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(300))
                                },
                                label = "screen_navigation"
                            ) { screen ->
                                when (screen) {
                                    CineScreen.SPLASH -> SplashScreen()
                                    CineScreen.LOGIN -> LoginScreen(viewModel = viewModel)
                                    CineScreen.HOME -> HomeScreen(viewModel = viewModel)
                                    CineScreen.DETAIL -> DetailsScreen(viewModel = viewModel)
                                    CineScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                                    CineScreen.WATCHLIST -> WatchlistScreen(viewModel = viewModel)
                                    CineScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                                    CineScreen.SUBSCRIPTION -> SubscriptionScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }

                    // Floating toast notifications over everything
                    uiState.userNotificationMessage?.let { toastMsg ->
                        LaunchedEffect(toastMsg) {
                            delay(2500)
                            viewModel.dismissToast()
                        }
                        CineGlowToast(
                            message = toastMsg,
                            onDismiss = { viewModel.dismissToast() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 96.dp, start = 16.dp, end = 16.dp)
                        )
                    }

                    // Full Screen Immersive Video Stream Overlay (always overlays on top)
                    if (uiState.activeVideoPlayingId != null) {
                        VideoPlayerOverlay(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomGlowingBottomNavBar(
    currentScreen: CineScreen,
    onNavigate: (CineScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding() // STRICT MANDATE FOR GESTURE OVERLAPS
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing outline background block (Futuristic cinematic glass bar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xCC111827)) // 80% opacity dark slate matching design HTML bg-[#111827]/80
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.08f), AccentRed.copy(alpha = 0.20f), Color.White.copy(alpha = 0.08f))),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // HOME Tab
                CineNavBarItem(
                    selected = currentScreen == CineScreen.HOME,
                    iconSelected = Icons.Filled.Home,
                    iconUnselected = Icons.Outlined.Home,
                    label = "Core",
                    testTag = "nav_home_tab",
                    onClick = { onNavigate(CineScreen.HOME) }
                )

                // SEARCH Tab
                CineNavBarItem(
                    selected = currentScreen == CineScreen.SEARCH,
                    iconSelected = Icons.Filled.Search,
                    iconUnselected = Icons.Outlined.Search,
                    label = "Scan",
                    testTag = "nav_search_tab",
                    onClick = { onNavigate(CineScreen.SEARCH) }
                )

                // WATCHLIST Tab
                CineNavBarItem(
                    selected = currentScreen == CineScreen.WATCHLIST,
                    iconSelected = Icons.Filled.Bookmark,
                    iconUnselected = Icons.Outlined.BookmarkBorder,
                    label = "Theater",
                    testTag = "nav_watchlist_tab",
                    onClick = { onNavigate(CineScreen.WATCHLIST) }
                )

                // PROFILE Tab
                CineNavBarItem(
                    selected = currentScreen == CineScreen.PROFILE,
                    iconSelected = Icons.Filled.Person,
                    iconUnselected = Icons.Outlined.Person,
                    label = "Sync ID",
                    testTag = "nav_profile_tab",
                    onClick = { onNavigate(CineScreen.PROFILE) }
                )
            }
        }
    }
}

@Composable
fun CineNavBarItem(
    selected: Boolean,
    iconSelected: androidx.compose.ui.graphics.vector.ImageVector,
    iconUnselected: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .width(56.dp)
            .height(52.dp) // Comfortably support indicator dot
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            // Elegant glowing indicator dot at the top center
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(AccentRed)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .blur(5.dp)
                            .background(AccentRed.copy(alpha = 0.4f), CircleShape)
                    )
                }

                Icon(
                    imageVector = if (selected) iconSelected else iconUnselected,
                    contentDescription = label,
                    tint = if (selected) AccentRed else Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label.uppercase(),
                color = if (selected) AccentRed else Color.White.copy(alpha = 0.4f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun CineGlowToast(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 340.dp)
            .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable(onClick = onDismiss),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF2111827))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
        }
    }
}
