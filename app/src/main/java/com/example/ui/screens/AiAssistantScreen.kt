package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.ChatMessage
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    onCopyId: (String) -> Unit,
    onOpenResource: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isListeningVoice by remember { mutableStateOf(false) }
    var selectedActionPill by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickActionPills = remember {
        listOf(
            "Search vault files",
            "Summarize saved PDFs",
            "Explain 3D Clay UI",
            "Find AI prompts",
            "Generate Kotlin code",
            "Check cloud storage"
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    ClayBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepViolet
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HubbyAiMascot(size = 32.dp, animateEyes = true)
                    Text(
                        text = "Hubby AI",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepViolet
                    )
                    UniqueIdChip(displayId = "QK-HUBBY-AI", onCopied = onCopyId)
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(SpeedCyan.copy(alpha = 0.4f), SoftLavender))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI Mode",
                        tint = RoyalPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Conversation Scrollable Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                // Top Centered Animated Hubby AI Mascot with looking eyes!
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HubbyAiMascot(
                            size = 140.dp,
                            animateEyes = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isListeningVoice) "Listening to voice input..." else "Hubby AI Online • Neural Vision Mascot",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isListeningVoice) DangerRose else DeepViolet
                        )
                    }
                }

                // Quick AI Actions Pills
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Quick AI Operations",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickActionPills) { pill ->
                                Surface(
                                    onClick = {
                                        selectedActionPill = pill
                                        onSendMessage(pill)
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.White.copy(alpha = 0.85f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.testTag("ai_pill_${pill.take(8).replace(" ", "_").lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = null,
                                            tint = RoyalPurple,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = pill,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = DeepViolet
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Chat Messages
                items(messages) { msg ->
                    ChatBubble(
                        message = msg,
                        onCopyId = onCopyId,
                        onOpenResource = onOpenResource
                    )
                }
            }

            // Frosted Translucent Message Composer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    backgroundColor = Color.White.copy(alpha = 0.94f),
                    elevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Attachment Button
                        IconButton(
                            onClick = {
                                onSendMessage("Attached file 'Vault_Asset.png' for analysis")
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SoftLavender.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AttachFile,
                                contentDescription = "Attach file",
                                tint = DeepViolet,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Text Field
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = "Ask Quick it or search files...",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_message_input"),
                            maxLines = 3
                        )

                        // Glossy Voice Input Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isListeningVoice) androidx.compose.ui.graphics.SolidColor(DangerRose)
                                    else Brush.linearGradient(listOf(PastelPurple, RoyalPurple))
                                )
                                .clickable {
                                    isListeningVoice = !isListeningVoice
                                    if (isListeningVoice) {
                                        inputText = "Summarize my cloud vault files and 3D clay guidelines"
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListeningVoice) Icons.Filled.MicOff else Icons.Filled.Mic,
                                contentDescription = "Voice input",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Send Button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(listOf(SpeedCyan, DeepViolet))
                                )
                                .clickable(
                                    enabled = inputText.isNotBlank(),
                                    onClick = {
                                        val text = inputText
                                        inputText = ""
                                        isListeningVoice = false
                                        onSendMessage(text)
                                    }
                                )
                                .testTag("ai_send_button"),
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
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onCopyId: (String) -> Unit,
    onOpenResource: (String) -> Unit
) {
    val isUser = message.isUser

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomStart = if (isUser) 22.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 22.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.linearGradient(listOf(RoyalPurple, DeepViolet))
                    } else {
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.95f), SoftLavender.copy(alpha = 0.85f))
                        )
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) Color.Transparent else GlassBorderPurple,
                    shape = RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomStart = if (isUser) 22.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 22.dp
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HubbyAiMascot(size = 20.dp, animateEyes = true)
                        Text(
                            text = "Hubby AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepViolet
                        )
                    }
                }

                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = if (isUser) Color.White else TextPrimary
                )

                // Referenced Vault Resource Card if returned
                if (message.referencedResource != null && message.referencedResourceId != null) {
                    Surface(
                        onClick = { onOpenResource(message.referencedResourceId) },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.InsertDriveFile,
                                contentDescription = null,
                                tint = DeepViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = message.referencedResource,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepViolet,
                                    maxLines = 1
                                )
                                UniqueIdChip(
                                    displayId = message.referencedResourceId,
                                    onCopied = onCopyId
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = message.timestamp,
            fontSize = 10.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
