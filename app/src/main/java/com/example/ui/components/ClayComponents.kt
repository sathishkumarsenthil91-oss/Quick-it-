package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * 3D Claymorphism + Liquid Glass Card Container
 * Features:
 * - 24–32dp rounded corners
 * - Dual layered pastel drop shadow
 * - Translucent milky frosted surface
 * - Delicate white inner/border highlight
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    backgroundColor: Color = GlassSurface,
    borderColor: Color = GlassBorder,
    elevation: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardPress"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .shadow(
                elevation = if (isPressed) elevation / 2 else elevation,
                shape = shape,
                ambientColor = ClayDarkShadow,
                spotColor = ClayRaisedShadow
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.92f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        borderColor,
                        Color.White.copy(alpha = 0.2f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 600f)
                ),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = RoyalPurple.copy(alpha = 0.15f)),
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        content()
    }
}

/**
 * Glossy 3D Pearlescent Action Button
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(RoyalPurple, DeepViolet),
    shape: Shape = RoundedCornerShape(22.dp),
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "btnPress"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .shadow(
                elevation = if (isPressed) 3.dp else 7.dp,
                shape = shape,
                spotColor = DeepViolet.copy(alpha = 0.45f)
            )
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) gradientColors else listOf(Color.LightGray, Color.Gray)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.65f), Color.Transparent)
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.25f)),
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

/**
 * Unique ID Display Chip with 1-Tap Copy Action
 */
@Composable
fun UniqueIdChip(
    displayId: String,
    onCopied: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current

    Surface(
        onClick = {
            clipboardManager.setText(AnnotatedString(displayId))
            onCopied(displayId)
        },
        shape = RoundedCornerShape(12.dp),
        color = SoftLavender.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(RoyalPurple)
            )
            Text(
                text = displayId,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DeepViolet,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Fluid Pastel Multi-layer Gradient Background for the Entire App
 */
@Composable
fun ClayBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Pure crystal pearl & soft translucent ice blue gradient (no pink or grey)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PearlWhite,
                            Color(0xFFF6F8FF),
                            IceBlue.copy(alpha = 0.30f),
                            Color(0xFFF9FAFF)
                        )
                    )
                )
                // Top-right soft lavender aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SoftLavender.copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.12f),
                        radius = size.width * 0.60f
                    )
                )
                // Bottom-left crystal ice cyan aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SpeedCyan.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(size.width * 0.15f, size.height * 0.85f),
                        radius = size.width * 0.65f
                    )
                )
            }
    ) {
        content()
    }
}
