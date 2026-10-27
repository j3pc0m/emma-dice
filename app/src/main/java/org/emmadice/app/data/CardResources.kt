package org.emmadice.app.data

import android.content.Context
import org.emmadice.app.data.db.CardEntity
import java.io.File

data class ResourceDeletionResult(
    val imagePath: String?,
    val audioPath: String?,
    val failure: Throwable?
)

interface CardResourceManager {
    fun deleteResources(card: CardEntity): ResourceDeletionResult
    fun isUsable(relativePath: String?, directory: String): Boolean
}

class CardResources internal constructor(private val filesDir: File) : CardResourceManager {
    constructor(context: Context) : this(context.filesDir)
    fun imageFile(cardKey: String): File =
        fileForKey("images", cardKey, "_photo.jpg")

    fun audioFile(cardKey: String): File =
        fileForKey("audio", cardKey, "_audio.m4a")

    fun absolutePath(relativePath: String): String = resolveAuthorized(relativePath).absolutePath

    override fun deleteResources(card: CardEntity): ResourceDeletionResult {
        val image = deleteResource(card.imagePath, "images")
        val audio = deleteResource(card.audioPath, "audio")
        return ResourceDeletionResult(
            imagePath = image.first,
            audioPath = audio.first,
            failure = image.second ?: audio.second
        )
    }

    private fun deleteResource(path: String?, directory: String): Pair<String?, Throwable?> {
        if (path == null) return null to null
        return try {
            val file = resolveAuthorized(path, directory)
            if (file.exists()) {
                check(file.delete()) {
                    "No se pudo eliminar ${file.name}"
                }
            }
            null to null
        } catch (error: Throwable) {
            path to error
        }
    }

    override fun isUsable(relativePath: String?, directory: String): Boolean {
        if (relativePath == null) return false
        return runCatching {
            val file = resolveAuthorized(relativePath, directory)
            file.exists() && file.isFile && file.length() > 0L
        }.getOrDefault(false)
    }

    private fun fileForKey(directory: String, cardKey: String, suffix: String): File {
        require(isSafeKey(cardKey)) { "Clave de tarjeta no válida" }
        return File(authorizedDirectory(directory), "$cardKey$suffix")
    }

    private fun resolveAuthorized(relativePath: String, expectedDirectory: String? = null): File {
        require(!File(relativePath).isAbsolute) { "La ruta debe ser relativa" }
        val parts = relativePath.split('/', '\\')
        require(parts.size == 2 && parts[0] in AUTHORIZED_DIRECTORIES) {
            "Ruta de recurso no autorizada"
        }
        require(parts[1] != "." && parts[1] != ".." && parts[1].isNotBlank()) {
            "Ruta de recurso no autorizada"
        }
        require(expectedDirectory == null || parts[0] == expectedDirectory) {
            "Tipo de recurso no autorizado"
        }
        require(!parts[1].contains("..")) { "Ruta de recurso no autorizada" }
        val directory = authorizedDirectory(parts[0])
        val file = File(directory, parts[1]).canonicalFile
        require(file.parentFile == directory.canonicalFile) { "Ruta fuera del directorio autorizado" }
        return file
    }

    private fun authorizedDirectory(name: String): File {
        require(name in AUTHORIZED_DIRECTORIES) { "Directorio no autorizado" }
        return File(filesDir, name).canonicalFile
    }

    companion object {
        private val AUTHORIZED_DIRECTORIES = setOf("images", "audio")

        fun isSafeKey(key: String): Boolean =
            key.isNotBlank() && key != "." && key != ".." &&
                key.all { it.isLetterOrDigit() || it == '_' || it == '-' }
    }
}
