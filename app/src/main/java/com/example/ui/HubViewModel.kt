package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HubRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    AUTH,
    DASHBOARD,
    STORAGE,
    AI_ASSISTANT,
    GROUPS,
    PLANNER,
    NOTES,
    PROMPTS,
    CODING_PROJECTS,
    LEARNING_HUB,
    PROFILE
}

class HubViewModel(
    private val repository: HubRepository = HubRepository.instance
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _screenHistory = mutableListOf<AppScreen>()

    private val _selectedCategory = MutableStateFlow<FileCategory?>(null)
    val selectedCategory: StateFlow<FileCategory?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _showSmartSaveSheet = MutableStateFlow(false)
    val showSmartSaveSheet: StateFlow<Boolean> = _showSmartSaveSheet.asStateFlow()

    private val _selectedFileDetail = MutableStateFlow<StorageItem?>(null)
    val selectedFileDetail: StateFlow<StorageItem?> = _selectedFileDetail.asStateFlow()

    private val _selectedFolderDetail = MutableStateFlow<FolderItem?>(null)
    val selectedFolderDetail: StateFlow<FolderItem?> = _selectedFolderDetail.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    val currentUser = repository.currentUser
    val storageItems = repository.storageItems
    val folders = repository.folders
    val chatMessages = repository.chatMessages
    val notes = repository.notes
    val prompts = repository.prompts
    val tasks = repository.tasks
    val codingProjects = repository.codingProjects
    val learningSubjects = repository.learningSubjects
    val groups = repository.groups
    val hubMessages = repository.hubMessages
    val friendUsers = repository.friendUsers
    val conversations = repository.conversations
    val activeVoiceCall = repository.activeVoiceCall

    private val _selectedHub = MutableStateFlow<GroupHub?>(null)
    val selectedHub: StateFlow<GroupHub?> = _selectedHub.asStateFlow()

    private val _activeConversation = MutableStateFlow<ConversationItem?>(null)
    val activeConversation: StateFlow<ConversationItem?> = _activeConversation.asStateFlow()

    fun selectConversation(conv: ConversationItem?) {
        _activeConversation.value = conv
        if (conv != null) {
            repository.markConversationRead(conv.id)
        }
    }

    fun selectHub(hub: GroupHub?) {
        _selectedHub.value = hub
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
        repository.sendHubMessage(
            hubId = hubId,
            text = text,
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
    }

    fun addMessageReaction(messageId: String, emoji: String) {
        repository.addMessageReaction(messageId, emoji)
    }

    fun toggleStarMessage(messageId: String) {
        repository.toggleStarMessage(messageId)
        showToast("Updated Starred Message")
    }

    fun togglePinMessage(messageId: String) {
        repository.togglePinMessage(messageId)
        showToast("Updated Pinned Message")
    }

    fun deleteMessage(messageId: String) {
        repository.deleteMessage(messageId)
        showToast("Message deleted")
    }

    fun togglePinConversation(id: String) {
        repository.togglePinConversation(id)
    }

    fun toggleArchiveConversation(id: String) {
        repository.toggleArchiveConversation(id)
    }

    fun toggleMuteConversation(id: String) {
        repository.toggleMuteConversation(id)
    }

    fun markConversationRead(id: String) {
        repository.markConversationRead(id)
    }

    fun deleteConversation(id: String) {
        repository.deleteConversation(id)
        if (_activeConversation.value?.id == id) {
            _activeConversation.value = null
        }
        showToast("Chat deleted")
    }

    fun startVoiceCall(contactName: String, contactId: String, isGroup: Boolean = false) {
        repository.startVoiceCall(contactName, contactId, isGroup)
    }

    fun endVoiceCall() {
        repository.endVoiceCall()
        showToast("Voice Call ended")
    }

    fun toggleCallMute() {
        repository.toggleCallMute()
    }

    fun toggleCallSpeaker() {
        repository.toggleCallSpeaker()
    }

    fun createConversation(name: String, isGroup: Boolean, friendUserId: String? = null): ConversationItem {
        val conv = repository.createConversation(name, isGroup, friendUserId)
        _activeConversation.value = conv
        showToast("Created ${if (isGroup) "Group" else "Chat"}: $name")
        return conv
    }

    fun inviteFriendToHub(friendId: String) {
        repository.inviteFriendToHub(friendId)
        showToast("Friend invited to Hub!")
    }

    // Filtered storage items based on category and search query (name, tag, category, or Unique ID)
    val filteredStorageItems = combine(
        storageItems,
        _selectedCategory,
        _searchQuery
    ) { items, cat, query ->
        items.filter { item ->
            val matchCategory = cat == null || item.category == cat
            val matchQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                item.name.lowercase().contains(q) ||
                        item.displayId.lowercase().contains(q) ||
                        item.category.displayName.lowercase().contains(q) ||
                        item.tags.any { it.lowercase().contains(q) } ||
                        item.description.lowercase().contains(q)
            }
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (_screenHistory.isNotEmpty()) {
            val prev = _screenHistory.removeAt(_screenHistory.size - 1)
            _currentScreen.value = prev
            true
        } else {
            if (_currentScreen.value != AppScreen.DASHBOARD) {
                _currentScreen.value = AppScreen.DASHBOARD
                true
            } else {
                false
            }
        }
    }

    fun setSelectedCategory(category: FileCategory?) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }

    fun openSmartSave() {
        _showSmartSaveSheet.value = true
    }

    fun closeSmartSave() {
        _showSmartSaveSheet.value = false
    }

    fun selectFileDetail(item: StorageItem?) {
        _selectedFileDetail.value = item
    }

    fun selectFolderDetail(folder: FolderItem?) {
        _selectedFolderDetail.value = folder
    }

    fun showToast(msg: String) {
        _snackBarMessage.value = msg
    }

    fun clearToast() {
        _snackBarMessage.value = null
    }

    fun sendChatMessage(text: String) {
        if (text.isNotBlank()) {
            repository.sendUserMessage(text)
        }
    }

    fun saveSmartItem(
        name: String,
        category: FileCategory,
        sizeBytes: Long,
        folderId: String?,
        folderPath: String,
        description: String,
        tags: List<String>,
        url: String? = null,
        contentSnippet: String = ""
    ) {
        val created = repository.addStorageItem(
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
        closeSmartSave()
        showToast("Saved '${created.name}' (ID: ${created.displayId})")
    }

    fun createFolder(name: String, category: FileCategory) {
        val folder = repository.createFolder(name, category)
        showToast("Created folder '${folder.name}' (ID: ${folder.displayId})")
    }

    fun toggleFileFavorite(id: String) {
        repository.toggleFileFavorite(id)
    }

    fun toggleFolderFavorite(id: String) {
        repository.toggleFolderFavorite(id)
    }

    fun deleteFile(id: String) {
        repository.deleteFile(id)
        if (_selectedFileDetail.value?.id == id) {
            _selectedFileDetail.value = null
        }
        showToast("Item deleted from vault")
    }

    fun addNote(title: String, content: String, tags: List<String>, folder: String) {
        val note = repository.addNote(title, content, tags, folder)
        showToast("Note '${note.title}' created (ID: ${note.displayId})")
    }

    fun addPrompt(title: String, promptText: String, category: String) {
        val prm = repository.addPrompt(title, promptText, category)
        showToast("Prompt saved (ID: ${prm.displayId})")
    }

    fun toggleTask(id: String) {
        repository.toggleTask(id)
    }

    fun addTask(title: String, dueTime: String, priority: TaskPriority, category: String) {
        val task = repository.addTask(title, dueTime, priority, category)
        showToast("Task scheduled (ID: ${task.displayId})")
    }

    fun createGroup(name: String, description: String, isPrivate: Boolean) {
        val group = repository.createGroup(name, description, isPrivate)
        showToast("Group '${group.name}' created (ID: ${group.displayId})")
    }
}
