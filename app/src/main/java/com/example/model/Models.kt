package com.example.model

import java.util.UUID

enum class FileCategory(val displayName: String, val iconName: String) {
    PHOTOS("Photos", "image"),
    PDFS("PDFs", "picture_as_pdf"),
    VIDEOS("Videos", "videocam"),
    NOTES("Notes", "edit_note"),
    AI_PROMPTS("AI Prompts", "psychology"),
    LINKS("Links", "link"),
    SOCIAL("Social", "share"),
    CODE("Code", "code"),
    DOCUMENTS("Docs", "description"),
    OTHER("Other", "folder_open")
}

data class StorageItem(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-FIL-" + (1000..9999).random(),
    val name: String,
    val category: FileCategory,
    val sizeBytes: Long,
    val formattedSize: String,
    val folderId: String? = null,
    val folderPath: String = "/My Vault",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val createdAt: String = "Just now",
    val contentSnippet: String = "",
    val url: String? = null
)

data class FolderItem(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-FLD-" + (1000..9999).random(),
    val name: String,
    val category: FileCategory = FileCategory.OTHER,
    val itemCount: Int = 0,
    val parentFolderId: String? = null,
    val path: String = "/My Vault",
    val isFavorite: Boolean = false,
    val colorHex: Long = 0xFF7955D9,
    val createdAt: String = "Today"
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Now",
    val suggestedAction: String? = null,
    val referencedResource: String? = null,
    val referencedResourceId: String? = null
)

data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-NOT-" + (1000..9999).random(),
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val folderName: String = "General",
    val updatedAt: String = "Today",
    val isPinned: Boolean = false
)

data class PromptItem(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-PRM-" + (1000..9999).random(),
    val title: String,
    val promptText: String,
    val category: String = "General",
    val model: String = "Gemini 2.5",
    val isFavorite: Boolean = false
)

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-TSK-" + (1000..9999).random(),
    val title: String,
    val dueTime: String,
    val isCompleted: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: String = "Daily"
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH, URGENT
}

data class CodingProject(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-PRJ-" + (1000..9999).random(),
    val title: String,
    val description: String,
    val status: String = "Active",
    val repoUrl: String = "https://github.com/myproject/quickit",
    val languages: List<String> = listOf("Kotlin", "Compose", "TypeScript"),
    val snippet: String = ""
)

data class LearningSubject(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-LRN-" + (1000..9999).random(),
    val title: String,
    val progress: Float, // 0.0f to 1.0f
    val currentTopic: String,
    val totalModules: Int = 12,
    val completedModules: Int = 7
)

enum class GroupRole {
    OWNER, ADMIN, EDITOR, VIEWER
}

data class GroupHub(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-GRP-" + (1000..9999).random(),
    val name: String,
    val description: String,
    val isPrivate: Boolean = true,
    val inviteCode: String = "QK" + (10000..99999).random(),
    val memberCount: Int = 1,
    val userRole: GroupRole = GroupRole.OWNER,
    val sharedFilesCount: Int = 4,
    val bannerGradientStart: Long = 0xFF7955D9,
    val bannerGradientEnd: Long = 0xFF5142BA
)

data class UserProfile(
    val id: String = "usr_quickit_9921",
    val displayId: String = "QK-USR-8821",
    val name: String = "Alex Rivera",
    val email: String = "alex.rivera@quickit.hub",
    val plan: String = "Cloud Pro Plus",
    val totalStorageBytes: Long = 100L * 1024 * 1024 * 1024, // 100 GB
    val usedStorageBytes: Long = 28L * 1024 * 1024 * 1024, // 28 GB
    val avatarInitials: String = "AR"
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

enum class MessageType {
    TEXT, IMAGE, VIDEO, VOICE_NOTE, FEATURE_SHARE, VOICE_CALL_LOG
}

enum class CallStatus {
    IDLE, OUTGOING_RINGING, INCOMING_RINGING, CONNECTED, ENDED
}

data class VoiceCallState(
    val callId: String = UUID.randomUUID().toString(),
    val contactName: String = "",
    val contactId: String = "",
    val isGroup: Boolean = false,
    val isIncoming: Boolean = false,
    val status: CallStatus = CallStatus.IDLE,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isBluetoothOn: Boolean = false
)

data class ConversationItem(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String = "QK-CHT-" + (1000..9999).random(),
    val name: String,
    val avatarInitials: String,
    val isGroup: Boolean = false,
    val isOnline: Boolean = false,
    val lastSeen: String = "Active now",
    val lastMessage: String = "",
    val lastMessageTime: String = "10:30 AM",
    val lastMessageStatus: MessageStatus = MessageStatus.READ,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val typingUser: String? = null,
    val draftText: String = "",
    val memberCount: Int = 1,
    val rlsPolicy: String = "RLS Encrypted",
    val friendUserId: String? = null
)

data class HubMessage(
    val id: String = UUID.randomUUID().toString(),
    val hubId: String,
    val senderName: String,
    val senderUserId: String, // QK-USR-...
    val isCurrentUser: Boolean = false,
    val text: String,
    val timestamp: String = "Just now",
    val status: MessageStatus = MessageStatus.READ,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaCaption: String? = null,
    val audioDurationSeconds: Int = 0,
    val isStarred: Boolean = false,
    val isPinned: Boolean = false,
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val replyToSender: String? = null,
    val reactions: List<String> = emptyList(), // e.g. ["❤️", "👍"]
    val sharedItemTitle: String? = null,
    val sharedItemId: String? = null,
    val sharedItemType: String? = null // FILE, NOTE, PROMPT, CODE, TASK
)

data class FriendUser(
    val id: String = UUID.randomUUID().toString(),
    val displayId: String,
    val name: String,
    val role: GroupRole = GroupRole.EDITOR,
    val rlsPolicy: String = "RLS Policy: Vault Isolated (Decrypted On-Demand)",
    val isInvited: Boolean = false
)
