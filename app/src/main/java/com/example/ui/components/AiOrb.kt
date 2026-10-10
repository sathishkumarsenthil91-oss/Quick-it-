package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.sin

/**
 * 3D Glowing Pearlescent AI Orb Mascot
 * Inspired by futuristic digital assistants:
 * - Floating hover physics
 * - Gentle ambient light breathing
 * - Translucent liquid glass surface with specular reflection
 * - Dynamic energy core
 */
@Composable
fun AiOrb(
    modifier: Modifier = Modifier,
    size: Dp = 130.dp,
    isThinking: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbMotion")

    // Vertical floating sine wave
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatAnim"
    )

    // Breathing aura pulse
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThinking) 800 else 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraAnim"
    )

    // Inner core rotation
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThinking) 2500 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coreRotation"
    )

    Box(
        modifier = modifier
            .size(size)
            .offset(y = floatOffset.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2f * 0.72f

            // 1. Ambient Soft Breathing Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PastelPurple.copy(alpha = 0.55f),
                        RoyalPurple.copy(alpha = 0.25f),
                        SpeedCyan.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.45f * auraScale
                ),
                radius = baseRadius * 1.45f * auraScale,
                center = center
            )

            // 2. Liquid Glass Outer Sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE0C3FC).copy(alpha = 0.95f),
                        PastelPurple,
                        RoyalPurple,
                        DeepViolet
                    ),
                    center = Offset(center.x - baseRadius * 0.25f, center.y - baseRadius * 0.3f),
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = center
            )

            // 3. Dynamic Luminous Inner Core (Rotating energy)
            val rad = Math.toRadians(rotationAngle.toDouble())
            val coreOffsetX = (sin(rad) * baseRadius * 0.2f).toFloat()
            val coreOffsetY = (kotlin.math.cos(rad) * baseRadius * 0.15f).toFloat()
            val innerCenter = Offset(center.x + coreOffsetX, center.y + coreOffsetY)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        SpeedCyan.copy(alpha = 0.85f),
                        RoyalPurple.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    center = innerCenter,
                    radius = baseRadius * 0.65f
                ),
                radius = baseRadius * 0.65f,
                center = innerCenter
            )

            // 4. Pearlescent Specular Glass Glint (Top left reflection)
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    start = Offset(center.x - baseRadius * 0.6f, center.y - baseRadius * 0.65f),
                    end = Offset(center.x, center.y - baseRadius * 0.2f)
                ),
                topLeft = Offset(center.x - baseRadius * 0.55f, center.y - baseRadius * 0.65f),
                size = androidx.compose.ui.geometry.Size(baseRadius * 0.65f, baseRadius * 0.35f)
            )

            // 5. Delicate Bottom Rim Light (Bounce light)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        SpeedCyan.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.45f)
                    ),
                    center = Offset(center.x, center.y + baseRadius * 0.85f),
                    radius = baseRadius * 0.4f
                ),
                radius = baseRadius,
                center = center
            )
        }
    }
}
