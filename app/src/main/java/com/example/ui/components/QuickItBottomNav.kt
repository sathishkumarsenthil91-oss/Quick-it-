package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.*

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val testTag: String
)

@Composable
fun QuickItBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    onOpenSmartSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(AppScreen.DASHBOARD, "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_dashboard"),
        NavItem(AppScreen.STORAGE, "Vault", Icons.Filled.Folder, Icons.Outlined.Folder, "nav_storage"),
        // Center space reserved for Smart Save (+) button
        NavItem(AppScreen.AI_ASSISTANT, "Hubby AI", Icons.Filled.Psychology, Icons.Outlined.Psychology, "nav_ai"),
        NavItem(AppScreen.GROUPS, "Chats", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, "nav_chats")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Pill Translucent Liquid Frosted Glass Container (Pure Crystal, Zero Pink/Grey)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(36.dp),
                    ambientColor = Color.Transparent,
                    spotColor = DeepViolet.copy(alpha = 0.16f)
                )
                .clip(RoundedCornerShape(36.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.88f),
                            Color.White.copy(alpha = 0.68f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f),
                            SpeedCyan.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.80f)
                        )
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First 2 Nav Items
                items.take(2).forEach { item ->
                    NavButton(
                        item = item,
                        isSelected = currentScreen == item.screen,
                        onClick = { onNavigate(item.screen) }
                    )
                }

                // Center spacer for the raised floating + Save button
                Spacer(modifier = Modifier.width(56.dp))

                // Last 2 Nav Items
                items.takeLast(2).forEach { item ->
                    NavButton(
                        item = item,
                        isSelected = currentScreen == item.screen,
                        onClick = { onNavigate(item.screen) }
                    )
                }
            }
        }

        // Center Floating + Universal Smart Save Button
        Box(
            modifier = Modifier
                .offset(y = (-16).dp)
                .size(62.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = DeepViolet.copy(alpha = 0.6f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SpeedCyan, RoyalPurple, DeepViolet)
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.2f))
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                    onClick = onOpenSmartSave
                )
                .testTag("universal_smart_save_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Universal Smart Save",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun NavButton(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "navScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextSecondary,
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .scale(scaleAnim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(item.testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 42.dp else 36.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (item.screen != AppScreen.AI_ASSISTANT && isSelected) {
                        Brush.linearGradient(listOf(RoyalPurple, DeepViolet))
                    } else {
                        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.screen == AppScreen.AI_ASSISTANT) {
                HubbyAiMascot(
                    size = if (isSelected) 36.dp else 30.dp,
                    animateEyes = true
                )
            } else {
                Icon(
                    imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                    contentDescription = item.title,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Text(
            text = item.title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DeepViolet else TextSecondary
        )
    }
}
