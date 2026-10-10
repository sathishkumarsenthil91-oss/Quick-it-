package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.AppScreen
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    user: UserProfile,
    storageItems: List<StorageItem>,
    folders: List<FolderItem>,
    onNavigate: (AppScreen) -> Unit,
    onOpenFileDetail: (StorageItem) -> Unit,
    onOpenFolderDetail: (FolderItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onCopyId: (String) -> Unit
) {
    val totalBytes = user.totalStorageBytes
    val usedBytes = storageItems.sumOf { it.sizeBytes } + 28L * 1024 * 1024 * 1024 // base OS vault
    val usagePercent = (usedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0.05f, 1f)

    ClayBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Top Header: Logo, User Greeting & User Unique ID Chip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Quick it authentic speeding Q mark
                        QuickItMark(size = 46.dp)

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Hello, ${user.name.split(" ").first()}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepViolet
                                )
                                UniqueIdChip(
                                    displayId = user.displayId,
                                    onCopied = onCopyId
                                )
                            }
                            Text(
                                text = "Personal AI Digital Hub",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Profile Avatar Pill
                    Surface(
                        onClick = { onNavigate(AppScreen.PROFILE) },
                        shape = CircleShape,
                        color = SoftLavender,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, RoyalPurple),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.avatarInitials,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepViolet
                            )
                        }
                    }
                }
            }

            // 2. Interactive AI Assistant Orb Hero Card
            item {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    backgroundColor = Color.White.copy(alpha = 0.88f),
                    elevation = 12.dp,
                    onClick = { onNavigate(AppScreen.AI_ASSISTANT) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftLavender.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Text(
                                    text = "✦ HUBBY AI CORE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = "Ask Hubby AI anything",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepViolet
                            )

                            Text(
                                text = "Search files, summarize PDFs, or generate code",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Chat with Hubby AI",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple
                                )
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = RoyalPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Hubby AI Animated Mascot with looking eyes!
                        HubbyAiMascot(size = 85.dp, animateEyes = true)
                    }
                }
            }

            // 3. Floating Glass Storage Summary Card
            item {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = Color.White.copy(alpha = 0.9f),
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(SoftLavender),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudQueue,
                                        contentDescription = "Cloud Storage",
                                        tint = RoyalPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "My Cloud Storage",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepViolet
                                )
                            }

                            Text(
                                text = "${"%.1f".format(usedBytes / 1_000_000_000f)} GB / 100 GB",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple
                            )
                        }

                        // Glossy Multi-Segment Progress Indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SoftLavender.copy(alpha = 0.5f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(usagePercent)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(SpeedCyan, RoyalPurple, DeepViolet)
                                        )
                                    )
                            )
                        }

                        // Category Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StorageCategoryPill("Photos", "${storageItems.count { it.category == FileCategory.PHOTOS }} files", SpeedCyan)
                            StorageCategoryPill("PDFs", "${storageItems.count { it.category == FileCategory.PDFS }} files", RoyalPurple)
                            StorageCategoryPill("Code", "${storageItems.count { it.category == FileCategory.CODE }} files", DeepViolet)
                            StorageCategoryPill("Prompts", "${storageItems.count { it.category == FileCategory.AI_PROMPTS }} files", PastelPurple)
                        }
                    }
                }
            }

            // 4. Quick Modules Shortcuts (Learning Hub, Coding Projects, Prompts, Notes, Planner)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Digital Hub Modules",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        ModuleQuickCard(
                            title = "AI Prompts",
                            subtitle = "3 Saved Chains",
                            icon = Icons.Filled.Psychology,
                            gradient = listOf(RoyalPurple, DeepViolet),
                            onClick = { onNavigate(AppScreen.PROMPTS) }
                        )
                    }
                    item {
                        ModuleQuickCard(
                            title = "Personal Notes",
                            subtitle = "Rich Docs & Ideas",
                            icon = Icons.Filled.EditNote,
                            gradient = listOf(PastelPurple, RoyalPurple),
                            onClick = { onNavigate(AppScreen.NOTES) }
                        )
                    }
                    item {
                        ModuleQuickCard(
                            title = "Daily Planner",
                            subtitle = "Tasks & Reminders",
                            icon = Icons.Filled.CheckCircle,
                            gradient = listOf(SpeedCyan, RoyalPurple),
                            onClick = { onNavigate(AppScreen.PLANNER) }
                        )
                    }
                    item {
                        ModuleQuickCard(
                            title = "Coding Projects",
                            subtitle = "GitHub & Snippets",
                            icon = Icons.Filled.Code,
                            gradient = listOf(DeepViolet, Color(0xFF1E1B4B)),
                            onClick = { onNavigate(AppScreen.CODING_PROJECTS) }
                        )
                    }
                    item {
                        ModuleQuickCard(
                            title = "Learning Hub",
                            subtitle = "Interactive Paths",
                            icon = Icons.Filled.School,
                            gradient = listOf(Color(0xFF00B4D8), RoyalPurple),
                            onClick = { onNavigate(AppScreen.LEARNING_HUB) }
                        )
                    }
                }
            }

            // 5. Favorite Folders Carousel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Favorite Folders",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet
                    )

                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple,
                        modifier = Modifier.clickable { onNavigate(AppScreen.STORAGE) }
                    )
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(folders.filter { it.isFavorite }) { folder ->
                        FolderCard(
                            folder = folder,
                            onClick = { onOpenFolderDetail(folder) },
                            onCopyId = onCopyId
                        )
                    }
                }
            }

            // 6. Recent Files Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Vault Items",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet
                    )

                    Text(
                        text = "${storageItems.size} files",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            items(storageItems.take(4)) { file ->
                FileItemRow(
                    item = file,
                    onClick = { onOpenFileDetail(file) },
                    onToggleFavorite = { onToggleFavorite(file.id) },
                    onCopyId = onCopyId
                )
            }
        }
    }
}

@Composable
private fun StorageCategoryPill(name: String, count: String, dotColor: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        Text(text = count, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
private fun ModuleQuickCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    ClayCard(
        modifier = Modifier
            .width(136.dp)
            .height(110.dp),
        shape = RoundedCornerShape(22.dp),
        onClick = onClick,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepViolet
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun FolderCard(
    folder: FolderItem,
    onClick: () -> Unit,
    onCopyId: (String) -> Unit,
    isSelected: Boolean = false
) {
    ClayCard(
        modifier = Modifier
            .width(155.dp)
            .height(130.dp),
        shape = RoundedCornerShape(22.dp),
        onClick = onClick,
        borderColor = if (isSelected) RoyalPurple else GlassBorderPurple,
        elevation = if (isSelected) 8.dp else 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(folder.colorHex).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Folder,
                        contentDescription = folder.name,
                        tint = Color(folder.colorHex),
                        modifier = Modifier.size(20.dp)
                    )
                }

                UniqueIdChip(
                    displayId = folder.displayId,
                    onCopied = onCopyId
                )
            }

            Column {
                Text(
                    text = folder.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepViolet,
                    maxLines = 1
                )
                Text(
                    text = "${folder.itemCount} items",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun FileItemRow(
    item: StorageItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopyId: (String) -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = 4.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Icon Badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when (item.category) {
                            FileCategory.PHOTOS -> SpeedCyan.copy(alpha = 0.2f)
                            FileCategory.PDFS -> DangerRose.copy(alpha = 0.2f)
                            FileCategory.CODE -> DeepViolet.copy(alpha = 0.2f)
                            FileCategory.AI_PROMPTS -> RoyalPurple.copy(alpha = 0.2f)
                            FileCategory.VIDEOS -> WarningAmber.copy(alpha = 0.2f)
                            else -> SoftLavender
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.category) {
                        FileCategory.PHOTOS -> Icons.Filled.Image
                        FileCategory.PDFS -> Icons.Filled.PictureAsPdf
                        FileCategory.CODE -> Icons.Filled.Code
                        FileCategory.AI_PROMPTS -> Icons.Filled.Psychology
                        FileCategory.VIDEOS -> Icons.Filled.Videocam
                        FileCategory.NOTES -> Icons.Filled.EditNote
                        FileCategory.LINKS -> Icons.Filled.Link
                        else -> Icons.Filled.InsertDriveFile
                    },
                    contentDescription = item.name,
                    tint = DeepViolet,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    UniqueIdChip(
                        displayId = item.displayId,
                        onCopied = onCopyId
                    )
                    Text(
                        text = "• ${item.formattedSize}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "• ${item.createdAt}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
            if (item.url != null || item.category == FileCategory.LINKS) {
                IconButton(
                    onClick = {
                        val target = item.url ?: item.contentSnippet.ifBlank { item.name }
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(target))
                        onCopyId(target)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = "Copy Link",
                        tint = RoyalPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (item.isFavorite) DangerRose else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
