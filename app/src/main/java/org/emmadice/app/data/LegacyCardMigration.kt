package org.emmadice.app.data

import org.emmadice.app.data.db.CardEntity
import java.io.File

object LegacyCardMigration {
    data class Seed(
        val stableKey: String,
        val title: String,
        val position: Int
    )

    val seeds = listOf(
        Seed("papa", "Papá", 0),
        Seed("mama", "Mamá", 1),
        Seed("agua", "Agua", 2)
    )

    fun seedCard(filesDir: File, seed: Seed): CardEntity {
        val image = File(filesDir, "images/${seed.stableKey}_photo.jpg")
        val audio = File(filesDir, "audio/${seed.stableKey}_audio.m4a")
        return CardEntity(
            0L,
            seed.stableKey,
            seed.title,
            seed.position,
            true,
            image.takeIf(::isUsable)?.let { "images/${it.name}" },
            audio.takeIf(::isUsable)?.let { "audio/${it.name}" }
        )
    }

    fun isUsable(file: File): Boolean = file.exists() && file.isFile && file.length() > 0L
}
