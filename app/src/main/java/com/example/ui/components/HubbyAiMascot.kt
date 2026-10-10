package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Hubby AI Animated Mascot
 * Based directly on user's uploaded image:
 * - Glowing electric blue/cyan rounded speech-bubble body with gentle neon aura
 * - Two vertical pill-shaped white eyes
 * - Dynamic eye animation: smoothly looks left, center, right, and blinks!
 */
@Composable
fun HubbyAiMascot(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    animateEyes: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hubbyAiAnim")

    // Gentle floating bob
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatBob"
    )

    // Eye look animation: Look Left (-1f) -> Center (0f) -> Look Right (+1f) -> Center (0f)
    val eyeLookX by if (animateEyes) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 5000
                    0.0f at 0
                    0.0f at 1200 // Look center
                    -1.0f at 1800 using FastOutSlowInEasing // Look left
                    -1.0f at 2800 // Hold left
                    0.0f at 3200 using FastOutSlowInEasing // Center
                    1.0f at 3800 using FastOutSlowInEasing // Look right
                    1.0f at 4600 // Hold right
                    0.0f at 5000 using FastOutSlowInEasing // Center
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "eyeLookX"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // Occasional quick blink animation (scale eye height down to 0.1 briefly)
    val eyeBlinkScale by if (animateEyes) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 3500
                    1.0f at 0
                    1.0f at 3000
                    0.15f at 3100 using FastOutLinearInEasing // blink close
                    1.0f at 3200 using LinearOutSlowInEasing // blink open
                    1.0f at 3500
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "eyeBlink"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    // Glowing aura pulse
    val auraGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraGlow"
    )

    Box(
        modifier = modifier
            .size(size)
            .offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val centerX = w * 0.48f
            val centerY = h * 0.46f
            val bubbleW = w * 0.74f
            val bubbleH = h * 0.68f

            // 1. Exterior Soft Blue Neon Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF38BDF8).copy(alpha = 0.45f * auraGlow),
                        Color(0xFF00B4D8).copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = bubbleW * 0.85f
                ),
                radius = bubbleW * 0.85f,
                center = Offset(centerX, centerY)
            )

            // 2. Speech Bubble Body with Speech Tail on Bottom-Right
            val bubblePath = Path().apply {
                val left = centerX - bubbleW / 2f
                val top = centerY - bubbleH / 2f
                val right = centerX + bubbleW / 2f
                val bottom = centerY + bubbleH / 2f
                val radius = bubbleW * 0.38f

                // Rounded Rect for main bubble head
                addRoundRect(
                    RoundRect(
                        left = left,
                        top = top,
                        right = right,
                        bottom = bottom,
                        cornerRadius = CornerRadius(radius, radius)
                    )
                )

                // Speech Tail pointing bottom-right
                moveTo(right - radius * 0.4f, bottom - radius * 0.1f)
                lineTo(right + radius * 0.25f, bottom + radius * 0.45f)
                lineTo(right - radius * 0.95f, bottom)
                close()
            }

            // Draw bubble with gradient from vibrant cyan to bright azure blue
            drawPath(
                path = bubblePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF60A5FA), // bright cyan-blue top
                        Color(0xFF3B82F6), // rich electric blue
                        Color(0xFF2563EB)  // deeper bottom
                    ),
                    startY = centerY - bubbleH / 2f,
                    endY = centerY + bubbleH / 2f + 20f
                )
            )

            // Subtle inner light highlight on top edge
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    startY = centerY - bubbleH / 2f,
                    endY = centerY - bubbleH * 0.1f
                ),
                topLeft = Offset(centerX - bubbleW * 0.42f, centerY - bubbleH * 0.45f),
                size = Size(bubbleW * 0.84f, bubbleH * 0.35f),
                cornerRadius = CornerRadius(bubbleW * 0.3f, bubbleW * 0.3f)
            )

            // 3. Two Vertical White Pill Eyes with Looking Animation & Blink
            val eyeWidth = bubbleW * 0.16f
            val baseEyeHeight = bubbleH * 0.44f
            val currentEyeHeight = baseEyeHeight * eyeBlinkScale
            val eyeSpacing = bubbleW * 0.18f

            val maxLookShift = bubbleW * 0.08f
            val eyeShiftX = eyeLookX * maxLookShift

            val leftEyeCenterX = centerX - eyeSpacing + eyeShiftX
            val rightEyeCenterX = centerX + eyeSpacing + eyeShiftX
            val eyeCenterY = centerY

            val eyeCornerRadius = CornerRadius(eyeWidth / 2f, eyeWidth / 2f)

            // Left Eye
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(
                    leftEyeCenterX - eyeWidth / 2f,
                    eyeCenterY - currentEyeHeight / 2f
                ),
                size = Size(eyeWidth, currentEyeHeight),
                cornerRadius = eyeCornerRadius
            )

            // Right Eye
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(
                    rightEyeCenterX - eyeWidth / 2f,
                    eyeCenterY - currentEyeHeight / 2f
                ),
                size = Size(eyeWidth, currentEyeHeight),
                cornerRadius = eyeCornerRadius
            )
        }
    }
}
