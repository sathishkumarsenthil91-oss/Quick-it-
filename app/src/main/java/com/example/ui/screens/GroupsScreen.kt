package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    groups: List<GroupHub>,
    hubMessages: List<HubMessage>,
    friendUsers: List<FriendUser>,
    storageItems: List<StorageItem>,
    notes: List<NoteItem>,
    prompts: List<PromptItem>,
    tasks: List<TaskItem>,
    codingProjects: List<CodingProject> = emptyList(),
    onCreateGroup: (String, String, Boolean) -> Unit,
    onSendMessage: (hubId: String, text: String, sharedItemTitle: String?, sharedItemId: String?, sharedItemType: String?) -> Unit,
    onInviteFriend: (String) -> Unit,
    onCopyId: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Hubs, 1: Friend Search (RLS)
    var activeChatHub by remember { mutableStateOf<GroupHub?>(null) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showShareFeaturePicker by remember { mutableStateOf(false) }

    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var isPrivateGroup by remember { mutableStateOf(true) }
    var friendSearchQuery by remember { mutableStateOf("") }
    var chatInputText by remember { mutableStateOf("") }

    val chatListState = rememberLazyListState()

    // Filtered friends based on search by Unique User ID or name
    val filteredFriends = remember(friendUsers, friendSearchQuery) {
        if (friendSearchQuery.isBlank()) friendUsers
        else {
            val q = friendSearchQuery.trim().lowercase()
            friendUsers.filter {
                it.name.lowercase().contains(q) || it.displayId.lowercase().contains(q)
            }
        }
    }

    ClayBackground {
        if (activeChatHub != null) {
            // ----------------------------------------------------
            // Active Hub Interactive Chat View
            // ----------------------------------------------------
            val hub = activeChatHub!!
            val messagesForHub = hubMessages.filter { it.hubId == hub.id }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Chat Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = { activeChatHub = null },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Hubs",
                                tint = DeepViolet
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = hub.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepViolet
                                )
                                UniqueIdChip(displayId = hub.displayId, onCopied = onCopyId)
                            }
                            Text(
                                text = "RLS Policy: Encrypted Peer Isolation",
                                fontSize = 11.sp,
                                color = SpeedCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Hubby AI Companion Trigger with animated eyes!
                        IconButton(
                            onClick = {
                                onSendMessage(
                                    hub.id,
                                    "✦ Hubby AI: Peer security active under RLS policy. All shared vault features are isolated to authorized members.",
                                    null,
                                    null,
                                    null
                                )
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        ) {
                            HubbyAiMascot(size = 28.dp, animateEyes = true)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftLavender,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple)
                        ) {
                            Text(
                                text = "${hub.memberCount} members",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Chat Messages Thread
                LazyColumn(
                    state = chatListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                ) {
                    items(messagesForHub) { msg ->
                        HubChatBubble(
                            message = msg,
                            onCopyId = onCopyId
                        )
                    }
                }

                // Chat Composer with "Share App Feature" Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        backgroundColor = Color.White.copy(alpha = 0.95f),
                        elevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // "Share Feature" trigger button
                            IconButton(
                                onClick = { showShareFeaturePicker = true },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SoftLavender)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share Feature",
                                    tint = RoyalPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            TextField(
                                value = chatInputText,
                                onValueChange = { chatInputText = it },
                                placeholder = {
                                    Text("Message Hub or share vault item...", fontSize = 13.sp, color = TextMuted)
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f),
                                maxLines = 3
                            )

                            // Send Button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(SpeedCyan, DeepViolet))
                                    )
                                    .clickable(
                                        enabled = chatInputText.isNotBlank(),
                                        onClick = {
                                            val txt = chatInputText
                                            chatInputText = ""
                                            onSendMessage(hub.id, txt, null, null, null)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ----------------------------------------------------
            // Main Hubs & Friends Overview
            // ----------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Collaborative Hubs",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepViolet
                        )
                        Text(
                            text = "RLS-isolated group chat & friend vault sharing",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    ClayButton(
                        onClick = { showCreateGroupDialog = true },
                        modifier = Modifier.testTag("create_group_button"),
                        gradientColors = listOf(RoyalPurple, DeepViolet)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create Hub",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Hub", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Tab Switcher: "Hubs & Chats" vs "Search Friends (RLS)"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ClayCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (selectedTab == 0) RoyalPurple else Color.White.copy(alpha = 0.85f),
                        borderColor = if (selectedTab == 0) DeepViolet else GlassBorderPurple,
                        onClick = { selectedTab = 0 },
                        elevation = if (selectedTab == 0) 4.dp else 1.dp
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Active Hubs & Chat",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color.White else DeepViolet
                            )
                        }
                    }

                    ClayCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = if (selectedTab == 1) RoyalPurple else Color.White.copy(alpha = 0.85f),
                        borderColor = if (selectedTab == 1) DeepViolet else GlassBorderPurple,
                        onClick = { selectedTab = 1 },
                        elevation = if (selectedTab == 1) 4.dp else 1.dp
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Search Friends (RLS)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) Color.White else DeepViolet
                            )
                        }
                    }
                }

                if (selectedTab == 0) {
                    // -----------------------------
                    // Active Hubs List
                    // -----------------------------
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            ClayCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                backgroundColor = Color.White.copy(alpha = 0.9f),
                                elevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Forum,
                                        contentDescription = null,
                                        tint = RoyalPurple,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = "Tap any Hub below to join live chat and share files, prompts, and code snippets directly into the discussion.",
                                        fontSize = 12.sp,
                                        color = DeepViolet,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        items(groups) { group ->
                            GroupCard(
                                group = group,
                                onOpenChat = { activeChatHub = group },
                                onCopyId = onCopyId
                            )
                        }
                    }
                } else {
                    // -----------------------------
                    // Friend Search by Unique ID (RLS)
                    // -----------------------------
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        // My Hub User ID & RLS Policy Card
                        ClayCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(20.dp),
                            backgroundColor = Color.White.copy(alpha = 0.95f),
                            elevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                                .background(Brush.linearGradient(listOf(SpeedCyan, RoyalPurple))),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Fingerprint,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "My Hub User ID",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DeepViolet
                                            )
                                            Text(
                                                text = "Share with friends to connect",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                    UniqueIdChip(displayId = "QK-USR-8902", onCopied = onCopyId)
                                }
                                Text(
                                    text = "RLS Policy: Row-Level Security ensures friends can only access items you explicitly share in this chat.",
                                    fontSize = 11.sp,
                                    color = DeepViolet,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        OutlinedTextField(
                            value = friendSearchQuery,
                            onValueChange = { friendSearchQuery = it },
                            label = { Text("Search by Unique User ID (e.g. QK-USR-3301)") },
                            placeholder = { Text("Enter friend ID or name") },
                            leadingIcon = {
                                Icon(Icons.Filled.PersonSearch, contentDescription = null, tint = RoyalPurple)
                            },
                            trailingIcon = {
                                if (friendSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { friendSearchQuery = "" }) {
                                        Icon(Icons.Filled.Clear, contentDescription = null, tint = TextMuted)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalPurple,
                                unfocusedBorderColor = GlassBorderPurple
                            )
                        )

                        LazyColumn(
                            contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredFriends) { friend ->
                                FriendCard(
                                    friend = friend,
                                    onInvite = { onInviteFriend(friend.id) },
                                    onStartChat = {
                                        activeChatHub = GroupHub(
                                            id = "direct_${friend.id}",
                                            name = friend.name,
                                            description = "1:1 RLS Peer Private Chat",
                                            displayId = "QK-HUB-${friend.displayId.takeLast(4)}",
                                            isPrivate = true,
                                            memberCount = 2,
                                            inviteCode = "RLS-PEER",
                                            sharedFilesCount = 1
                                        )
                                    },
                                    onCopyId = onCopyId
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // "Share App Feature into Hub Chat" Modal Sheet
    if (showShareFeaturePicker && activeChatHub != null) {
        val hub = activeChatHub!!
        ModalBottomSheet(
            onDismissRequest = { showShareFeaturePicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.98f), SoftLavender.copy(alpha = 0.95f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Share Vault Feature into ${hub.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet
                    )
                    Text(
                        text = "Select any personal vault item to share with hub members:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text("Vault Files", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                        }
                        items(storageItems.take(3)) { item ->
                            ShareItemRow(
                                title = item.name,
                                displayId = item.displayId,
                                subtitle = item.formattedSize,
                                icon = Icons.Filled.InsertDriveFile,
                                onSelect = {
                                    onSendMessage(hub.id, "Shared file from vault: ${item.name}", item.name, item.displayId, "FILE")
                                    showShareFeaturePicker = false
                                }
                            )
                        }

                        item {
                            Text("AI Prompts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                        }
                        items(prompts.take(2)) { prm ->
                            ShareItemRow(
                                title = prm.title,
                                displayId = prm.displayId,
                                subtitle = prm.category,
                                icon = Icons.Filled.Psychology,
                                onSelect = {
                                    onSendMessage(hub.id, "Shared AI prompt: ${prm.title}", prm.title, prm.displayId, "PROMPT")
                                    showShareFeaturePicker = false
                                }
                            )
                        }

                        item {
                            Text("Personal Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                        }
                        items(notes.take(2)) { note ->
                            ShareItemRow(
                                title = note.title,
                                displayId = note.displayId,
                                subtitle = note.folderName,
                                icon = Icons.Filled.EditNote,
                                onSelect = {
                                    onSendMessage(hub.id, "Shared note: ${note.title}", note.title, note.displayId, "NOTE")
                                    showShareFeaturePicker = false
                                }
                            )
                        }

                        if (codingProjects.isNotEmpty()) {
                            item {
                                Text("Coding Projects", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                            }
                            items(codingProjects.take(2)) { proj ->
                                ShareItemRow(
                                    title = proj.title,
                                    displayId = proj.displayId,
                                    subtitle = "${proj.languages.firstOrNull() ?: "Kotlin"} • ${proj.status}",
                                    icon = Icons.Filled.Code,
                                    onSelect = {
                                        onSendMessage(hub.id, "Shared project: ${proj.title}", proj.title, proj.displayId, "CODE")
                                        showShareFeaturePicker = false
                                    }
                                )
                            }
                        }

                        if (tasks.isNotEmpty()) {
                            item {
                                Text("Daily Tasks & Reminders", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                            }
                            items(tasks.take(2)) { task ->
                                ShareItemRow(
                                    title = task.title,
                                    displayId = task.displayId,
                                    subtitle = "${task.dueTime} • ${task.priority.name}",
                                    icon = Icons.Filled.CheckCircle,
                                    onSelect = {
                                        onSendMessage(hub.id, "Shared task: ${task.title}", task.title, task.displayId, "TASK")
                                        showShareFeaturePicker = false
                                    }
                                )
                            }
                        }
                    }

                    ClayButton(
                        onClick = { showShareFeaturePicker = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Create Group Dialog
    if (showCreateGroupDialog) {
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = {
                Text(
                    text = "Create Collaborative Hub",
                    fontWeight = FontWeight.Bold,
                    color = DeepViolet
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Hub Name") },
                        placeholder = { Text("e.g. AI Research Squad") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = groupDescription,
                        onValueChange = { groupDescription = it },
                        label = { Text("Description & Mission") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isPrivateGroup) "Private (Invite Only)" else "Public Community",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepViolet
                        )
                        Switch(
                            checked = isPrivateGroup,
                            onCheckedChange = { isPrivateGroup = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RoyalPurple
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftLavender.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple)
                    ) {
                        Text(
                            text = "RLS Policy Protection: PostgreSQL Row Level Security automatically restricts read/write permissions to verified members.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = DeepViolet,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                ClayButton(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            onCreateGroup(groupName, groupDescription, isPrivateGroup)
                            groupName = ""
                            groupDescription = ""
                            showCreateGroupDialog = false
                        }
                    }
                ) {
                    Text("Create Hub", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(26.dp)
        )
    }
}

@Composable
private fun ShareItemRow(
    title: String,
    displayId: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onSelect: () -> Unit
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = RoyalPurple, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepViolet, maxLines = 1)
                Text(text = "$displayId • $subtitle", fontSize = 11.sp, color = TextSecondary)
            }
            Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
        }
    }
}

@Composable
private fun HubChatBubble(
    message: HubMessage,
    onCopyId: (String) -> Unit
) {
    val isUser = message.isCurrentUser

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = message.senderName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DeepViolet
            )
            UniqueIdChip(displayId = message.senderUserId, onCopied = onCopyId)
            Text(text = message.timestamp, fontSize = 10.sp, color = TextMuted)
        }

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(listOf(RoyalPurple, DeepViolet))
                    else Brush.verticalGradient(listOf(Color.White, SoftLavender.copy(alpha = 0.8f)))
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) Color.Transparent else GlassBorderPurple,
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = if (isUser) Color.White else TextPrimary
                )

                // Shared App Feature Card inside chat thread
                if (message.sharedItemTitle != null && message.sharedItemId != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isUser) Color.White.copy(alpha = 0.2f) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (message.sharedItemType) {
                                    "PROMPT" -> Icons.Filled.Psychology
                                    "NOTE" -> Icons.Filled.EditNote
                                    "CODE" -> Icons.Filled.Code
                                    else -> Icons.Filled.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = if (isUser) Color.White else RoyalPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = message.sharedItemTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) Color.White else DeepViolet,
                                    maxLines = 1
                                )
                                Text(
                                    text = "ID: ${message.sharedItemId}",
                                    fontSize = 10.sp,
                                    color = if (isUser) Color.White.copy(alpha = 0.8f) else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupCard(
    group: GroupHub,
    onOpenChat: () -> Unit,
    onCopyId: (String) -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = 6.dp,
        onClick = onOpenChat
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(group.bannerGradientStart), Color(group.bannerGradientEnd))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (group.isPrivate) Icons.Filled.Lock else Icons.Filled.Public,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = group.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepViolet
                        )
                        Text(
                            text = if (group.isPrivate) "Private • ${group.memberCount} members" else "Public • ${group.memberCount} members",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftLavender,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple)
                ) {
                    Text(
                        text = "Open Chat ✦",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = group.description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UniqueIdChip(displayId = group.displayId, onCopied = onCopyId)
                    Text(
                        text = "Invite: ${group.inviteCode}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FolderShared,
                        contentDescription = null,
                        tint = RoyalPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${group.sharedFilesCount} shared items",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendCard(
    friend: FriendUser,
    onInvite: () -> Unit,
    onStartChat: () -> Unit,
    onCopyId: (String) -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(SpeedCyan, RoyalPurple))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friend.name.split(" ").map { it.take(1) }.joinToString(""),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(friend.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepViolet)
                    UniqueIdChip(displayId = friend.displayId, onCopied = onCopyId)
                }
                Text(friend.rlsPolicy, fontSize = 10.sp, color = SpeedCyan, fontWeight = FontWeight.SemiBold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Direct 1:1 RLS Chat Button
                IconButton(
                    onClick = onStartChat,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SoftLavender)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Forum,
                        contentDescription = "Start 1:1 Chat",
                        tint = RoyalPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }

                ClayButton(
                    onClick = onInvite,
                    shape = RoundedCornerShape(14.dp),
                    gradientColors = if (friend.isInvited) listOf(Color(0xFF10B981), Color(0xFF059669)) else listOf(RoyalPurple, DeepViolet)
                ) {
                    Text(
                        text = if (friend.isInvited) "Connected" else "Connect",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
