package org.emmadice.app.data

import java.io.File
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.emmadice.app.data.db.CardDao
import org.emmadice.app.data.db.CardEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CardRepositoryDeleteTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun deleteSuccessRemovesResourcesBeforeRoomEntity() = runBlocking {
        val card = card()
        val dao = FakeCardDao(card)
        val resources = FakeResourceManager(ResourceDeletionResult(null, null, null))

        CardRepository(dao, resources).delete(card)

        assertEquals(1, dao.deleteCalls)
        assertNull(dao.updated)
    }

    @Test
    fun photoFailureKeepsRoomEntityAndUnsyncedPhotoReference() = runBlocking {
        val card = card(imagePath = "images/card_photo.jpg")
        val dao = FakeCardDao(card)
        val error = IOException("photo failure")
        val resources = FakeResourceManager(
            ResourceDeletionResult(card.imagePath, null, error)
        )

        val thrown = runCatching { CardRepository(dao, resources).delete(card) }.exceptionOrNull()

        assertEquals(error, thrown)
        assertEquals(0, dao.deleteCalls)
        assertEquals(card.imagePath, dao.updated?.imagePath)
        assertNull(dao.updated?.audioPath)
    }

    @Test
    fun audioFailureAfterPhotoKeepsOnlyAudioReference() = runBlocking {
        val card = card(
            imagePath = "images/card_photo.jpg",
            audioPath = "audio/card_audio.m4a"
        )
        val dao = FakeCardDao(card)
        val error = IOException("audio failure")
        val resources = FakeResourceManager(
            ResourceDeletionResult(null, card.audioPath, error)
        )

        val thrown = runCatching { CardRepository(dao, resources).delete(card) }.exceptionOrNull()

        assertEquals(error, thrown)
        assertEquals(0, dao.deleteCalls)
        assertNull(dao.updated?.imagePath)
        assertEquals(card.audioPath, dao.updated?.audioPath)
    }

    @Test
    fun roomDeleteFailureKeepsEntityWithClearedReferences() = runBlocking {
        val card = card(
            imagePath = "images/card_photo.jpg",
            audioPath = "audio/card_audio.m4a"
        )
        val dao = FakeCardDao(card, deleteResult = 0)
        val resources = FakeResourceManager(ResourceDeletionResult(null, null, null))

        val thrown = runCatching { CardRepository(dao, resources).delete(card) }.exceptionOrNull()

        assertTrue(thrown is IllegalStateException)
        assertEquals(1, dao.deleteCalls)
        assertNull(dao.updated?.imagePath)
        assertNull(dao.updated?.audioPath)
    }

    @Test
    fun actualResourceDeletionDoesNotTouchAnotherCard() = runBlocking {
        val filesDir = temporaryFolder.newFolder("files")
        val resources = CardResources(filesDir)
        val firstFile = resources.imageFile("first")
        val secondFile = resources.imageFile("second")
        firstFile.parentFile?.mkdirs()
        firstFile.writeBytes(byteArrayOf(1))
        secondFile.writeBytes(byteArrayOf(2))

        val result = resources.deleteResources(card("first", "images/first_photo.jpg"))

        assertNull(result.failure)
        assertFalse(firstFile.exists())
        assertTrue(secondFile.exists())
    }

    @Test
    fun missingResourceIsReportedAsClearedWithoutCreatingFalseReference() = runBlocking {
        val card = card(imagePath = "images/missing_photo.jpg")
        val dao = FakeCardDao(card)
        val resources = FakeResourceManager(ResourceDeletionResult(null, null, null))

        CardRepository(dao, resources).delete(card)

        assertEquals(1, dao.deleteCalls)
        assertNull(dao.updated)
    }

    private fun card(
        key: String = "card",
        imagePath: String? = null,
        audioPath: String? = null
    ) = CardEntity(
        id = 1L,
        stableKey = key,
        title = key,
        position = 0,
        enabled = true,
        imagePath = imagePath,
        audioPath = audioPath
    )
}

private class FakeCardDao(
    private var row: CardEntity,
    private val deleteResult: Int = 1
) : CardDao {
    var updated: CardEntity? = null
    var deleteCalls = 0

    override fun observeAll(): Flow<List<CardEntity>> = flowOf(listOf(row))

    override suspend fun getAll(): List<CardEntity> = listOf(row)

    override suspend fun insert(card: CardEntity): Long = card.id

    override suspend fun update(card: CardEntity): Int {
        updated = card
        row = card
        return 1
    }

    override suspend fun delete(card: CardEntity): Int {
        deleteCalls++
        return deleteResult
    }
}

private class FakeResourceManager(
    private val result: ResourceDeletionResult
) : CardResourceManager {
    override fun deleteResources(card: CardEntity): ResourceDeletionResult = result

    override fun isUsable(relativePath: String?, directory: String): Boolean = true
}
