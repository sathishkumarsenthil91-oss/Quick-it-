package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AiOrb
import com.example.ui.components.ClayBackground
import com.example.ui.components.ClayButton
import com.example.ui.components.ClayCard
import com.example.ui.components.QuickItFullLogo
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    ClayBackground {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Speeding Logo Presentation
            AiOrb(size = 130.dp)

            Spacer(modifier = Modifier.height(24.dp))

            // Full Quick it Brand Logo from image
            QuickItFullLogo(
                height = 54.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Text(
                text = "PERSONAL AI DIGITAL HUB",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalPurple,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "3D Claymorphism • Liquid Glass • Cloud Storage",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = RoyalPurple,
                strokeWidth = 2.5.dp
            )
        }
    }
}

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var pageIndex by remember { mutableIntStateOf(0) }

    val pages = remember {
        listOf(
            Triple(
                "Futuristic AI Companion",
                "Chat, search vault files with natural language, and summarize documents with our glowing neural AI mascot.",
                Icons.Filled.AutoAwesome
            ),
            Triple(
                "Smart Cloud Storage & Vault",
                "Store Photos, PDFs, Code, Videos, and Notes with permanent unique IDs (QK-*) and instant categorization.",
                Icons.Filled.CloudDone
            ),
            Triple(
                "Collaborative Hubs & Security",
                "Create private or public hubs with role-based permissions, zero-trust isolation, and cross-platform sync.",
                Icons.Filled.Security
            )
        )
    }

    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick it",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepViolet
                )

                TextButton(onClick = onFinish) {
                    Text(
                        text = "Skip",
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Center Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                AiOrb(size = 130.dp)

                Spacer(modifier = Modifier.height(32.dp))

                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(RoyalPurple, SpeedCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = pages[pageIndex].third,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = pages[pageIndex].first,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepViolet,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = pages[pageIndex].second,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Bottom Navigation & Indicators
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { i ->
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (i == pageIndex) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == pageIndex) RoyalPurple else SoftLavender
                                )
                        )
                    }
                }

                ClayButton(
                    onClick = {
                        if (pageIndex < pages.size - 1) {
                            pageIndex++
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_next_button")
                ) {
                    Text(
                        text = if (pageIndex == pages.size - 1) "Get Started" else "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
