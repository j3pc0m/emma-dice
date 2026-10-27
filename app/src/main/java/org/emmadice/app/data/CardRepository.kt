package org.emmadice.app.data

import android.content.Context
import androidx.room.withTransaction
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.emmadice.app.data.db.AppDatabase
import org.emmadice.app.data.db.CardDao
import org.emmadice.app.data.db.CardEntity
import java.util.UUID

class CardRepository private constructor(
    private val database: AppDatabase?,
    private val dao: CardDao,
    private val resourceManager: CardResourceManager,
    private val resources: CardResources?,
    private val legacyFilesDir: java.io.File?
) {
    constructor(context: Context) : this(
        AppDatabase.get(context),
        AppDatabase.get(context).cardDao(),
        CardResources(context.applicationContext),
        CardResources(context.applicationContext),
        context.filesDir
    )

    internal constructor(dao: CardDao, resourceManager: CardResourceManager) : this(
        null,
        dao,
        resourceManager,
        resourceManager as? CardResources,
        null
    )
    private val initializationMutex = Mutex()

    val cards = dao.observeAll()

    suspend fun initialize() = initializationMutex.withLock {
        checkNotNull(database).withTransaction {
            val existing = dao.getAll().associateBy { it.stableKey }
            LegacyCardMigration.seeds.forEach { seed ->
                val migrated = LegacyCardMigration.seedCard(checkNotNull(legacyFilesDir), seed)
                val current = existing[seed.stableKey]
                if (current == null) {
                    check(dao.insert(migrated) != -1L) { "No se pudo insertar ${seed.title}" }
                } else {
                    val updated = current.copy(
                        imagePath = current.imagePath?.takeIf { resourceManager.isUsable(it, "images") }
                            ?: migrated.imagePath,
                        audioPath = current.audioPath?.takeIf { resourceManager.isUsable(it, "audio") }
                            ?: migrated.audioPath
                    )
                    if (updated != current) check(dao.update(updated) == 1) {
                        "No se pudo actualizar ${seed.title}"
                    }
                }
            }
        }
    }

    suspend fun create(title: String): CardEntity {
        require(title.isNotBlank())
        val current = dao.getAll()
        val card = CardEntity(
            0L,
            UUID.randomUUID().toString(),
            title.trim(),
            (current.maxOfOrNull { it.position } ?: -1) + 1,
            true,
            null,
            null
        )
        val id = dao.insert(card)
        check(id != -1L) { "No se pudo insertar la tarjeta" }
        return card.copy(id = id)
    }

    suspend fun update(card: CardEntity) {
        require(card.title.isNotBlank())
        check(dao.update(card.copy(title = card.title.trim())) == 1) {
            "La tarjeta ya no existe"
        }
    }

    suspend fun setImagePath(card: CardEntity, relativePath: String?) =
        update(card.copy(imagePath = relativePath).also {
            check(relativePath == null || resourceManager.isUsable(relativePath, "images")) {
                "La fotografía no existe o no es válida"
            }
        })

    suspend fun setAudioPath(card: CardEntity, relativePath: String?) =
        update(card.copy(audioPath = relativePath).also {
            check(relativePath == null || resourceManager.isUsable(relativePath, "audio")) {
                "El audio no existe o no es válido"
            }
        })

    suspend fun delete(card: CardEntity) {
        val deletion = resourceManager.deleteResources(card)
        if (deletion.failure != null) {
            check(dao.update(card.copy(
                imagePath = deletion.imagePath,
                audioPath = deletion.audioPath
            )) == 1) {
                "No se pudo sincronizar los recursos de la tarjeta"
            }
            throw deletion.failure
        }

        try {
            check(dao.delete(card) == 1) { "La tarjeta ya no existe" }
        } catch (error: Throwable) {
            check(dao.update(card.copy(imagePath = null, audioPath = null)) == 1) {
                "No se pudo conservar la tarjeta tras eliminar sus recursos"
            }
            throw error
        }
    }

    fun resources(): CardResources = checkNotNull(resources)
}
