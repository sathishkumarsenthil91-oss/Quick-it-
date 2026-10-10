package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.FolderItem
import com.example.ui.AppScreen
import com.example.ui.HubViewModel
import com.example.ui.components.ClayBackground
import com.example.ui.components.FileDetailSheet
import com.example.ui.components.QuickItBottomNav
import com.example.ui.components.SmartSaveBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.QuickItTheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            QuickItTheme {
                val viewModel: HubViewModel = viewModel()
                QuickItApp(viewModel)
            }
        }
    }
}

@Composable
fun QuickItApp(viewModel: HubViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val storageItems by viewModel.storageItems.collectAsStateWithLifecycle()
    val filteredStorageItems by viewModel.filteredStorageItems.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val prompts by viewModel.prompts.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val codingProjects by viewModel.codingProjects.collectAsStateWithLifecycle()
    val learningSubjects by viewModel.learningSubjects.collectAsStateWithLifecycle()
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val hubMessages by viewModel.hubMessages.collectAsStateWithLifecycle()
    val friendUsers by viewModel.friendUsers.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeVoiceCall by viewModel.activeVoiceCall.collectAsStateWithLifecycle()

    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isGridView by viewModel.isGridView.collectAsStateWithLifecycle()
    val showSmartSaveSheet by viewModel.showSmartSaveSheet.collectAsStateWithLifecycle()
    val selectedFileDetail by viewModel.selectedFileDetail.collectAsStateWithLifecycle()
    val snackBarMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle back button presses for sub-screens
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.SPLASH) {
        viewModel.navigateBack()
    }

    // Show toast / snackbar feedback
    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearToast()
        }
    }

    ClayBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Screen Content (full-screen so grids and lists extend behind floating bottom nav)
            AnimatedContent(
                targetState = currentScreen,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    when {
                        targetState == AppScreen.GROUPS || targetState == AppScreen.AI_ASSISTANT ->
                            (slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(280)) +
                                fadeIn(animationSpec = tween(240)))
                                .togetherWith(
                                    slideOutHorizontally(targetOffsetX = { -it / 8 }, animationSpec = tween(220)) +
                                        fadeOut(animationSpec = tween(180))
                                )
                        initialState == AppScreen.GROUPS || initialState == AppScreen.AI_ASSISTANT ->
                            (slideInHorizontally(initialOffsetX = { -it / 5 }, animationSpec = tween(280)) +
                                fadeIn(animationSpec = tween(240)))
                                .togetherWith(
                                    slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(220)) +
                                        fadeOut(animationSpec = tween(180))
                                )
                        else -> fadeIn(animationSpec = tween(160))
                            .togetherWith(fadeOut(animationSpec = tween(120)))
                    }.apply { targetContentZIndex = 1f }
                },
                label = "Quick It screen transition"
            ) { screen ->
                when (screen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onFinish = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }

                    AppScreen.AUTH -> {
                        // Login page removed per user request: go straight to dashboard
                        LaunchedEffect(Unit) {
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        }
                        DashboardScreen(
                            user = currentUser,
                            storageItems = storageItems,
                            folders = folders,
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenFileDetail = { viewModel.selectFileDetail(it) },
                            onOpenFolderDetail = { folder ->
                                viewModel.setSelectedCategory(folder.category)
                                viewModel.navigateTo(AppScreen.STORAGE)
                            },
                            onToggleFavorite = { viewModel.toggleFileFavorite(it) },
                            onCopyId = { id -> viewModel.showToast("Copied ID: $id") }
                        )
                    }

                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        user = currentUser,
                        storageItems = storageItems,
                        folders = folders,
                        onNavigate = { viewModel.navigateTo(it) },
                        onOpenFileDetail = { viewModel.selectFileDetail(it) },
                        onOpenFolderDetail = { folder ->
                            viewModel.setSelectedCategory(folder.category)
                            viewModel.navigateTo(AppScreen.STORAGE)
                        },
                        onToggleFavorite = { viewModel.toggleFileFavorite(it) },
                        onCopyId = { text ->
                            viewModel.showToast(if (text.startsWith("http") || text.startsWith("Copied") || text.startsWith("Link")) "Copied to clipboard!" else "Copied ID: $text")
                        }
                    )
                }

                AppScreen.STORAGE -> {
                    StorageScreen(
                        storageItems = filteredStorageItems,
                        folders = folders,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        isGridView = isGridView,
                        onCategorySelected = { viewModel.setSelectedCategory(it) },
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onToggleGridView = { viewModel.toggleGridView() },
                        onOpenFileDetail = { viewModel.selectFileDetail(it) },
                        onOpenFolderDetail = { folder ->
                            viewModel.setSelectedCategory(folder.category)
                        },
                        onToggleFileFavorite = { viewModel.toggleFileFavorite(it) },
                        onCreateFolder = { name, cat -> viewModel.createFolder(name, cat) },
                        onCopyId = { text ->
                            viewModel.showToast(if (text.startsWith("http") || text.startsWith("Copied") || text.startsWith("Link")) "Copied to clipboard!" else "Copied ID: $text")
                        }
                    )
                }

                AppScreen.AI_ASSISTANT -> {
                    AiAssistantScreen(
                        messages = chatMessages,
                        onSendMessage = { viewModel.sendChatMessage(it) },
                        onBack = { viewModel.navigateBack() },
                        onCopyId = { id -> viewModel.showToast("Copied ID: $id") },
                        onOpenResource = { resId ->
                            val found = storageItems.find { it.displayId == resId }
                            if (found != null) {
                                viewModel.selectFileDetail(found)
                            } else {
                                viewModel.showToast("Opening resource $resId")
                            }
                        }
                    )
                }

                AppScreen.GROUPS -> {
                    ChatSystemScreen(
                        conversations = conversations,
                        messages = hubMessages,
                        friendUsers = friendUsers,
                        storageItems = storageItems,
                        notes = notes,
                        prompts = prompts,
                        tasks = tasks,
                        codingProjects = codingProjects,
                        activeVoiceCall = activeVoiceCall,
                        onSendMessage = { hubId, text, type, mediaUrl, caption, audioDur, replyId, replyTxt, replySender, itemTitle, itemId, itemType ->
                            viewModel.sendHubMessage(
                                hubId = hubId,
                                text = text,
                                type = type,
                                mediaUrl = mediaUrl,
                                mediaCaption = caption,
                                audioDurationSeconds = audioDur,
                                replyToMessageId = replyId,
                                replyToText = replyTxt,
                                replyToSender = replySender,
                                sharedItemTitle = itemTitle,
                                sharedItemId = itemId,
                                sharedItemType = itemType
                            )
                        },
                        onAddReaction = { msgId, emoji -> viewModel.addMessageReaction(msgId, emoji) },
                        onToggleStarMessage = { msgId -> viewModel.toggleStarMessage(msgId) },
                        onTogglePinMessage = { msgId -> viewModel.togglePinMessage(msgId) },
                        onDeleteMessage = { msgId -> viewModel.deleteMessage(msgId) },
                        onTogglePinConversation = { convId -> viewModel.togglePinConversation(convId) },
                        onToggleArchiveConversation = { convId -> viewModel.toggleArchiveConversation(convId) },
                        onToggleMuteConversation = { convId -> viewModel.toggleMuteConversation(convId) },
                        onDeleteConversation = { convId -> viewModel.deleteConversation(convId) },
                        onStartVoiceCall = { name, id, isGroup -> viewModel.startVoiceCall(name, id, isGroup) },
                        onEndVoiceCall = { viewModel.endVoiceCall() },
                        onToggleCallMute = { viewModel.toggleCallMute() },
                        onToggleCallSpeaker = { viewModel.toggleCallSpeaker() },
                        onCreateConversation = { name, isGroup, friendId -> viewModel.createConversation(name, isGroup, friendId) },
                        onCopyId = { id -> viewModel.showToast("Copied ID: $id") },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                AppScreen.PROMPTS -> {
                    PromptsScreen(
                        prompts = prompts,
                        onBack = { viewModel.navigateBack() },
                        onAddPrompt = { title, body, cat -> viewModel.addPrompt(title, body, cat) },
                        onCopyId = { msg -> viewModel.showToast(msg) }
                    )
                }

                AppScreen.NOTES -> {
                    NotesScreen(
                        notes = notes,
                        onBack = { viewModel.navigateBack() },
                        onAddNote = { title, content, tags, folder ->
                            viewModel.addNote(title, content, tags, folder)
                        },
                        onCopyId = { id -> viewModel.showToast("Copied ID: $id") }
                    )
                }

                AppScreen.PLANNER -> {
                    PlannerScreen(
                        tasks = tasks,
                        onBack = { viewModel.navigateBack() },
                        onToggleTask = { viewModel.toggleTask(it) },
                        onAddTask = { title, due, priority, cat -> viewModel.addTask(title, due, priority, cat) },
                        onCopyId = { id -> viewModel.showToast("Copied ID: $id") }
                    )
                }

                AppScreen.CODING_PROJECTS -> {
                    CodingProjectsScreen(
                        projects = codingProjects,
                        onBack = { viewModel.navigateBack() },
                        onCopyId = { id -> viewModel.showToast("Copied ID: $id") }
                    )
                }

                AppScreen.LEARNING_HUB -> {
                    LearningHubScreen(
                        subjects = learningSubjects,
                        onBack = { viewModel.navigateBack() },
                        onCopyId = { id -> viewModel.showToast("Copied ID: $id") }
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        user = currentUser,
                        onBack = { viewModel.navigateBack() },
                        onLogout = {
                            viewModel.showToast("Cloud vault synchronized successfully!")
                            viewModel.navigateTo(AppScreen.DASHBOARD)
                        },
                        onCopyId = { id -> viewModel.showToast("Copied ID: $id") }
                    )
                }
            }
        }

        // Keep navigation on primary tabs, but never overlay immersive chats, AI,
        // modal sheets, file previews, or the on-screen keyboard.
        val showBottomNav = (currentScreen == AppScreen.DASHBOARD ||
            currentScreen == AppScreen.STORAGE) &&
            !showSmartSaveSheet &&
            selectedFileDetail == null &&
            !WindowInsets.isImeVisible

        AnimatedVisibility(
            visible = showBottomNav,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(280)) +
                fadeIn(animationSpec = tween(220)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(180)) +
                fadeOut(animationSpec = tween(140))
        ) {
            QuickItBottomNav(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) },
                onOpenSmartSave = { viewModel.openSmartSave() }
            )
        }

        // Snackbar feedback overlay
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp)
        )
    }

    // Universal Smart Save Bottom Sheet (+)
    if (showSmartSaveSheet) {
        SmartSaveBottomSheet(
            folders = folders,
            onDismiss = { viewModel.closeSmartSave() },
            onSave = { name, category, sizeBytes, folderId, folderPath, description, tags, url, contentSnippet ->
                viewModel.saveSmartItem(
                    name = name,
                    category = category,
                    sizeBytes = sizeBytes,
                    folderId = folderId,
                    folderPath = folderPath,
                    description = description,
                    tags = tags,
                    url = url,
                    contentSnippet = contentSnippet
                )
            },
            onCreateFolder = { name, cat -> viewModel.createFolder(name, cat) }
        )
    }

    // File Detail Preview Sheet
    selectedFileDetail?.let { fileItem ->
        FileDetailSheet(
            item = fileItem,
            onDismiss = { viewModel.selectFileDetail(null) },
            onDelete = { viewModel.deleteFile(it) },
            onToggleFavorite = { viewModel.toggleFileFavorite(it) },
            onCopyId = { id -> viewModel.showToast("Copied ID: $id") },
            onShare = { item ->
                viewModel.showToast("Sharing '${item.name}' (ID: ${item.displayId})")
            }
        )
    }
    }
}
