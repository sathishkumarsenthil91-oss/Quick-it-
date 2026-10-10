package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FileCategory
import com.example.model.FolderItem
import com.example.ui.theme.*

data class SmartSaveAction(
    val category: FileCategory,
    val title: String,
    val icon: ImageVector,
    val defaultExtension: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSaveBottomSheet(
    folders: List<FolderItem>,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: FileCategory,
        sizeBytes: Long,
        folderId: String?,
        folderPath: String,
        description: String,
        tags: List<String>,
        url: String?,
        contentSnippet: String
    ) -> Unit,
    onCreateFolder: ((String, FileCategory) -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current
    val saveActions = remember {
        listOf(
            SmartSaveAction(FileCategory.LINKS, "Paste Link", Icons.Filled.Link, ".link"),
            SmartSaveAction(FileCategory.PDFS, "Upload PDF", Icons.Filled.PictureAsPdf, ".pdf"),
            SmartSaveAction(FileCategory.PHOTOS, "Upload Photo", Icons.Filled.Image, ".png"),
            SmartSaveAction(FileCategory.VIDEOS, "Upload Video", Icons.Filled.Videocam, ".mp4"),
            SmartSaveAction(FileCategory.NOTES, "Create Note", Icons.Filled.EditNote, ".txt"),
            SmartSaveAction(FileCategory.AI_PROMPTS, "Save AI Prompt", Icons.Filled.Psychology, ".prompt"),
            SmartSaveAction(FileCategory.CODE, "Save Code", Icons.Filled.Code, ".kt"),
            SmartSaveAction(FileCategory.DOCUMENTS, "Upload Document", Icons.Filled.Description, ".docx"),
            SmartSaveAction(FileCategory.SOCIAL, "Save Social Post", Icons.Filled.Share, ".social")
        )
    }

    var selectedAction by remember { mutableStateOf(saveActions[0]) }
    var itemName by remember { mutableStateOf("") }
    var itemContentOrUrl by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemTags by remember { mutableStateOf("QuickIt, Vault, AI") }
    var selectedFolder by remember { mutableStateOf<FolderItem?>(folders.firstOrNull()) }
    var isAiGenerating by remember { mutableStateOf(false) }
    var showInlineFolderDialog by remember { mutableStateOf(false) }
    var newInlineFolderName by remember { mutableStateOf("") }

    fun pasteLinkFromClipboard() {
        val clip = clipboardManager.getText()?.text
        if (!clip.isNullOrBlank()) {
            val trimmed = clip.trim()
            itemContentOrUrl = trimmed
            val clean = trimmed.removePrefix("https://").removePrefix("http://").removePrefix("www.")
            val domain = clean.split("/").firstOrNull() ?: "Link"
            val pathPart = clean.split("/").filter { it.isNotBlank() }.drop(1).firstOrNull() ?: ""
            val cleanTitle = if (pathPart.isNotBlank()) {
                "${domain}_${pathPart.take(16)}"
            } else {
                domain
            }.replace(Regex("[^a-zA-Z0-9_.-]"), "_")
            if (itemName.isBlank() || itemName.startsWith("New_")) {
                itemName = "$cleanTitle.link"
            }
        }
    }

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
                        listOf(Color.White.copy(alpha = 0.96f), SoftLavender.copy(alpha = 0.95f))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Drag handle bar
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(42.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(TextMuted.copy(alpha = 0.4f))
                )

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Universal Smart Save",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepViolet
                        )
                        Text(
                            text = "Save any link, document, code, or prompt to your vault",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
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

                // 9 Smart Save Action Selector Chips
                Text(
                    text = "Select Content Type",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(saveActions) { action ->
                        val isSelected = action == selectedAction
                        Surface(
                            onClick = {
                                selectedAction = action
                                if (itemName.isBlank()) {
                                    itemName = "New_${action.title.replace(" ", "_")}${action.defaultExtension}"
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) RoyalPurple else Color.White.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) DeepViolet else GlassBorderPurple
                            ),
                            shadowElevation = if (isSelected) 6.dp else 2.dp,
                            modifier = Modifier.testTag("action_chip_${action.category.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.title,
                                    tint = if (isSelected) Color.White else DeepViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = action.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }

                // Input: Item Name
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Resource Title / File Name") },
                    placeholder = { Text("e.g. Gemini_Multimodal_Pipeline.kt") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("smart_save_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalPurple,
                        unfocusedBorderColor = GlassBorderPurple,
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                // Input: Content or URL
                OutlinedTextField(
                    value = itemContentOrUrl,
                    onValueChange = { itemContentOrUrl = it },
                    label = {
                        Text(
                            if (selectedAction.category == FileCategory.LINKS || selectedAction.category == FileCategory.SOCIAL)
                                "URL / Link"
                            else "Snippet, Content, or Prompt Body"
                        )
                    },
                    placeholder = { Text("https://... or paste raw content here") },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (itemContentOrUrl.isNotEmpty()) {
                                IconButton(onClick = { itemContentOrUrl = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                            IconButton(onClick = { pasteLinkFromClipboard() }) {
                                Icon(
                                    imageVector = Icons.Filled.ContentPaste,
                                    contentDescription = "Paste from Clipboard",
                                    tint = RoyalPurple
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalPurple,
                        unfocusedBorderColor = GlassBorderPurple,
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                // Quick Paste Bar for Links
                if (selectedAction.category == FileCategory.LINKS || selectedAction.category == FileCategory.SOCIAL) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Supports GitHub, Docs, YouTube, Web links",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Surface(
                            onClick = { pasteLinkFromClipboard() },
                            shape = RoundedCornerShape(12.dp),
                            color = PastelPurple.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentPaste,
                                    contentDescription = null,
                                    tint = RoyalPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Paste from Clipboard",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple
                                )
                            }
                        }
                    }
                }

                // Target Folder Destination Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vault Destination Folder",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    TextButton(
                        onClick = { showInlineFolderDialog = true },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Filled.CreateNewFolder, contentDescription = null, tint = RoyalPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Folder", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(folders) { folder ->
                        val isSelected = folder == selectedFolder
                        Surface(
                            onClick = { selectedFolder = folder },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) DeepViolet else Color.White.copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) RoyalPurple else GlassBorderPurple
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Folder,
                                    contentDescription = folder.name,
                                    tint = if (isSelected) Color.White else RoyalPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = folder.name,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            }
                        }
                    }

                    item {
                        Surface(
                            onClick = { showInlineFolderDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add Folder",
                                    tint = RoyalPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Add Folder",
                                    fontSize = 11.sp,
                                    color = RoyalPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // AI Auto-Enrich Button (Generates smart tags, description and clean name)
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = PastelPurple.copy(alpha = 0.25f),
                    borderColor = PastelPurple.copy(alpha = 0.6f),
                    onClick = {
                        isAiGenerating = true
                        val base = if (itemName.isBlank()) selectedAction.title else itemName
                        if (itemName.isBlank()) {
                            itemName = "Quick_${base.replace(" ", "_")}${selectedAction.defaultExtension}"
                        }
                        itemDescription = "AI-analyzed resource for ${selectedAction.category.displayName} in ${selectedFolder?.name ?: "Vault"}. Automatically organized."
                        itemTags = "${selectedAction.category.displayName}, Cloud, QuickIt, AI-Indexed"
                        isAiGenerating = false
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HubbyAiMascot(size = 36.dp, animateEyes = true)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Enrich with Hubby AI",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepViolet
                            )
                            Text(
                                text = "Generate title, smart tags & descriptions automatically",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Save Action Button
                ClayButton(
                    onClick = {
                        val finalName = if (itemName.isBlank()) {
                            "Saved_${selectedAction.title.replace(" ", "_")}_${System.currentTimeMillis() % 1000}${selectedAction.defaultExtension}"
                        } else itemName

                        val tagList = itemTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }

                        val isLinkCategory = selectedAction.category == FileCategory.LINKS || selectedAction.category == FileCategory.SOCIAL
                        val trimmed = itemContentOrUrl.trim()
                        val finalUrl = if (isLinkCategory && trimmed.isNotBlank()) {
                            if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
                        } else if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                            trimmed
                        } else null

                        val finalSnippet = if (trimmed.isNotBlank()) trimmed else (finalUrl ?: "")

                        onSave(
                            finalName,
                            selectedAction.category,
                            45_000L + (1000..90000).random(),
                            selectedFolder?.id,
                            selectedFolder?.path ?: "/My Vault",
                            if (itemDescription.isBlank()) "Saved via Quick it Universal Smart Save" else itemDescription,
                            tagList,
                            finalUrl,
                            finalSnippet
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("smart_save_confirm_button"),
                    gradientColors = listOf(RoyalPurple, DeepViolet)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save to Vault",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showInlineFolderDialog) {
        AlertDialog(
            onDismissRequest = { showInlineFolderDialog = false },
            title = {
                Text(
                    text = "New Vault Folder",
                    fontWeight = FontWeight.Bold,
                    color = DeepViolet
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Create a new folder to store your items.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = newInlineFolderName,
                        onValueChange = { newInlineFolderName = it },
                        label = { Text("Folder Name") },
                        placeholder = { Text("e.g. Project Specs, Bookmarks") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                ClayButton(
                    onClick = {
                        if (newInlineFolderName.isNotBlank()) {
                            onCreateFolder?.invoke(newInlineFolderName.trim(), selectedAction.category)
                            newInlineFolderName = ""
                            showInlineFolderDialog = false
                        }
                    }
                ) {
                    Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInlineFolderDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
