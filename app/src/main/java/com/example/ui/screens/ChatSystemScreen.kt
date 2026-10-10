package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSystemScreen(
    conversations: List<ConversationItem>,
    messages: List<HubMessage>,
    friendUsers: List<FriendUser>,
    storageItems: List<StorageItem>,
    notes: List<NoteItem>,
    prompts: List<PromptItem>,
    tasks: List<TaskItem>,
    codingProjects: List<CodingProject>,
    activeVoiceCall: VoiceCallState?,
    onSendMessage: (
        hubId: String,
        text: String,
        type: MessageType,
        mediaUrl: String?,
        mediaCaption: String?,
        audioDurationSeconds: Int,
        replyToMessageId: String?,
        replyToText: String?,
        replyToSender: String?,
        sharedItemTitle: String?,
        sharedItemId: String?,
        sharedItemType: String?
    ) -> Unit,
    onAddReaction: (messageId: String, emoji: String) -> Unit,
    onToggleStarMessage: (messageId: String) -> Unit,
    onTogglePinMessage: (messageId: String) -> Unit,
    onDeleteMessage: (messageId: String) -> Unit,
    onTogglePinConversation: (id: String) -> Unit,
    onToggleArchiveConversation: (id: String) -> Unit,
    onToggleMuteConversation: (id: String) -> Unit,
    onDeleteConversation: (id: String) -> Unit,
    onStartVoiceCall: (contactName: String, contactId: String, isGroup: Boolean) -> Unit,
    onEndVoiceCall: () -> Unit,
    onToggleCallMute: () -> Unit,
    onToggleCallSpeaker: () -> Unit,
    onCreateConversation: (name: String, isGroup: Boolean, friendUserId: String?) -> ConversationItem,
    onCopyId: (String) -> Unit
) {
    var activeConversation by remember { mutableStateOf<ConversationItem?>(null) }
    var selectedTabFilter by remember { mutableStateOf("All") } // All, Unread, Groups, Pinned, Archived
    var chatSearchQuery by remember { mutableStateOf("") }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showCameraView by remember { mutableStateOf(false) }

    ClayBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            if (activeConversation != null) {
                // ----------------------------------------------------
                // Active 1:1 or Group Conversation Screen
                // ----------------------------------------------------
                ConversationView(
                    conversation = activeConversation!!,
                    allMessages = messages.filter { it.hubId == activeConversation!!.id },
                    storageItems = storageItems,
                    notes = notes,
                    prompts = prompts,
                    tasks = tasks,
                    codingProjects = codingProjects,
                    onBack = { activeConversation = null },
                    onSendMessage = { text, type, mediaUrl, caption, audioDur, replyId, replyTxt, replySender, itemTitle, itemId, itemType ->
                        onSendMessage(
                            activeConversation!!.id,
                            text,
                            type,
                            mediaUrl,
                            caption,
                            audioDur,
                            replyId,
                            replyTxt,
                            replySender,
                            itemTitle,
                            itemId,
                            itemType
                        )
                    },
                    onStartVoiceCall = {
                        onStartVoiceCall(activeConversation!!.name, activeConversation!!.id, activeConversation!!.isGroup)
                    },
                    onOpenLiveCamera = { showCameraView = true },
                    onAddReaction = onAddReaction,
                    onToggleStarMessage = onToggleStarMessage,
                    onTogglePinMessage = onTogglePinMessage,
                    onDeleteMessage = onDeleteMessage,
                    onCopyId = onCopyId
                )
            } else {
                // ----------------------------------------------------
                // Chat Home Screen (WhatsApp-Inspired Dashboard)
                // ----------------------------------------------------
                ChatHomeView(
                    conversations = conversations,
                    selectedTab = selectedTabFilter,
                    onSelectTab = { selectedTabFilter = it },
                    searchQuery = chatSearchQuery,
                    onSearchQueryChanged = { chatSearchQuery = it },
                    onOpenConversation = { activeConversation = it },
                    onNewChatClick = { showNewChatDialog = true },
                    onCreateGroupClick = { showCreateGroupDialog = true },
                    onTogglePinConversation = onTogglePinConversation,
                    onToggleArchiveConversation = onToggleArchiveConversation,
                    onToggleMuteConversation = onToggleMuteConversation,
                    onDeleteConversation = onDeleteConversation,
                    onCopyId = onCopyId
                )
            }

            // ----------------------------------------------------
            // In-Chat Live Camera & Media Capture Overlay
            // ----------------------------------------------------
            if (showCameraView && activeConversation != null) {
                LiveCameraMediaOverlay(
                    onDismiss = { showCameraView = false },
                    onSendMedia = { mediaType, caption, filterName ->
                        onSendMessage(
                            activeConversation!!.id,
                            if (mediaType == MessageType.IMAGE) "📷 Photo captured with camera" else "🎥 Video recorded with camera",
                            mediaType,
                            "captured_media",
                            caption.ifBlank { "Live camera capture • $filterName" },
                            0,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                        showCameraView = false
                    }
                )
            }

            // ----------------------------------------------------
            // Active Voice Call Overlay (Audio Only)
            // ----------------------------------------------------
            activeVoiceCall?.let { callState ->
                VoiceCallOverlay(
                    callState = callState,
                    onEndCall = onEndVoiceCall,
                    onToggleMute = onToggleCallMute,
                    onToggleSpeaker = onToggleCallSpeaker
                )
            }

            // ----------------------------------------------------
            // New Chat Dialog
            // ----------------------------------------------------
            if (showNewChatDialog) {
                NewChatDialog(
                    friendUsers = friendUsers,
                    onDismiss = { showNewChatDialog = false },
                    onSelectFriend = { friend ->
                        val created = onCreateConversation(friend.name, false, friend.displayId)
                        activeConversation = created
                        showNewChatDialog = false
                    },
                    onCopyId = onCopyId
                )
            }

            // ----------------------------------------------------
            // Create Group Dialog
            // ----------------------------------------------------
            if (showCreateGroupDialog) {
                CreateGroupChatDialog(
                    friendUsers = friendUsers,
                    onDismiss = { showCreateGroupDialog = false },
                    onCreateGroup = { name, _ ->
                        val created = onCreateConversation(name, true, null)
                        activeConversation = created
                        showCreateGroupDialog = false
                    }
                )
            }
        }
    }
}

// ====================================================================
// 1. CHAT HOME VIEW
// ====================================================================
@Composable
private fun ChatHomeView(
    conversations: List<ConversationItem>,
    selectedTab: String,
    onSelectTab: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onOpenConversation: (ConversationItem) -> Unit,
    onNewChatClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    onTogglePinConversation: (String) -> Unit,
    onToggleArchiveConversation: (String) -> Unit,
    onToggleMuteConversation: (String) -> Unit,
    onDeleteConversation: (String) -> Unit,
    onCopyId: (String) -> Unit
) {
    val filterTabs = listOf("All", "Unread", "Groups", "Pinned", "Archived")

    val filteredConversations = remember(conversations, selectedTab, searchQuery) {
        conversations.filter { conv ->
            val matchesTab = when (selectedTab) {
                "Unread" -> conv.unreadCount > 0 && !conv.isArchived
                "Groups" -> conv.isGroup && !conv.isArchived
                "Pinned" -> conv.isPinned && !conv.isArchived
                "Archived" -> conv.isArchived
                else -> !conv.isArchived
            }
            val matchesQuery = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                conv.name.lowercase().contains(q) ||
                        conv.lastMessage.lowercase().contains(q) ||
                        conv.displayId.lowercase().contains(q)
            }
            matchesTab && matchesQuery
        }.sortedWith(
            compareByDescending<ConversationItem> { it.isPinned }
                .thenByDescending { it.lastMessageTime }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = 85.dp) // Leave clearance for bottom nav
            ) {
                // Secondary FAB: Create Group
                SmallFloatingActionButton(
                    onClick = onCreateGroupClick,
                    containerColor = SoftLavender,
                    contentColor = DeepViolet,
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.GroupAdd, contentDescription = "Create Group")
                }

                // Primary FAB: New Chat
                FloatingActionButton(
                    onClick = onNewChatClick,
                    containerColor = RoyalPurple,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = "New Chat", modifier = Modifier.size(26.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Chats",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepViolet
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                Text("RLS Peer E2EE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            }
                        }
                    }
                    Text(
                        text = "Real-time encrypted messaging & calling",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onCreateGroupClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                    ) {
                        Icon(Icons.Filled.GroupAdd, contentDescription = "New Group", tint = RoyalPurple, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Search chats or contacts...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = RoyalPurple) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalPurple,
                        unfocusedBorderColor = GlassBorderPurple,
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f)
                    )
                )
            }

            // Filter Tabs (All, Unread, Groups, Pinned, Archived)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs) { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        onClick = { onSelectTab(tab) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) RoyalPurple else Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DeepViolet else GlassBorderPurple)
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else DeepViolet,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Chat Conversations List
            if (filteredConversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.ChatBubbleOutline, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                        Text("No chats in '$selectedTab'", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepViolet)
                        Text("Tap + below to start a new chat with your peers", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 130.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredConversations, key = { it.id }) { conv ->
                        ConversationRowItem(
                            conversation = conv,
                            onClick = { onOpenConversation(conv) },
                            onTogglePin = { onTogglePinConversation(conv.id) },
                            onToggleArchive = { onToggleArchiveConversation(conv.id) },
                            onToggleMute = { onToggleMuteConversation(conv.id) },
                            onDelete = { onDeleteConversation(conv.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationRowItem(
    conversation: ConversationItem,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onToggleMute: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        backgroundColor = Color.White.copy(alpha = 0.92f),
        elevation = 3.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Contact Avatar with Online Indicator
            Box {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            if (conversation.isGroup) Brush.linearGradient(listOf(SpeedCyan, RoyalPurple))
                            else Brush.linearGradient(listOf(PastelPurple, DeepViolet))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = conversation.avatarInitials,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (conversation.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(2.dp, Color.White, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            // Contact Info & Last Message Snippet
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Text(
                        text = conversation.lastMessageTime,
                        fontSize = 11.sp,
                        color = if (conversation.unreadCount > 0) RoyalPurple else TextMuted,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // WhatsApp Status Ticks (✓, ✓✓)
                        if (!conversation.isGroup && conversation.lastMessageStatus != MessageStatus.SENDING) {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = null,
                                tint = if (conversation.lastMessageStatus == MessageStatus.READ) SpeedCyan else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Text(
                            text = conversation.lastMessage,
                            fontSize = 13.sp,
                            color = if (conversation.unreadCount > 0) DeepViolet else TextSecondary,
                            fontWeight = if (conversation.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (conversation.isPinned) {
                            Icon(Icons.Filled.PushPin, contentDescription = "Pinned", tint = RoyalPurple, modifier = Modifier.size(14.dp))
                        }
                        if (conversation.isMuted) {
                            Icon(Icons.Filled.VolumeOff, contentDescription = "Muted", tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                        if (conversation.unreadCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = RoyalPurple,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${conversation.unreadCount}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Overflow Options Menu
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Options", tint = TextMuted, modifier = Modifier.size(18.dp))
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(if (conversation.isPinned) "Unpin Chat" else "Pin Chat") },
                                    leadingIcon = { Icon(Icons.Filled.PushPin, contentDescription = null) },
                                    onClick = { onTogglePin(); showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (conversation.isArchived) "Unarchive" else "Archive") },
                                    leadingIcon = { Icon(Icons.Filled.Archive, contentDescription = null) },
                                    onClick = { onToggleArchive(); showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (conversation.isMuted) "Unmute" else "Mute") },
                                    leadingIcon = { Icon(Icons.Filled.VolumeMute, contentDescription = null) },
                                    onClick = { onToggleMute(); showMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Chat", color = DangerRose) },
                                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = DangerRose) },
                                    onClick = { onDelete(); showMenu = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================================================================
// 2. CONVERSATION VIEW (REAL-TIME MESSAGING)
// ====================================================================
@Composable
private fun ConversationView(
    conversation: ConversationItem,
    allMessages: List<HubMessage>,
    storageItems: List<StorageItem>,
    notes: List<NoteItem>,
    prompts: List<PromptItem>,
    tasks: List<TaskItem>,
    codingProjects: List<CodingProject>,
    onBack: () -> Unit,
    onSendMessage: (
        text: String,
        type: MessageType,
        mediaUrl: String?,
        caption: String?,
        audioDur: Int,
        replyId: String?,
        replyTxt: String?,
        replySender: String?,
        itemTitle: String?,
        itemId: String?,
        itemType: String?
    ) -> Unit,
    onStartVoiceCall: () -> Unit,
    onOpenLiveCamera: () -> Unit,
    onAddReaction: (String, String) -> Unit,
    onToggleStarMessage: (String) -> Unit,
    onTogglePinMessage: (String) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onCopyId: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<HubMessage?>(null) }
    var showSharePicker by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var voiceRecordDuration by remember { mutableIntStateOf(0) }
    var selectedReactionMessage by remember { mutableStateOf<HubMessage?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    // Voice recording timer
    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            voiceRecordDuration = 0
            while (isRecordingVoice) {
                delay(1000)
                voiceRecordDuration += 1
            }
        }
    }

    LaunchedEffect(allMessages.size) {
        if (allMessages.isNotEmpty()) {
            listState.animateScrollToItem(allMessages.size)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Conversation Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepViolet)
                }

                Box {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (conversation.isGroup) Brush.linearGradient(listOf(SpeedCyan, RoyalPurple))
                                else Brush.linearGradient(listOf(PastelPurple, DeepViolet))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(conversation.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    if (conversation.isOnline) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Column {
                    Text(conversation.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepViolet)
                    Text(
                        text = if (conversation.isGroup) "${conversation.memberCount} members • RLS Active" else conversation.lastSeen,
                        fontSize = 11.sp,
                        color = if (conversation.isOnline) Color(0xFF059669) else TextSecondary,
                        fontWeight = if (conversation.isOnline) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                // Live Voice Call Action Button (Starts WhatsApp-style Voice Call)
                IconButton(
                    onClick = onStartVoiceCall,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(Icons.Filled.Call, contentDescription = "Voice Call", tint = RoyalPurple, modifier = Modifier.size(20.dp))
                }

                // Camera Action
                IconButton(
                    onClick = onOpenLiveCamera,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = "Camera", tint = RoyalPurple, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Messages Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
        ) {
            // Date Divider
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftLavender.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple)
                    ) {
                        Text(
                            text = "TODAY • END-TO-END RLS ENCRYPTED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            items(allMessages, key = { it.id }) { msg ->
                MessageBubbleItem(
                    message = msg,
                    onReact = { onAddReaction(msg.id, it) },
                    onReply = { replyingToMessage = msg },
                    onStar = { onToggleStarMessage(msg.id) },
                    onPin = { onTogglePinMessage(msg.id) },
                    onCopy = { clipboard.setText(AnnotatedString(msg.text)) },
                    onDelete = { onDeleteMessage(msg.id) },
                    onCopyId = onCopyId
                )
            }
        }

        // Quoted Reply Preview Bar
        replyingToMessage?.let { replyTarget ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = SoftLavender.copy(alpha = 0.9f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Replying to ${replyTarget.senderName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                        Text(replyTarget.text, fontSize = 12.sp, color = DeepViolet, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = { replyingToMessage = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel reply", tint = TextMuted)
                    }
                }
            }
        }

        // Active Voice Recording Bar (WhatsApp style)
        if (isRecordingVoice) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pulsing Red Recording Indicator
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(DangerRose)
                        )
                        Text(
                            text = "${voiceRecordDuration / 60}:%02d".format(voiceRecordDuration % 60),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DangerRose
                        )
                        Text("Recording Voice Note...", fontSize = 12.sp, color = TextSecondary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Discard
                        IconButton(
                            onClick = { isRecordingVoice = false },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Discard", tint = DangerRose)
                        }

                        // Send Voice Note
                        IconButton(
                            onClick = {
                                val dur = if (voiceRecordDuration == 0) 6 else voiceRecordDuration
                                onSendMessage(
                                    "🎤 Voice note ($dur s)",
                                    MessageType.VOICE_NOTE,
                                    null,
                                    null,
                                    dur,
                                    replyingToMessage?.id,
                                    replyingToMessage?.text,
                                    replyingToMessage?.senderName,
                                    null,
                                    null,
                                    null
                                )
                                isRecordingVoice = false
                                replyingToMessage = null
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoyalPurple)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        } else {
            // Standard Message Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = Color.White.copy(alpha = 0.95f),
                    elevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Share Vault Feature (+) Button
                        IconButton(
                            onClick = { showSharePicker = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftLavender)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Share Feature", tint = RoyalPurple, modifier = Modifier.size(20.dp))
                        }

                        // Live Camera Shutter Button
                        IconButton(
                            onClick = onOpenLiveCamera,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftLavender)
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Live Camera", tint = RoyalPurple, modifier = Modifier.size(18.dp))
                        }

                        // Text Field
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Message...", fontSize = 13.sp, color = TextMuted) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.weight(1f),
                            maxLines = 4
                        )

                        // Action: Send Button (if text entered) OR Hold Mic to Record Voice Note
                        if (inputText.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(SpeedCyan, DeepViolet)))
                                    .clickable {
                                        val txt = inputText
                                        inputText = ""
                                        val reply = replyingToMessage
                                        replyingToMessage = null
                                        onSendMessage(
                                            txt,
                                            MessageType.TEXT,
                                            null,
                                            null,
                                            0,
                                            reply?.id,
                                            reply?.text,
                                            reply?.senderName,
                                            null,
                                            null,
                                            null
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            // WhatsApp Style Voice Note Button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(RoyalPurple, DeepViolet)))
                                    .clickable {
                                        isRecordingVoice = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Mic, contentDescription = "Voice Note", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // Feature Share Bottom Sheet Picker
        if (showSharePicker) {
            ShareVaultFeatureModal(
                storageItems = storageItems,
                notes = notes,
                prompts = prompts,
                tasks = tasks,
                codingProjects = codingProjects,
                onDismiss = { showSharePicker = false },
                onSelectFeature = { title, id, type ->
                    onSendMessage(
                        "Shared $type: $title",
                        MessageType.FEATURE_SHARE,
                        null,
                        null,
                        0,
                        null,
                        null,
                        null,
                        title,
                        id,
                        type
                    )
                    showSharePicker = false
                }
            )
        }
    }
}

// ====================================================================
// 3. MESSAGE BUBBLE COMPONENT
// ====================================================================
@Composable
private fun MessageBubbleItem(
    message: HubMessage,
    onReact: (String) -> Unit,
    onReply: () -> Unit,
    onStar: () -> Unit,
    onPin: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onCopyId: (String) -> Unit
) {
    val isUser = message.isCurrentUser
    var showMenu by remember { mutableStateOf(false) }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var audioSpeed by remember { mutableIntStateOf(1) } // 1x, 2x

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
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
                    else Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.95f), SoftLavender.copy(alpha = 0.85f)))
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
                .clickable { showMenu = true }
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Quoted reply header
                if (message.replyToText != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isUser) Color.White.copy(alpha = 0.2f) else SoftLavender,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text(message.replyToSender ?: "Peer", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isUser) Color.White else RoyalPurple)
                            Text(message.replyToText, fontSize = 11.sp, color = if (isUser) Color.White.copy(alpha = 0.8f) else DeepViolet, maxLines = 1)
                        }
                    }
                }

                // Sender Name if received
                if (!isUser) {
                    Text(message.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                }

                // Render Content Based on Message Type
                when (message.type) {
                    MessageType.VOICE_NOTE -> {
                        // WhatsApp Style In-Chat Voice Note Player
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { isPlayingAudio = !isPlayingAudio },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isUser) Color.White else RoyalPurple)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play voice note",
                                    tint = if (isUser) RoyalPurple else Color.White
                                )
                            }

                            // Dynamic Waveform Bars
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val heights = listOf(14.dp, 22.dp, 18.dp, 28.dp, 12.dp, 24.dp, 16.dp, 20.dp, 26.dp, 14.dp)
                                heights.forEachIndexed { idx, h ->
                                    val barHeight = if (isPlayingAudio && idx % 2 == 0) h * 1.3f else h
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (isUser) Color.White.copy(alpha = 0.85f) else SpeedCyan)
                                    )
                                }
                            }

                            // Duration & Speed Toggle
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "0:%02d".format(message.audioDurationSeconds),
                                    fontSize = 11.sp,
                                    color = if (isUser) Color.White else DeepViolet,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${audioSpeed}x",
                                    fontSize = 10.sp,
                                    color = if (isUser) SpeedCyan else RoyalPurple,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        audioSpeed = if (audioSpeed == 1) 2 else 1
                                    }
                                )
                            }
                        }
                    }

                    MessageType.IMAGE -> {
                        // In-chat Media Preview
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF6366F1), Color(0xFF9333EA)))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                                    Text("Live Camera Capture", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (!message.mediaCaption.isNullOrBlank()) {
                                Text(message.mediaCaption, fontSize = 13.sp, color = if (isUser) Color.White else TextPrimary)
                            }
                        }
                    }

                    MessageType.VOICE_CALL_LOG -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Call, contentDescription = null, tint = if (isUser) Color.White else RoyalPurple, modifier = Modifier.size(16.dp))
                            Text(message.text, fontSize = 12.sp, color = if (isUser) Color.White else TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }

                    MessageType.FEATURE_SHARE -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isUser) Color.White.copy(alpha = 0.2f) else Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when (message.sharedItemType) {
                                        "PROMPT" -> Icons.Filled.Psychology
                                        "NOTE" -> Icons.Filled.EditNote
                                        "CODE" -> Icons.Filled.Code
                                        "TASK" -> Icons.Filled.CheckCircle
                                        else -> Icons.Filled.InsertDriveFile
                                    },
                                    contentDescription = null,
                                    tint = if (isUser) Color.White else RoyalPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(message.sharedItemTitle ?: "Shared Asset", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isUser) Color.White else DeepViolet)
                                    message.sharedItemId?.let { id ->
                                        UniqueIdChip(displayId = id, onCopied = onCopyId)
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                            color = if (isUser) Color.White else TextPrimary
                        )
                    }
                }

                // Footer: Timestamp + WhatsApp Status Ticks (✓, ✓✓)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isStarred) {
                        Icon(Icons.Filled.Star, contentDescription = "Starred", tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = if (isUser) Color.White.copy(alpha = 0.75f) else TextMuted
                    )
                    if (isUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.DoneAll,
                            contentDescription = null,
                            tint = if (message.status == MessageStatus.READ) SpeedCyan else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Options Dropdown
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                // Quick emoji reaction bar
                DropdownMenuItem(
                    text = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("❤️", "👍", "😂", "😮", "🔥").forEach { emoji ->
                                Text(emoji, fontSize = 20.sp, modifier = Modifier.clickable { onReact(emoji); showMenu = false })
                            }
                        }
                    },
                    onClick = {}
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Reply") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null) },
                    onClick = { onReply(); showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Star Message") },
                    leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) },
                    onClick = { onStar(); showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Pin Message") },
                    leadingIcon = { Icon(Icons.Filled.PushPin, contentDescription = null) },
                    onClick = { onPin(); showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Copy Text") },
                    leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null) },
                    onClick = { onCopy(); showMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Delete", color = DangerRose) },
                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = DangerRose) },
                    onClick = { onDelete(); showMenu = false }
                )
            }
        }

        // Reactions Pills Display
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { emoji ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
                        shadowElevation = 2.dp
                    ) {
                        Text(text = emoji, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}

// ====================================================================
// 4. LIVE CAMERA & MEDIA CAPTURE OVERLAY
// ====================================================================
@Composable
private fun LiveCameraMediaOverlay(
    onDismiss: () -> Unit,
    onSendMedia: (type: MessageType, caption: String, filterName: String) -> Unit
) {
    var cameraMode by remember { mutableStateOf("PHOTO") } // PHOTO or VIDEO
    var isRecordingVideo by remember { mutableStateOf(false) }
    var videoSeconds by remember { mutableIntStateOf(0) }
    var isFrontCamera by remember { mutableStateOf(false) }
    var flashMode by remember { mutableStateOf("AUTO") } // AUTO, ON, OFF
    var zoomLevel by remember { mutableStateOf("1x") }
    var capturedPreviewReady by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Normal") }
    var captionText by remember { mutableStateOf("") }

    val filterList = listOf("Normal", "Liquid Pearl", "Cyber Violet", "Sunset Clay", "Noir Mono")

    LaunchedEffect(isRecordingVideo) {
        if (isRecordingVideo) {
            videoSeconds = 0
            while (isRecordingVideo) {
                delay(1000)
                videoSeconds += 1
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (!capturedPreviewReady) {
            // Live Viewfinder Screen
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Camera Controls Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Camera", tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    // Flash Toggle
                    IconButton(onClick = {
                        flashMode = when (flashMode) {
                            "AUTO" -> "ON"
                            "ON" -> "OFF"
                            else -> "AUTO"
                        }
                    }) {
                        Icon(
                            imageVector = when (flashMode) {
                                "ON" -> Icons.Filled.FlashOn
                                "OFF" -> Icons.Filled.FlashOff
                                else -> Icons.Filled.FlashAuto
                            },
                            contentDescription = "Flash",
                            tint = Color.White
                        )
                    }

                    // Front/Rear Camera Flip
                    IconButton(onClick = { isFrontCamera = !isFrontCamera }) {
                        Icon(Icons.Filled.Cameraswitch, contentDescription = "Flip Camera", tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }

                // Viewfinder Simulated Optics Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                if (isFrontCamera) listOf(Color(0xFF2E1065), Color(0xFF0F172A))
                                else listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Optical Focus Reticle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(1.5.dp, SpeedCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    )

                    // Mode Badge / Recording Timer
                    if (isRecordingVideo) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 16.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(DangerRose))
                            Text("● 00:%02d".format(videoSeconds), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Zoom Chips
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("0.5x", "1x", "2x", "5x").forEach { z ->
                            Surface(
                                onClick = { zoomLevel = z },
                                shape = CircleShape,
                                color = if (zoomLevel == z) RoyalPurple else Color.Black.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                            ) {
                                Text(z, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                    }
                }

                // Bottom Shutter & Mode Switcher
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Photo / Video Toggle
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Text(
                            text = "PHOTO",
                            color = if (cameraMode == "PHOTO") SpeedCyan else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { cameraMode = "PHOTO"; isRecordingVideo = false }
                        )
                        Text(
                            text = "VIDEO",
                            color = if (cameraMode == "VIDEO") SpeedCyan else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { cameraMode = "VIDEO" }
                        )
                    }

                    // Shutter Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (cameraMode == "VIDEO") DangerRose else Color.White
                            )
                            .clickable {
                                if (cameraMode == "PHOTO") {
                                    capturedPreviewReady = true
                                } else {
                                    if (isRecordingVideo) {
                                        isRecordingVideo = false
                                        capturedPreviewReady = true
                                    } else {
                                        isRecordingVideo = true
                                    }
                                }
                            }
                    )
                }
            }
        } else {
            // Captured Media Review & Filter Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { capturedPreviewReady = false }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retake", tint = Color.White)
                    }
                    Text(
                        text = if (cameraMode == "PHOTO") "Photo Preview" else "Video Preview (${videoSeconds}s)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = Color.White)
                    }
                }

                // Media Preview with Applied Filter
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when (selectedFilter) {
                                "Cyber Violet" -> Brush.verticalGradient(listOf(Color(0xFF6B21A8), Color(0xFF0F172A)))
                                "Sunset Clay" -> Brush.verticalGradient(listOf(Color(0xFFEA580C), Color(0xFF7C2D12)))
                                "Noir Mono" -> Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF000000)))
                                else -> Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF312E81)))
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = if (cameraMode == "PHOTO") Icons.Filled.Photo else Icons.Filled.Videocam,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                        Text("Filter: $selectedFilter", color = SpeedCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Filter Selector Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    items(filterList) { f ->
                        Surface(
                            onClick = { selectedFilter = f },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selectedFilter == f) RoyalPurple else Color.White.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                        ) {
                            Text(f, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }

                // Caption & Send Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        placeholder = { Text("Add a caption...", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SpeedCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.weight(1f)
                    )

                    FloatingActionButton(
                        onClick = {
                            val type = if (cameraMode == "PHOTO") MessageType.IMAGE else MessageType.VIDEO
                            onSendMedia(type, captionText, selectedFilter)
                        },
                        containerColor = RoyalPurple,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Media")
                    }
                }
            }
        }
    }
}

// ====================================================================
// 5. LIVE VOICE CALL OVERLAY (AUDIO ONLY)
// ====================================================================
@Composable
private fun VoiceCallOverlay(
    callState: VoiceCallState,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit
) {
    var callTimer by remember { mutableIntStateOf(1) }

    val infinitePulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infinitePulse.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callTimer += 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepViolet, Color(0xFF1E103A), Color(0xFF0F0B1E))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Call Header Info
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "🔒 E2EE Real-Time Voice Call • HD Audio",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpeedCyan,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Text(callState.contactName, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "${callTimer / 60}:%02d".format(callTimer % 60),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            // Central Pulsing Avatar & Audio Waves
            Box(contentAlignment = Alignment.Center) {
                // Outer Wave 1
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(SpeedCyan.copy(alpha = 0.15f))
                )
                // Outer Wave 2
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(RoyalPurple.copy(alpha = 0.35f))
                )
                // Main Contact Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(SpeedCyan, DeepViolet))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = callState.contactName.take(2).uppercase(),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // Bottom Call Controls (Mute, Speaker, Bluetooth, End Call)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Microphone
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (callState.isMuted) DangerRose else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (callState.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Speakerphone Toggle
                    IconButton(
                        onClick = onToggleSpeaker,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (callState.isSpeakerOn) RoyalPurple else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = if (callState.isSpeakerOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeDown,
                            contentDescription = "Speaker",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Bluetooth Headset Indicator
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Filled.Bluetooth, contentDescription = "Bluetooth", tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }

                // End Voice Call (Red Circle)
                FloatingActionButton(
                    onClick = onEndCall,
                    containerColor = DangerRose,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(68.dp)
                ) {
                    Icon(Icons.Filled.CallEnd, contentDescription = "End Call", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

// ====================================================================
// 6. DIALOGS (NEW CHAT, CREATE GROUP, SHARE FEATURE)
// ====================================================================
@Composable
private fun NewChatDialog(
    friendUsers: List<FriendUser>,
    onDismiss: () -> Unit,
    onSelectFriend: (FriendUser) -> Unit,
    onCopyId: (String) -> Unit
) {
    var customIdInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Peer Chat", fontWeight = FontWeight.Bold, color = DeepViolet) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select from your verified contacts or enter a peer's Unique User ID:", fontSize = 12.sp, color = TextSecondary)

                OutlinedTextField(
                    value = customIdInput,
                    onValueChange = { customIdInput = it },
                    placeholder = { Text("Enter ID (e.g. QK-USR-3301)") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (customIdInput.isNotBlank()) {
                    ClayButton(
                        onClick = {
                            val dummy = FriendUser(displayId = customIdInput.trim(), name = "Peer ${customIdInput.takeLast(4)}")
                            onSelectFriend(dummy)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Connect & Open Chat", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider()

                LazyColumn(modifier = Modifier.heightIn(max = 240.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(friendUsers) { friend ->
                        Surface(
                            onClick = { onSelectFriend(friend) },
                            shape = RoundedCornerShape(14.dp),
                            color = SoftLavender.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(friend.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepViolet)
                                UniqueIdChip(displayId = friend.displayId, onCopied = onCopyId)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CreateGroupChatDialog(
    friendUsers: List<FriendUser>,
    onDismiss: () -> Unit,
    onCreateGroup: (name: String, members: List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Collaborative Group", fontWeight = FontWeight.Bold, color = DeepViolet) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    placeholder = { Text("e.g. Research & Architecture") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("All members will join under Row Level Security (RLS) encrypted vault isolation.", fontSize = 11.sp, color = TextSecondary)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (groupName.isNotBlank()) {
                        onCreateGroup(groupName, friendUsers.map { it.name })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
            ) {
                Text("Create Group")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareVaultFeatureModal(
    storageItems: List<StorageItem>,
    notes: List<NoteItem>,
    prompts: List<PromptItem>,
    tasks: List<TaskItem>,
    codingProjects: List<CodingProject>,
    onDismiss: () -> Unit,
    onSelectFeature: (title: String, id: String, type: String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Share Feature into Chat", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepViolet)
            Text("Select any saved asset from your vault to share:", fontSize = 12.sp, color = TextSecondary)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Text("Vault Files", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalPurple) }
                items(storageItems.take(3)) { item ->
                    SharePickerRow(item.name, item.displayId, item.formattedSize, Icons.Filled.InsertDriveFile) {
                        onSelectFeature(item.name, item.displayId, "FILE")
                    }
                }

                item { Text("Personal Notes", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalPurple) }
                items(notes.take(2)) { n ->
                    SharePickerRow(n.title, n.displayId, n.folderName, Icons.Filled.EditNote) {
                        onSelectFeature(n.title, n.displayId, "NOTE")
                    }
                }

                item { Text("AI Prompts", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalPurple) }
                items(prompts.take(2)) { p ->
                    SharePickerRow(p.title, p.displayId, p.category, Icons.Filled.Psychology) {
                        onSelectFeature(p.title, p.displayId, "PROMPT")
                    }
                }

                item { Text("Coding Projects", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalPurple) }
                items(codingProjects.take(2)) { c ->
                    SharePickerRow(c.title, c.displayId, c.languages.firstOrNull() ?: "Code", Icons.Filled.Code) {
                        onSelectFeature(c.title, c.displayId, "CODE")
                    }
                }

                item { Text("Daily Tasks", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalPurple) }
                items(tasks.take(2)) { t ->
                    SharePickerRow(t.title, t.displayId, t.dueTime, Icons.Filled.CheckCircle) {
                        onSelectFeature(t.title, t.displayId, "TASK")
                    }
                }
            }
        }
    }
}

@Composable
private fun SharePickerRow(
    title: String,
    displayId: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = SoftLavender.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = RoyalPurple, modifier = Modifier.size(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepViolet, maxLines = 1)
                Text("$subtitle • $displayId", fontSize = 10.sp, color = TextSecondary)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
}
