package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import com.example.model.FileCategory
import com.example.model.StorageItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailSheet(
    item: StorageItem,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onCopyId: (String) -> Unit,
    onShare: (StorageItem) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val rawUrl = item.url ?: if (item.category == FileCategory.LINKS || item.contentSnippet.startsWith("http")) {
        item.contentSnippet
    } else null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.98f), SoftLavender.copy(alpha = 0.94f))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(Color.White, GlassBorderPurple, Color.White)
                    ),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when (item.category) {
                                        FileCategory.PHOTOS -> SpeedCyan.copy(alpha = 0.25f)
                                        FileCategory.PDFS -> DangerRose.copy(alpha = 0.25f)
                                        FileCategory.CODE -> DeepViolet.copy(alpha = 0.25f)
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
                                    else -> Icons.Filled.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = DeepViolet,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column {
                            Text(
                                text = item.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepViolet,
                                maxLines = 1
                            )
                            UniqueIdChip(
                                displayId = item.displayId,
                                onCopied = onCopyId
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SoftLavender.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = DeepViolet,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Metadata Details Card
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailMetaRow("Category", item.category.displayName)
                        DetailMetaRow("File Size", item.formattedSize)
                        DetailMetaRow("Vault Path", item.folderPath)
                        DetailMetaRow("Created", item.createdAt)
                        DetailMetaRow("Internal UUID", item.id.take(13) + "...")

                        if (item.tags.isNotEmpty()) {
                            DetailMetaRow("Smart Tags", item.tags.joinToString(", "))
                        }
                    }
                }

                // Web Link / Resource URL Card
                if (rawUrl != null) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = PastelPurple.copy(alpha = 0.25f),
                        borderColor = PastelPurple.copy(alpha = 0.7f),
                        elevation = 3.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Link,
                                        contentDescription = null,
                                        tint = RoyalPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Web Resource URL",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepViolet
                                    )
                                }
                                Text(
                                    text = "1-Tap Copy & Open",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            Text(
                                text = rawUrl,
                                fontSize = 12.sp,
                                color = RoyalPurple,
                                fontWeight = FontWeight.Medium,
                                maxLines = 3
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ClayButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(rawUrl))
                                        onCopyId("Link copied: $rawUrl")
                                    },
                                    modifier = Modifier.weight(1f),
                                    gradientColors = listOf(RoyalPurple, DeepViolet)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy Link",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Link", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val validUrl = if (rawUrl.startsWith("http://") || rawUrl.startsWith("https://")) rawUrl else "https://$rawUrl"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            onCopyId("Could not open browser")
                                        }
                                    },
                                    shape = RoundedCornerShape(18.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurple),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.OpenInNew,
                                        contentDescription = "Open in Browser",
                                        tint = RoyalPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Link", color = RoyalPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Description or Snippet Card
                if (item.description.isNotBlank() || item.contentSnippet.isNotBlank()) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = Color.White.copy(alpha = 0.85f),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Resource Overview & Content",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepViolet
                                )
                                TextButton(
                                    onClick = {
                                        val snippet = item.contentSnippet.ifBlank { item.description }
                                        clipboardManager.setText(AnnotatedString(snippet))
                                        onCopyId("Content copied to clipboard!")
                                    },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = RoyalPurple)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                                }
                            }
                            Text(
                                text = item.description.ifBlank { item.contentSnippet },
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Actions: Share, Copy, Favorite, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClayButton(
                        onClick = { onShare(item) },
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(RoyalPurple, DeepViolet)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val toCopy = rawUrl ?: item.contentSnippet.ifBlank { item.description.ifBlank { item.name } }
                            clipboardManager.setText(AnnotatedString(toCopy))
                            onCopyId("Copied to clipboard!")
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = RoyalPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = { onToggleFavorite(item.id) },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) DangerRose else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = { onDelete(item.id) },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRose.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("file_delete_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = DangerRose,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun DetailMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DeepViolet)
    }
}
