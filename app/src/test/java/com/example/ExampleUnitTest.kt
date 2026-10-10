package com.example

import com.example.data.HubRepository
import com.example.model.FileCategory
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun repository_initialSeedData_isPopulated() {
        val repo = HubRepository()
        assertTrue(repo.storageItems.value.isNotEmpty())
        assertTrue(repo.folders.value.isNotEmpty())
        assertTrue(repo.chatMessages.value.isNotEmpty())
        assertTrue(repo.prompts.value.isNotEmpty())
        assertTrue(repo.notes.value.isNotEmpty())
        assertTrue(repo.tasks.value.isNotEmpty())
    }

    @Test
    fun repository_uniqueIdGeneration_formatCorrect() {
        val repo = HubRepository()
        val item = repo.addStorageItem(
            name = "Test_Spec.pdf",
            category = FileCategory.PDFS,
            sizeBytes = 25000L,
            folderId = null,
            folderPath = "/My Vault",
            description = "Test Description",
            tags = listOf("Test", "AI")
        )

        assertTrue(item.displayId.startsWith("QK-FIL-"))
        assertTrue(repo.storageItems.value.any { it.id == item.id })
    }

    @Test
    fun repository_chatMessage_searchQuery_matchesFiles() {
        val repo = HubRepository()
        repo.sendUserMessage("search Quick_It")
        val messages = repo.chatMessages.value
        val lastResponse = messages.last()
        assertFalse(lastResponse.isUser)
        assertTrue(lastResponse.text.contains("QK-FIL-8101") || lastResponse.text.contains("found"))
    }

    @Test
    fun repository_hubMessage_and_friends_work() {
        val repo = HubRepository()
        repo.sendHubMessage("grp-1", "Testing Hub chat", "Quick_It_3D_Clay_Design_Spec.pdf", "QK-FIL-8101", "FILE")
        val msgs = repo.hubMessages.value
        assertTrue(msgs.any { it.text == "Testing Hub chat" && it.sharedItemId == "QK-FIL-8101" })

        assertTrue(repo.friendUsers.value.isNotEmpty())
        val firstFriend = repo.friendUsers.value.first()
        repo.inviteFriendToHub(firstFriend.id)
        assertTrue(repo.friendUsers.value.first { it.id == firstFriend.id }.isInvited)
    }
}
