package org.emmadice.app

import org.emmadice.app.data.LegacyCardMigration
import org.emmadice.app.data.CardResources
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class LegacyCardMigrationTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun seedsKeepStableKeysAndLegacyTitles() {
        assertEquals(listOf("papa", "mama", "agua"), LegacyCardMigration.seeds.map { it.stableKey })
        assertEquals(listOf("Papá", "Mamá", "Agua"), LegacyCardMigration.seeds.map { it.title })
    }

    @Test
    fun migrationOnlyReferencesUsableLegacyFiles() {
        val filesDir = temporaryFolder.newFolder("files")
        val images = filesDir.resolve("images").also { it.mkdirs() }
        val audio = filesDir.resolve("audio").also { it.mkdirs() }
        images.resolve("papa_photo.jpg").writeBytes(byteArrayOf(1))
        audio.resolve("papa_audio.m4a").writeBytes(byteArrayOf(1))

        val papa = LegacyCardMigration.seedCard(filesDir, LegacyCardMigration.seeds.first())
        val mama = LegacyCardMigration.seedCard(filesDir, LegacyCardMigration.seeds[1])

        assertEquals("images/papa_photo.jpg", papa.imagePath)
        assertEquals("audio/papa_audio.m4a", papa.audioPath)
        assertNull(mama.imagePath)
        assertNull(mama.audioPath)
        assertTrue(LegacyCardMigration.isUsable(images.resolve("papa_photo.jpg")))
    }

    @Test
    fun missingEmptyDirectoryAndInvalidFilesAreNotUsable() {
        val filesDir = temporaryFolder.newFolder("files")
        val images = filesDir.resolve("images").also { it.mkdirs() }
        val directory = images.resolve("mama_photo.jpg").also { it.mkdirs() }
        val empty = images.resolve("agua_photo.jpg").also { it.createNewFile() }
        val missing = images.resolve("papa_photo.jpg")

        assertTrue(!LegacyCardMigration.isUsable(directory))
        assertTrue(!LegacyCardMigration.isUsable(empty))
        assertTrue(!LegacyCardMigration.isUsable(missing))
    }

    @Test
    fun seedDefinitionsAreStableAndIdempotent() {
        assertEquals(LegacyCardMigration.seeds, LegacyCardMigration.seeds.toList())
        assertEquals(setOf("papa", "mama", "agua"), LegacyCardMigration.seeds.map { it.stableKey }.toSet())
    }

    @Test
    fun resourceKeysRejectTraversalAndAcceptStableKeys() {
        assertTrue(CardResources.isSafeKey("papa"))
        assertTrue(CardResources.isSafeKey("new-card_1"))
        assertTrue(!CardResources.isSafeKey("../outside"))
        assertTrue(!CardResources.isSafeKey("images/papa"))
        assertTrue(!CardResources.isSafeKey(""))
    }
}
