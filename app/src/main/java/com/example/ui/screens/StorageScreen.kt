package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    storageItems: List<StorageItem>,
    folders: List<FolderItem>,
    selectedCategory: FileCategory?,
    searchQuery: String,
    isGridView: Boolean,
    onCategorySelected: (FileCategory?) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onToggleGridView: () -> Unit,
    onOpenFileDetail: (StorageItem) -> Unit,
    onOpenFolderDetail: (FolderItem) -> Unit,
    onToggleFileFavorite: (String) -> Unit,
    onCreateFolder: (String, FileCategory) -> Unit,
    onCopyId: (String) -> Unit
) {
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var newFolderCategory by remember { mutableStateOf(FileCategory.OTHER) }
    var selectedSortOption by remember { mutableStateOf("Date") }
    var selectedFolder by remember { mutableStateOf<FolderItem?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val folderFilteredItems = remember(storageItems, selectedFolder) {
        if (selectedFolder == null) {
            storageItems
        } else {
            val f = selectedFolder!!
            storageItems.filter { item ->
                item.folderId == f.id ||
                item.folderPath == f.path ||
                item.folderPath.endsWith(f.name) ||
                (item.category == f.category && f.itemCount > 0)
            }
        }
    }

    val sortedItems = remember(folderFilteredItems, selectedSortOption) {
        when (selectedSortOption) {
            "Name" -> folderFilteredItems.sortedBy { it.name }
            "Size" -> folderFilteredItems.sortedByDescending { it.sizeBytes }
            "Type" -> folderFilteredItems.sortedBy { it.category.name }
            else -> folderFilteredItems // default Date
        }
    }

    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar: Title & View / New Folder Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Cloud Vault",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet
                    )
                    Text(
                        text = "Encrypted storage & file manager",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Create Folder Button
                    IconButton(
                        onClick = { showCreateFolderDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CreateNewFolder,
                            contentDescription = "New Folder",
                            tint = RoyalPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Grid / List Toggle Button
                    IconButton(
                        onClick = onToggleGridView,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                            .testTag("toggle_grid_view_button")
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Filled.ViewList else Icons.Filled.GridView,
                            contentDescription = "Toggle View",
                            tint = DeepViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Minimalist Search Bar with Unique ID Search support
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = {
                        Text(
                            text = "Search by Name, Tag, Category or ID (e.g. QK-FIL-8101)",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = RoyalPurple
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        } else {
                            Box(modifier = Modifier.padding(end = 6.dp)) {
                                HubbyAiMascot(size = 24.dp, animateEyes = true)
                            }
                        }
                    },
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vault_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalPurple,
                        unfocusedBorderColor = GlassBorderPurple,
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                    )
                )
            }

            // Category Filter Pills Carousel
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAll = selectedCategory == null
                    Surface(
                        onClick = { onCategorySelected(null) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isAll) RoyalPurple else Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAll) DeepViolet else GlassBorderPurple
                        ),
                        modifier = Modifier.testTag("category_filter_all")
                    ) {
                        Text(
                            text = "All Files",
                            fontSize = 12.sp,
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAll) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                items(FileCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        onClick = { onCategorySelected(category) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) RoyalPurple else Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) DeepViolet else GlassBorderPurple
                        ),
                        modifier = Modifier.testTag("category_filter_${category.name.lowercase()}")
                    ) {
                        Text(
                            text = category.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Vault Folders Carousel Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Vault Folders",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SoftLavender,
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Text(
                            text = "${folders.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                TextButton(
                    onClick = { showCreateFolderDialog = true },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CreateNewFolder,
                        contentDescription = "New Folder",
                        tint = RoyalPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Folder", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(folders) { folder ->
                    val isFolderSelected = selectedFolder?.id == folder.id
                    FolderCard(
                        folder = folder,
                        onClick = {
                            selectedFolder = if (isFolderSelected) null else folder
                            onOpenFolderDetail(folder)
                        },
                        onCopyId = onCopyId,
                        isSelected = isFolderSelected
                    )
                }
            }

            // Sort & Breadcrumbs Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { selectedFolder = null }
                ) {
                    Icon(
                        imageVector = if (selectedFolder != null) Icons.Filled.Folder else Icons.Filled.FolderOpen,
                        contentDescription = null,
                        tint = RoyalPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (selectedFolder != null) "Root / My Vault / ${selectedFolder?.name}" else "Root / My Vault",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepViolet
                    )
                    if (selectedFolder != null) {
                        Surface(
                            onClick = { selectedFolder = null },
                            shape = CircleShape,
                            color = SoftLavender,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear folder filter", tint = DeepViolet, modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                }

                // Sort Dropdown Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable {
                        selectedSortOption = when (selectedSortOption) {
                            "Date" -> "Name"
                            "Name" -> "Size"
                            "Size" -> "Type"
                            else -> "Date"
                        }
                    }
                ) {
                    Text(
                        text = "Sort: $selectedSortOption",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple
                    )
                    Icon(
                        imageVector = Icons.Filled.Sort,
                        contentDescription = "Sort",
                        tint = RoyalPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Items Content (Grid or List View)
            if (sortedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SoftLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FolderOff,
                                contentDescription = null,
                                tint = RoyalPurple,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = if (selectedCategory == FileCategory.LINKS) "No saved links yet" else "No files found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepViolet
                        )
                        Text(
                            text = if (selectedCategory == FileCategory.LINKS)
                                "Tap the + button below to paste and save links directly from your clipboard"
                            else if (selectedFolder != null)
                                "Folder '${selectedFolder?.name}' has no files yet. Save files here with + button."
                            else
                                "Try adjusting your search query or save items with + button",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedItems) { item ->
                        FileGridCard(
                            item = item,
                            onClick = { onOpenFileDetail(item) },
                            onToggleFavorite = { onToggleFileFavorite(item.id) },
                            onCopyId = onCopyId
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedItems) { item ->
                        FileItemRow(
                            item = item,
                            onClick = { onOpenFileDetail(item) },
                            onToggleFavorite = { onToggleFileFavorite(item.id) },
                            onCopyId = onCopyId
                        )
                    }
                }
            }
        }
    }

    // Create Folder Dialog
    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = {
                Text(
                    text = "Create Smart Folder",
                    fontWeight = FontWeight.Bold,
                    color = DeepViolet
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Assign a dedicated container in your encrypted vault.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        label = { Text("Folder Name") },
                        placeholder = { Text("e.g. Design Assets, Research, Links") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Folder Type / Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf(FileCategory.OTHER, FileCategory.LINKS, FileCategory.CODE, FileCategory.PDFS, FileCategory.PHOTOS, FileCategory.AI_PROMPTS, FileCategory.NOTES)) { cat ->
                            val isCatSelected = newFolderCategory == cat
                            Surface(
                                onClick = { newFolderCategory = cat },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCatSelected) RoyalPurple else SoftLavender.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isCatSelected) DeepViolet else GlassBorderPurple)
                            ) {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCatSelected) Color.White else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                ClayButton(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            onCreateFolder(newFolderName.trim(), newFolderCategory)
                            newFolderName = ""
                            showCreateFolderDialog = false
                        }
                    }
                ) {
                    Text("Create Folder", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFolderDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp)
        )
    }
}

@Composable
private fun FileGridCard(
    item: StorageItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopyId: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val rawUrl = item.url ?: if (item.category == FileCategory.LINKS || item.contentSnippet.startsWith("http")) item.contentSnippet else null

    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 6.dp,
        onClick = onClick
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
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (item.category) {
                                FileCategory.PHOTOS -> SpeedCyan.copy(alpha = 0.25f)
                                FileCategory.PDFS -> DangerRose.copy(alpha = 0.25f)
                                FileCategory.CODE -> DeepViolet.copy(alpha = 0.25f)
                                FileCategory.LINKS -> PastelPurple.copy(alpha = 0.4f)
                                FileCategory.AI_PROMPTS -> RoyalPurple.copy(alpha = 0.25f)
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
                            FileCategory.LINKS -> Icons.Filled.Link
                            FileCategory.AI_PROMPTS -> Icons.Filled.Psychology
                            else -> Icons.Filled.InsertDriveFile
                        },
                        contentDescription = null,
                        tint = DeepViolet,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (rawUrl != null) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(rawUrl))
                                onCopyId(rawUrl)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Link,
                                contentDescription = "Copy Link",
                                tint = RoyalPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) DangerRose else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                if (rawUrl != null) {
                    Text(
                        text = rawUrl.removePrefix("https://").removePrefix("http://"),
                        fontSize = 10.sp,
                        color = RoyalPurple,
                        maxLines = 1
                    )
                }
                Text(
                    text = item.formattedSize,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                UniqueIdChip(
                    displayId = item.displayId,
                    onCopied = onCopyId
                )
            }
        }
    }
}
