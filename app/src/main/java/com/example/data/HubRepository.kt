package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class HubRepository {

    private val _currentUser = MutableStateFlow(UserProfile())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _storageItems = MutableStateFlow<List<StorageItem>>(emptyList())
    val storageItems: StateFlow<List<StorageItem>> = _storageItems.asStateFlow()

    private val _folders = MutableStateFlow<List<FolderItem>>(emptyList())
    val folders: StateFlow<List<FolderItem>> = _folders.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteItem>>(emptyList())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _prompts = MutableStateFlow<List<PromptItem>>(emptyList())
    val prompts: StateFlow<List<PromptItem>> = _prompts.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _codingProjects = MutableStateFlow<List<CodingProject>>(emptyList())
    val codingProjects: StateFlow<List<CodingProject>> = _codingProjects.asStateFlow()

    private val _learningSubjects = MutableStateFlow<List<LearningSubject>>(emptyList())
    val learningSubjects: StateFlow<List<LearningSubject>> = _learningSubjects.asStateFlow()

    private val _groups = MutableStateFlow<List<GroupHub>>(emptyList())
    val groups: StateFlow<List<GroupHub>> = _groups.asStateFlow()

    private val _hubMessages = MutableStateFlow<List<HubMessage>>(emptyList())
    val hubMessages: StateFlow<List<HubMessage>> = _hubMessages.asStateFlow()

    private val _conversations = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversations: StateFlow<List<ConversationItem>> = _conversations.asStateFlow()

    private val _activeVoiceCall = MutableStateFlow<VoiceCallState?>(null)
    val activeVoiceCall: StateFlow<VoiceCallState?> = _activeVoiceCall.asStateFlow()

    private val _friendUsers = MutableStateFlow<List<FriendUser>>(emptyList())
    val friendUsers: StateFlow<List<FriendUser>> = _friendUsers.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val seedFolders = listOf(
            FolderItem(
                id = "fld-1",
                displayId = "QK-FLD-1021",
                name = "AI Research & Prompts",
                category = FileCategory.AI_PROMPTS,
                itemCount = 6,
                path = "/My Vault/AI Research",
                isFavorite = true,
                colorHex = 0xFF7955D9
            ),
            FolderItem(
                id = "fld-2",
                displayId = "QK-FLD-2044",
                name = "Cloud Architecture Docs",
                category = FileCategory.PDFS,
                itemCount = 8,
                path = "/My Vault/Docs",
                isFavorite = true,
                colorHex = 0xFF5142BA
            ),
            FolderItem(
                id = "fld-3",
                displayId = "QK-FLD-3091",
                name = "Mobile App Design Assets",
                category = FileCategory.PHOTOS,
                itemCount = 14,
                path = "/My Vault/Design",
                isFavorite = true,
                colorHex = 0xFF00B4D8
            ),
            FolderItem(
                id = "fld-4",
                displayId = "QK-FLD-4112",
                name = "Kotlin & Compose Code",
                category = FileCategory.CODE,
                itemCount = 11,
                path = "/My Vault/Coding",
                isFavorite = false,
                colorHex = 0xFFC5A8F5
            ),
            FolderItem(
                id = "fld-5",
                displayId = "QK-FLD-5231",
                name = "Social Tech Bookmarks",
                category = FileCategory.SOCIAL,
                itemCount = 5,
                path = "/My Vault/Bookmarks",
                isFavorite = false,
                colorHex = 0xFFF59E0B
            )
        )
        _folders.value = seedFolders

        val seedFiles = listOf(
            StorageItem(
                id = "file-1",
                displayId = "QK-FIL-8101",
                name = "Quick_It_3D_Clay_Design_Spec.pdf",
                category = FileCategory.PDFS,
                sizeBytes = 4_200_000L,
                formattedSize = "4.2 MB",
                folderId = "fld-2",
                folderPath = "/My Vault/Docs",
                description = "Complete UI/UX specifications for 3D Claymorphism and Liquid Glass interfaces.",
                tags = listOf("Design", "3D", "UI/UX"),
                isFavorite = true,
                createdAt = "Yesterday",
                contentSnippet = "Claymorphism uses rounded corners, dual pastel drop shadows and pearlescent highlights..."
            ),
            StorageItem(
                id = "file-2",
                displayId = "QK-FIL-8102",
                name = "Gemini_Multimodal_Agent_System.kt",
                category = FileCategory.CODE,
                sizeBytes = 85_000L,
                formattedSize = "85 KB",
                folderId = "fld-4",
                folderPath = "/My Vault/Coding",
                description = "Production coroutine flow for streaming AI responses with tool calls.",
                tags = listOf("Kotlin", "Gemini", "AI"),
                isFavorite = true,
                createdAt = "2 hrs ago",
                contentSnippet = "suspend fun streamAssistantResponse(prompt: String): Flow<AiChunk> = flow { ... }"
            ),
            StorageItem(
                id = "file-3",
                displayId = "QK-FIL-8103",
                name = "Orb_Mascot_3D_Render.png",
                category = FileCategory.PHOTOS,
                sizeBytes = 2_800_000L,
                formattedSize = "2.8 MB",
                folderId = "fld-3",
                folderPath = "/My Vault/Design",
                description = "Translucent liquid glass AI orb with violet core and ambient glow particles.",
                tags = listOf("Asset", "Mascot", "3D"),
                isFavorite = true,
                createdAt = "Today",
                contentSnippet = "Image resolution: 2048x2048 RGBA"
            ),
            StorageItem(
                id = "file-4",
                displayId = "QK-FIL-8104",
                name = "Personal_Vault_Security_Architecture.txt",
                category = FileCategory.NOTES,
                sizeBytes = 32_000L,
                formattedSize = "32 KB",
                folderId = "fld-2",
                folderPath = "/My Vault/Docs",
                description = "Zero-trust encryption and role-based group access rules.",
                tags = listOf("Security", "Vault", "Cloud"),
                isFavorite = false,
                createdAt = "3 days ago",
                contentSnippet = "All user vaults are partitioned by secure UUIDs with cryptographic access tokens."
            ),
            StorageItem(
                id = "file-5",
                displayId = "QK-FIL-8105",
                name = "Autonomous_Agent_Master_Prompt.prompt",
                category = FileCategory.AI_PROMPTS,
                sizeBytes = 18_000L,
                formattedSize = "18 KB",
                folderId = "fld-1",
                folderPath = "/My Vault/AI Research",
                description = "Prompt chain for high-precision document extraction and knowledge graph linking.",
                tags = listOf("Prompt", "LLM", "Agent"),
                isFavorite = true,
                createdAt = "4 days ago",
                contentSnippet = "You are Quick It's Neural Core. Analyze the attached schema and provide JSON outputs..."
            ),
            StorageItem(
                id = "file-6",
                displayId = "QK-FIL-8106",
                name = "Supabase_Postgres_Schema_v2.sql",
                category = FileCategory.CODE,
                sizeBytes = 120_000L,
                formattedSize = "120 KB",
                folderId = "fld-4",
                folderPath = "/My Vault/Coding",
                description = "Row Level Security (RLS) tables for files, notes, folders, and shared groups.",
                tags = listOf("Database", "SQL", "Supabase"),
                isFavorite = false,
                createdAt = "1 week ago",
                contentSnippet = "CREATE TABLE user_vault_items (id UUID PRIMARY KEY, display_id TEXT UNIQUE...);"
            ),
            StorageItem(
                id = "file-7",
                displayId = "QK-FIL-8107",
                name = "DeepLearning_Transformer_Walkthrough.mp4",
                category = FileCategory.VIDEOS,
                sizeBytes = 18_500_000L,
                formattedSize = "18.5 MB",
                folderId = "fld-1",
                folderPath = "/My Vault/AI Research",
                description = "Animated 3D visual explanation of self-attention mechanisms and query-key dot products.",
                tags = listOf("AI", "Video", "Lecture"),
                isFavorite = false,
                createdAt = "May 2026",
                contentSnippet = "Video duration: 12:45, 1080p 60fps"
            ),
            StorageItem(
                id = "file-8",
                displayId = "QK-FIL-8108",
                name = "NextJS_App_Router_Best_Practices.link",
                category = FileCategory.LINKS,
                sizeBytes = 4_000L,
                formattedSize = "4 KB",
                folderId = "fld-5",
                folderPath = "/My Vault/Bookmarks",
                description = "Comprehensive guide on React Server Components and edge caching strategies.",
                tags = listOf("Web", "Next.js", "Guide"),
                isFavorite = false,
                createdAt = "Yesterday",
                contentSnippet = "https://nextjs.org/docs/app/building-your-application/routing",
                url = "https://nextjs.org/docs/app/building-your-application/routing"
            ),
            StorageItem(
                id = "file-9",
                displayId = "QK-FIL-8109",
                name = "Google_AI_Studio_Gemini_API.link",
                category = FileCategory.LINKS,
                sizeBytes = 5_200L,
                formattedSize = "5.2 KB",
                folderId = "fld-1",
                folderPath = "/My Vault/AI Research",
                description = "Developer portal for Gemini models, multimodal prompting, and API keys.",
                tags = listOf("AI", "Gemini", "API", "Google"),
                isFavorite = true,
                createdAt = "Today",
                contentSnippet = "https://aistudio.google.com",
                url = "https://aistudio.google.com"
            ),
            StorageItem(
                id = "file-10",
                displayId = "QK-FIL-8110",
                name = "Android_Jetpack_Compose_Docs.link",
                category = FileCategory.LINKS,
                sizeBytes = 3_500L,
                formattedSize = "3.5 KB",
                folderId = "fld-4",
                folderPath = "/My Vault/Coding",
                description = "Official Android modern declarative UI documentation and component guides.",
                tags = listOf("Android", "Compose", "Kotlin", "Docs"),
                isFavorite = true,
                createdAt = "2 days ago",
                contentSnippet = "https://developer.android.com/jetpack/compose",
                url = "https://developer.android.com/jetpack/compose"
            )
        )
        _storageItems.value = seedFiles

        _chatMessages.value = listOf(
            ChatMessage(
                text = "Hello Alex! I am Hubby AI, your personal companion. I have indexed your 8 vault files and 5 smart folders. How can I assist you today?",
                isUser = false,
                timestamp = "09:30 AM",
                suggestedAction = "Search personal files"
            ),
            ChatMessage(
                text = "What design principles do we use for the mobile app interface?",
                isUser = true,
                timestamp = "09:31 AM"
            ),
            ChatMessage(
                text = "According to your saved document 'Quick_It_3D_Clay_Design_Spec.pdf' (ID: QK-FIL-8101):\n\n• 3D Claymorphism: 24–36px rounded corners, dual ambient drop shadows, and soft pill buttons.\n• Liquid Glass UI: Translucent frosted glass cards with subtle white highlights.\n• Palette: Pearl White (#F9F8FF), Soft Lavender (#E8D9FF), Royal Purple (#7955D9), and Deep Violet (#5142BA).\n\nWould you like me to extract the code snippets or summarize another file?",
                isUser = false,
                timestamp = "09:31 AM",
                suggestedAction = "Open spec document",
                referencedResource = "Quick_It_3D_Clay_Design_Spec.pdf",
                referencedResourceId = "QK-FIL-8101"
            )
        )

        _notes.value = listOf(
            NoteItem(
                id = "not-1",
                displayId = "QK-NOT-5011",
                title = "Cross-Platform Sync Roadmap",
                content = "1. Sync Android Room database with Supabase Realtime.\n2. Support offline write queue with optimistic updates.\n3. Encrypt file payloads client-side with AES-256.\n4. Shared Google OAuth tokens for mobile and desktop web.",
                tags = listOf("Roadmap", "Sync", "Architecture"),
                folderName = "Docs",
                updatedAt = "Today, 10:15 AM",
                isPinned = true
            ),
            NoteItem(
                id = "not-2",
                displayId = "QK-NOT-5012",
                title = "AI Prompt Engineering Insights",
                content = "Always specify output schemas as typed Pydantic or Kotlin models. Provide 2-shot examples for edge cases. Keep temperature around 0.3 for retrieval and classification.",
                tags = listOf("AI", "Prompts"),
                folderName = "AI Research",
                updatedAt = "Yesterday",
                isPinned = false
            )
        )

        _prompts.value = listOf(
            PromptItem(
                id = "prm-1",
                displayId = "QK-PRM-3001",
                title = "Smart File Semantic Search",
                promptText = "Given user query: '{query}', scan metadata, keywords, tags and extract the top 3 most relevant files with explanation of why each matches.",
                category = "Search",
                model = "Gemini 2.5 Flash",
                isFavorite = true
            ),
            PromptItem(
                id = "prm-2",
                displayId = "QK-PRM-3002",
                title = "Executive PDF Summarizer",
                promptText = "Summarize the key decisions, architectural trade-offs, and action items in this document into 5 concise bullet points.",
                category = "Productivity",
                model = "Gemini 2.5 Pro",
                isFavorite = true
            ),
            PromptItem(
                id = "prm-3",
                displayId = "QK-PRM-3003",
                title = "Clay UI Component Generator",
                promptText = "Generate a Jetpack Compose composable with 28dp rounded corners, frosted translucent brush background, subtle ambient shadow and tactile ripple.",
                category = "Coding",
                model = "Gemini 2.5 Flash",
                isFavorite = false
            )
        )

        _tasks.value = listOf(
            TaskItem(
                id = "tsk-1",
                displayId = "QK-TSK-401",
                title = "Review 3D Claymorphism assets",
                dueTime = "Today, 2:00 PM",
                isCompleted = false,
                priority = TaskPriority.HIGH,
                category = "Design"
            ),
            TaskItem(
                id = "tsk-2",
                displayId = "QK-TSK-402",
                title = "Verify Google OAuth flow and token exchange",
                dueTime = "Today, 4:30 PM",
                isCompleted = false,
                priority = TaskPriority.URGENT,
                category = "Auth"
            ),
            TaskItem(
                id = "tsk-3",
                displayId = "QK-TSK-403",
                title = "Audit unique ID indexing in File Vault",
                dueTime = "Tomorrow",
                isCompleted = true,
                priority = TaskPriority.MEDIUM,
                category = "Security"
            )
        )

        _codingProjects.value = listOf(
            CodingProject(
                id = "prj-1",
                displayId = "QK-PRJ-701",
                title = "quickit-mobile-android",
                description = "Native Android app built with Jetpack Compose, Material 3, and 3D Clay liquid glass aesthetics.",
                status = "Active",
                repoUrl = "https://github.com/myproject/quickit-android",
                languages = listOf("Kotlin", "Jetpack Compose", "Coroutines"),
                snippet = "@Composable\nfun ClayCard(modifier: Modifier = Modifier) { ... }"
            ),
            CodingProject(
                id = "prj-2",
                displayId = "QK-PRJ-702",
                title = "quickit-web-frontend",
                description = "Responsive web application built with Next.js, Tailwind CSS, and Supabase integration.",
                status = "Deployed",
                repoUrl = "https://github.com/myproject/quickit-web",
                languages = listOf("TypeScript", "Next.js", "Tailwind"),
                snippet = "export default function HubDashboard() { return <GlassPanel /> }"
            )
        )

        _learningSubjects.value = listOf(
            LearningSubject(
                id = "lrn-1",
                displayId = "QK-LRN-101",
                title = "Advanced AI Systems & Prompting",
                progress = 0.85f,
                currentTopic = "Recursive Chain-of-Thought & Multi-Agent Lobbies",
                totalModules = 12,
                completedModules = 10
            ),
            LearningSubject(
                id = "lrn-2",
                displayId = "QK-LRN-102",
                title = "Distributed Cloud Architecture",
                progress = 0.65f,
                currentTopic = "Global Sharding, Raft Consensus & Edge Caching",
                totalModules = 10,
                completedModules = 6
            ),
            LearningSubject(
                id = "lrn-3",
                displayId = "QK-LRN-103",
                title = "3D UI & Spatial Interaction",
                progress = 0.40f,
                currentTopic = "Material Shadows, Clay Depth & Lighting Shaders",
                totalModules = 8,
                completedModules = 3
            )
        )

        _groups.value = listOf(
            GroupHub(
                id = "grp-1",
                displayId = "QK-GRP-901",
                name = "Quick It Core Engineers",
                description = "Engineering community for cross-platform cloud sync and AI agent development.",
                isPrivate = true,
                inviteCode = "QK-DEV-77",
                memberCount = 12,
                userRole = GroupRole.OWNER,
                sharedFilesCount = 16,
                bannerGradientStart = 0xFF7955D9,
                bannerGradientEnd = 0xFF5142BA
            ),
            GroupHub(
                id = "grp-2",
                displayId = "QK-GRP-902",
                name = "AI Product Innovators",
                description = "Public community sharing high-converting AI prompts, design systems, and workflow hacks.",
                isPrivate = false,
                inviteCode = "QK-PUB-24",
                memberCount = 148,
                userRole = GroupRole.EDITOR,
                sharedFilesCount = 42,
                bannerGradientStart = 0xFF00B4D8,
                bannerGradientEnd = 0xFF5142BA
            )
        )

        _conversations.value = listOf(
            ConversationItem(
                id = "conv-elena",
                displayId = "QK-CHT-3301",
                name = "Elena Rostova",
                avatarInitials = "ER",
                isGroup = false,
                isOnline = true,
                lastSeen = "Online",
                lastMessage = "Here's the new 3D Clay theme shader for Compose",
                lastMessageTime = "10:42 AM",
                lastMessageStatus = MessageStatus.READ,
                unreadCount = 0,
                isPinned = true,
                friendUserId = "QK-USR-3301"
            ),
            ConversationItem(
                id = "conv-marcus",
                displayId = "QK-CHT-5512",
                name = "Marcus Vance",
                avatarInitials = "MV",
                isGroup = false,
                isOnline = true,
                lastSeen = "Active 5m ago",
                lastMessage = "🎤 Voice note (0:18)",
                lastMessageTime = "10:35 AM",
                lastMessageStatus = MessageStatus.DELIVERED,
                unreadCount = 2,
                isPinned = true,
                friendUserId = "QK-USR-5512"
            ),
            ConversationItem(
                id = "grp-1",
                displayId = "QK-GRP-901",
                name = "Quick It Core Engineers",
                avatarInitials = "QC",
                isGroup = true,
                isOnline = true,
                lastSeen = "4 members",
                lastMessage = "Alex: Shared prompt QK-PRM-3001",
                lastMessageTime = "10:25 AM",
                lastMessageStatus = MessageStatus.READ,
                unreadCount = 0,
                isPinned = true,
                memberCount = 4
            ),
            ConversationItem(
                id = "conv-sophia",
                displayId = "QK-CHT-6610",
                name = "Sophia Chen",
                avatarInitials = "SC",
                isGroup = false,
                isOnline = false,
                lastSeen = "Today at 09:48 AM",
                lastMessage = "📷 Captured photo with flash from the lab",
                lastMessageTime = "09:48 AM",
                lastMessageStatus = MessageStatus.READ,
                unreadCount = 0,
                isPinned = false,
                friendUserId = "QK-USR-6610"
            ),
            ConversationItem(
                id = "grp-2",
                displayId = "QK-GRP-902",
                name = "AI Product Innovators",
                avatarInitials = "AP",
                isGroup = true,
                isOnline = false,
                lastSeen = "148 members",
                lastMessage = "Devin: RLS encryption key verified",
                lastMessageTime = "Yesterday",
                lastMessageStatus = MessageStatus.READ,
                unreadCount = 0,
                isPinned = false,
                memberCount = 148
            ),
            ConversationItem(
                id = "conv-devin",
                displayId = "QK-CHT-4420",
                name = "Devin Zhang",
                avatarInitials = "DZ",
                isGroup = false,
                isOnline = false,
                lastSeen = "Yesterday",
                lastMessage = "Voice Call • 4m 12s",
                lastMessageTime = "Yesterday",
                lastMessageStatus = MessageStatus.READ,
                unreadCount = 0,
                isPinned = false,
                friendUserId = "QK-USR-4420"
            ),
            ConversationItem(
                id = "conv-archived-1",
                displayId = "QK-CHT-1100",
                name = "Legacy Cloud Vault Migration",
                avatarInitials = "LC",
                isGroup = false,
                isOnline = false,
                lastSeen = "Oct 8",
                lastMessage = "All assets successfully backed up",
                lastMessageTime = "Oct 8",
                lastMessageStatus = MessageStatus.READ,
                unreadCount = 0,
                isArchived = true
            )
        )

        _hubMessages.value = listOf(
            // Elena 1:1 Conversation Messages
            HubMessage(
                hubId = "conv-elena",
                senderName = "Elena Rostova",
                senderUserId = "QK-USR-3301",
                text = "Hey Alex! Just tested the live camera media capture and voice notes in Quick It.",
                timestamp = "10:30 AM",
                status = MessageStatus.READ
            ),
            HubMessage(
                hubId = "conv-elena",
                senderName = "Elena Rostova",
                senderUserId = "QK-USR-3301",
                text = "Recorded this voice note from our lab session:",
                timestamp = "10:32 AM",
                status = MessageStatus.READ,
                type = MessageType.VOICE_NOTE,
                audioDurationSeconds = 18,
                reactions = listOf("❤️")
            ),
            HubMessage(
                hubId = "conv-elena",
                senderName = "Alex Rivera",
                senderUserId = "QK-USR-8821",
                isCurrentUser = true,
                text = "Awesome Elena! The audio waveform and playback speed controls work perfectly.",
                timestamp = "10:36 AM",
                status = MessageStatus.READ,
                reactions = listOf("👍", "🔥")
            ),
            HubMessage(
                hubId = "conv-elena",
                senderName = "Elena Rostova",
                senderUserId = "QK-USR-3301",
                text = "Here's the captured photo of our 3D Clay component layout:",
                timestamp = "10:40 AM",
                status = MessageStatus.READ,
                type = MessageType.IMAGE,
                mediaCaption = "Viewfinder live capture with Liquid Pearl filter",
                reactions = listOf("🔥")
            ),
            HubMessage(
                hubId = "conv-elena",
                senderName = "Alex Rivera",
                senderUserId = "QK-USR-8821",
                isCurrentUser = true,
                text = "Here's the new 3D Clay theme shader for Compose",
                timestamp = "10:42 AM",
                status = MessageStatus.READ,
                sharedItemTitle = "Quick_It_3D_Clay_Design_Spec.pdf",
                sharedItemId = "QK-FIL-8101",
                sharedItemType = "FILE"
            ),

            // Marcus Vance Conversation Messages
            HubMessage(
                hubId = "conv-marcus",
                senderName = "Marcus Vance",
                senderUserId = "QK-USR-5512",
                text = "Incoming voice call completed (3 mins 45 secs).",
                timestamp = "10:20 AM",
                status = MessageStatus.READ,
                type = MessageType.VOICE_CALL_LOG
            ),
            HubMessage(
                hubId = "conv-marcus",
                senderName = "Marcus Vance",
                senderUserId = "QK-USR-5512",
                text = "Check this voice message about the new RLS policy update:",
                timestamp = "10:35 AM",
                status = MessageStatus.DELIVERED,
                type = MessageType.VOICE_NOTE,
                audioDurationSeconds = 24
            ),

            // Group 1 Messages
            HubMessage(
                hubId = "grp-1",
                senderName = "Devin Zhang",
                senderUserId = "QK-USR-4420",
                text = "Hey squad! Just pushed the latest Postgres Row Level Security (RLS) rules to the vault.",
                timestamp = "10:14 AM",
                status = MessageStatus.READ,
                sharedItemTitle = "Supabase_Postgres_Schema_v2.sql",
                sharedItemId = "QK-FIL-8106",
                sharedItemType = "CODE"
            ),
            HubMessage(
                hubId = "grp-1",
                senderName = "Alex Rivera",
                senderUserId = "QK-USR-8821",
                isCurrentUser = true,
                text = "Awesome Devin! I shared the 3D Clay UI design spec here for everyone to review.",
                timestamp = "10:20 AM",
                status = MessageStatus.READ,
                sharedItemTitle = "Quick_It_3D_Clay_Design_Spec.pdf",
                sharedItemId = "QK-FIL-8101",
                sharedItemType = "FILE"
            ),
            HubMessage(
                hubId = "grp-1",
                senderName = "Elena Rostova",
                senderUserId = "QK-USR-3301",
                text = "Looks incredible! RLS isolates each personal vault so only this hub has decrypt rights.",
                timestamp = "10:25 AM",
                status = MessageStatus.READ
            )
        )

        _friendUsers.value = listOf(
            FriendUser(
                displayId = "QK-USR-3301",
                name = "Elena Rostova",
                role = GroupRole.ADMIN,
                rlsPolicy = "RLS Policy: Vault Isolated (Read-Only Shared)",
                isInvited = true
            ),
            FriendUser(
                displayId = "QK-USR-4420",
                name = "Devin Zhang",
                role = GroupRole.EDITOR,
                rlsPolicy = "RLS Policy: Full Group Decrypt Authorized",
                isInvited = true
            ),
            FriendUser(
                displayId = "QK-USR-5512",
                name = "Marcus Vance",
                role = GroupRole.VIEWER,
                rlsPolicy = "RLS Policy: Strict Private Vault Only",
                isInvited = false
            ),
            FriendUser(
                displayId = "QK-USR-7790",
                name = "Chloe Lin",
                role = GroupRole.EDITOR,
                rlsPolicy = "RLS Policy: Zero-Trust Peer Encrypted",
                isInvited = false
            )
        )
    }

    fun sendHubMessage(
        hubId: String,
        text: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        mediaCaption: String? = null,
        audioDurationSeconds: Int = 0,
        replyToMessageId: String? = null,
        replyToText: String? = null,
        replyToSender: String? = null,
        sharedItemTitle: String? = null,
        sharedItemId: String? = null,
        sharedItemType: String? = null
    ) {
        val msg = HubMessage(
            hubId = hubId,
            senderName = _currentUser.value.name,
            senderUserId = _currentUser.value.displayId,
            isCurrentUser = true,
            text = text,
            status = MessageStatus.READ,
            type = type,
            mediaUrl = mediaUrl,
            mediaCaption = mediaCaption,
            audioDurationSeconds = audioDurationSeconds,
            replyToMessageId = replyToMessageId,
            replyToText = replyToText,
            replyToSender = replyToSender,
            sharedItemTitle = sharedItemTitle,
            sharedItemId = sharedItemId,
            sharedItemType = sharedItemType
        )
        _hubMessages.value = _hubMessages.value + msg

        // Update conversation last message snippet and timestamp
        val preview = when (type) {
            MessageType.VOICE_NOTE -> "🎤 Voice note ($audioDurationSeconds s)"
            MessageType.IMAGE -> "📷 Photo ${mediaCaption?.let { ": $it" } ?: ""}"
            MessageType.VIDEO -> "🎥 Video ${mediaCaption?.let { ": $it" } ?: ""}"
            MessageType.VOICE_CALL_LOG -> "📞 $text"
            else -> text
        }
        _conversations.value = _conversations.value.map { conv ->
            if (conv.id == hubId) {
                conv.copy(
                    lastMessage = preview,
                    lastMessageTime = "Just now",
                    lastMessageStatus = MessageStatus.READ
                )
            } else conv
        }
    }

    fun addMessageReaction(messageId: String, emoji: String) {
        _hubMessages.value = _hubMessages.value.map { msg ->
            if (msg.id == messageId) {
                val updated = if (msg.reactions.contains(emoji)) {
                    msg.reactions.filterNot { it == emoji }
                } else {
                    msg.reactions + emoji
                }
                msg.copy(reactions = updated)
            } else msg
        }
    }

    fun toggleStarMessage(messageId: String) {
        _hubMessages.value = _hubMessages.value.map { msg ->
            if (msg.id == messageId) msg.copy(isStarred = !msg.isStarred) else msg
        }
    }

    fun togglePinMessage(messageId: String) {
        _hubMessages.value = _hubMessages.value.map { msg ->
            if (msg.id == messageId) msg.copy(isPinned = !msg.isPinned) else msg
        }
    }

    fun deleteMessage(messageId: String) {
        _hubMessages.value = _hubMessages.value.filterNot { it.id == messageId }
    }

    fun togglePinConversation(id: String) {
        _conversations.value = _conversations.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
    }

    fun toggleArchiveConversation(id: String) {
        _conversations.value = _conversations.value.map {
            if (it.id == id) it.copy(isArchived = !it.isArchived) else it
        }
    }

    fun toggleMuteConversation(id: String) {
        _conversations.value = _conversations.value.map {
            if (it.id == id) it.copy(isMuted = !it.isMuted) else it
        }
    }

    fun markConversationRead(id: String) {
        _conversations.value = _conversations.value.map {
            if (it.id == id) it.copy(unreadCount = 0) else it
        }
    }

    fun deleteConversation(id: String) {
        _conversations.value = _conversations.value.filterNot { it.id == id }
        _hubMessages.value = _hubMessages.value.filterNot { it.hubId == id }
    }

    fun startVoiceCall(contactName: String, contactId: String, isGroup: Boolean = false) {
        _activeVoiceCall.value = VoiceCallState(
            contactName = contactName,
            contactId = contactId,
            isGroup = isGroup,
            isIncoming = false,
            status = CallStatus.CONNECTED,
            durationSeconds = 1
        )
    }

    fun endVoiceCall() {
        val current = _activeVoiceCall.value
        if (current != null) {
            val dur = current.durationSeconds
            val durationText = "${dur / 60}m ${dur % 60}s"
            sendHubMessage(
                hubId = current.contactId,
                text = "Voice Call ended • $durationText",
                type = MessageType.VOICE_CALL_LOG
            )
        }
        _activeVoiceCall.value = null
    }

    fun toggleCallMute() {
        _activeVoiceCall.value = _activeVoiceCall.value?.let {
            it.copy(isMuted = !it.isMuted)
        }
    }

    fun toggleCallSpeaker() {
        _activeVoiceCall.value = _activeVoiceCall.value?.let {
            it.copy(isSpeakerOn = !it.isSpeakerOn)
        }
    }

    fun createConversation(name: String, isGroup: Boolean, friendUserId: String? = null): ConversationItem {
        val newConv = ConversationItem(
            name = name,
            avatarInitials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
            isGroup = isGroup,
            isOnline = true,
            lastMessage = "Started encrypted chat",
            lastMessageTime = "Just now",
            memberCount = if (isGroup) 3 else 2,
            friendUserId = friendUserId
        )
        _conversations.value = listOf(newConv) + _conversations.value
        return newConv
    }

    fun inviteFriendToHub(friendId: String) {
        _friendUsers.value = _friendUsers.value.map {
            if (it.id == friendId) it.copy(isInvited = true) else it
        }
    }

    // Dynamic Actions
    fun addStorageItem(
        name: String,
        category: FileCategory,
        sizeBytes: Long,
        folderId: String?,
        folderPath: String,
        description: String,
        tags: List<String>,
        url: String? = null,
        contentSnippet: String = ""
    ): StorageItem {
        val formattedSize = when {
            sizeBytes >= 1_000_000 -> "%.1f MB".format(sizeBytes / 1_000_000f)
            sizeBytes >= 1_000 -> "${sizeBytes / 1000} KB"
            else -> "$sizeBytes B"
        }
        val newItem = StorageItem(
            name = name,
            category = category,
            sizeBytes = sizeBytes,
            formattedSize = formattedSize,
            folderId = folderId,
            folderPath = folderPath,
            description = description,
            tags = tags,
            url = url,
            contentSnippet = contentSnippet.ifBlank { url ?: "" },
            createdAt = "Just now"
        )
        _storageItems.value = listOf(newItem) + _storageItems.value

        // Update folder count if applicable
        if (folderId != null) {
            _folders.value = _folders.value.map { f ->
                if (f.id == folderId) f.copy(itemCount = f.itemCount + 1) else f
            }
        }
        return newItem
    }

    fun createFolder(name: String, category: FileCategory = FileCategory.OTHER, path: String = "/My Vault"): FolderItem {
        val newFolder = FolderItem(
            name = name,
            category = category,
            path = "$path/$name",
            colorHex = 0xFF7955D9
        )
        _folders.value = _folders.value + newFolder
        return newFolder
    }

    fun toggleFileFavorite(id: String) {
        _storageItems.value = _storageItems.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun toggleFolderFavorite(id: String) {
        _folders.value = _folders.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun deleteFile(id: String) {
        _storageItems.value = _storageItems.value.filterNot { it.id == id }
    }

    fun addNote(title: String, content: String, tags: List<String>, folder: String): NoteItem {
        val newNote = NoteItem(
            title = title,
            content = content,
            tags = tags,
            folderName = folder
        )
        _notes.value = listOf(newNote) + _notes.value
        return newNote
    }

    fun addPrompt(title: String, promptText: String, category: String): PromptItem {
        val newPrompt = PromptItem(
            title = title,
            promptText = promptText,
            category = category
        )
        _prompts.value = listOf(newPrompt) + _prompts.value
        return newPrompt
    }

    fun toggleTask(id: String) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun addTask(title: String, dueTime: String, priority: TaskPriority, category: String): TaskItem {
        val newTask = TaskItem(
            title = title,
            dueTime = dueTime,
            priority = priority,
            category = category
        )
        _tasks.value = listOf(newTask) + _tasks.value
        return newTask
    }

    fun createGroup(name: String, description: String, isPrivate: Boolean): GroupHub {
        val newGroup = GroupHub(
            name = name,
            description = description,
            isPrivate = isPrivate,
            userRole = GroupRole.OWNER
        )
        _groups.value = listOf(newGroup) + _groups.value
        return newGroup
    }

    // AI Assistant Natural Language Dispatcher
    fun sendUserMessage(text: String) {
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        val query = text.lowercase()
        val response = when {
            query.contains("search") || query.contains("find") || query.contains("file") || query.contains("doc") -> {
                val matches = _storageItems.value.filter {
                    it.name.lowercase().contains(query.replace("search", "").replace("find", "").trim()) ||
                            it.tags.any { tag -> query.contains(tag.lowercase()) } ||
                            query.contains(it.category.name.lowercase())
                }
                if (matches.isNotEmpty()) {
                    val first = matches.first()
                    ChatMessage(
                        text = "I found ${matches.size} relevant resource(s) in your vault:\n\n• ${first.name} (ID: ${first.displayId})\nPath: ${first.folderPath}\nCategory: ${first.category.displayName}\n\nDescription: ${first.description}",
                        isUser = false,
                        suggestedAction = "View ${first.name}",
                        referencedResource = first.name,
                        referencedResourceId = first.displayId
                    )
                } else {
                    ChatMessage(
                        text = "I searched your encrypted storage for '$text'. Here are your most recent active files:\n• Quick_It_3D_Clay_Design_Spec.pdf (ID: QK-FIL-8101)\n• Gemini_Multimodal_Agent_System.kt (ID: QK-FIL-8102)\n• Orb_Mascot_3D_Render.png (ID: QK-FIL-8103)",
                        isUser = false,
                        suggestedAction = "Explore My Storage"
                    )
                }
            }

            query.contains("summarize") || query.contains("summary") -> {
                ChatMessage(
                    text = "Here is an executive summary of your documents:\n\n1. Quick It Vault utilizes client-side unique identifiers (UUID + readable QK-* display IDs).\n2. 3D Claymorphism guidelines require 24–36px border radius, pearl highlights, and lavender/violet gradients.\n3. Zero-trust isolation guarantees private items are never surfaced in public group searches.",
                    isUser = false,
                    suggestedAction = "Save to Notes"
                )
            }

            query.contains("prompt") -> {
                ChatMessage(
                    text = "You currently have 3 AI prompts in your library! Popular one: 'Smart File Semantic Search' (ID: QK-PRM-3001) tuned for Gemini 2.5 Flash. Would you like me to draft a new customized prompt?",
                    isUser = false,
                    suggestedAction = "Create AI Prompt"
                )
            }

            query.contains("explain") || query.contains("what is") || query.contains("how") -> {
                ChatMessage(
                    text = "Quick It combines a 3D Claymorphism + Liquid Glass design with real-time neural assistance. Your personal vault keeps photos, code, PDFs, and notes structured under permanent unique IDs while supporting seamless Google OAuth and multi-device sync.",
                    isUser = false,
                    suggestedAction = "Explore Learning Hub"
                )
            }

            else -> {
                ChatMessage(
                    text = "I'm Hubby AI! I've analyzed your request with our neural engine. I can organize your vault files, draft notes, summarize documents, or share assets into your hubs. What would you like to do?",
                    isUser = false,
                    suggestedAction = "Smart Save new file"
                )
            }
        }

        _chatMessages.value = _chatMessages.value + response
    }

    companion object {
        val instance = HubRepository()
    }
}
