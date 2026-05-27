package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CineLogo
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.CardBg
import com.example.ui.theme.DarkBg
import com.example.ui.theme.NeonBlueGlow
import com.example.ui.theme.NeonRedGlow
import com.example.ui.viewmodel.CineViewModel

@Composable
fun LoginScreen(
    viewModel: CineViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "shapes")
    val animOffset1 by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )
    val animOffset2 by infiniteTransition.animateFloat(
        initialValue = 50f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("login_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Floating colorful halo gradient blobs for organic glassmorphism background
        Box(
            modifier = Modifier
                .offset(x = animOffset1.dp, y = animOffset2.dp)
                .size(300.dp)
                .align(Alignment.TopStart)
                .blur(80.dp)
                .background(Brush.radialGradient(listOf(NeonRedGlow.copy(alpha = 0.22f), Color.Transparent)))
        )

        Box(
            modifier = Modifier
                .offset(x = animOffset2.dp, y = animOffset1.dp)
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .blur(80.dp)
                .background(Brush.radialGradient(listOf(NeonBlueGlow.copy(alpha = 0.22f), Color.Transparent)))
        )

        // Main Login Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center
        ) {
            CineLogo(
                logoSize = 64.dp,
                animate = true,
                showText = true,
                subtitle = "ENTER THE FUTURE OF ENTERTAINMENT"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Glassmorphism login card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x0CFFFFFF))
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.02f))
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sign In",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "Access your cinematic timeline",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Username field
                    OutlinedTextField(
                        value = uiState.authUsernameInput,
                        onValueChange = { viewModel.updateAuthUsername(it) },
                        label = { Text("Cyber Username", color = Color.White.copy(alpha = 0.6f)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User", tint = AccentRed) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentRed,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            cursorColor = AccentRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email field
                    OutlinedTextField(
                        value = uiState.authEmailInput,
                        onValueChange = { viewModel.updateAuthEmail(it) },
                        label = { Text("Security Email", color = Color.White.copy(alpha = 0.6f)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = AccentBlue) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            cursorColor = AccentBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Premium auth button
                    Button(
                        onClick = { viewModel.submitLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "INITIALIZE CORE STREAMING",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.White.copy(alpha = 0.1f)
                        )
                        Text(
                            text = "SECURE SOCIAL ID",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.White.copy(alpha = 0.1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid-like socials row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SocialButton(
                            text = "Google",
                            color = AccentBlue.copy(alpha = 0.15f),
                            borderColor = AccentBlue.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.updateAuthUsername("Cyber_Voyager")
                            viewModel.updateAuthEmail("voyager@gmail.com")
                            viewModel.submitLogin()
                        }

                        SocialButton(
                            text = "NeuralID",
                            color = AccentRed.copy(alpha = 0.15f),
                            borderColor = AccentRed.copy(alpha = 0.4f),
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.updateAuthUsername("Synthetica_Neuron")
                            viewModel.updateAuthEmail("neuron@cinevortex.ai")
                            viewModel.submitLogin()
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Secure status text
                    Text(
                        text = "Encrypted utilizing SHA-512 Cinematic Keys. By signing in, you consent to standard neural synchronization agreements.",
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.35f),
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SocialButton(
    text: String,
    color: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}
