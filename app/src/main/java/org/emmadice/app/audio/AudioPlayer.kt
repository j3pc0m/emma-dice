package org.emmadice.app.audio

import android.media.MediaPlayer
import java.io.File

/** Keeps one asynchronously prepared player per communication card. */
class AudioPlayer {
    private enum class State { IDLE, PREPARING, PREPARED, PLAYING }

    private class Entry(
        val key: String,
        val path: String,
        val length: Long,
        val lastModified: Long,
        val player: MediaPlayer
    ) {
        var state = State.IDLE
        var prepareToken = 0L
        var playWhenPrepared = false
        var errorCallback: (() -> Unit)? = null
        var released = false
    }

    private val entries = mutableMapOf<String, Entry>()
    private var activeEntry: Entry? = null
    private var released = false

    fun preload(key: String, audioFile: File) {
        if (released || !isUsable(audioFile)) {
            invalidate(key)
            return
        }
        val entry = entryFor(key, audioFile) ?: return
        if (entry.state == State.IDLE) prepare(entry)
    }

    /** Requests playback without blocking the UI thread. */
    fun play(
        key: String,
        audioFile: File,
        onError: (() -> Unit)? = null
    ): Boolean {
        if (released || !isUsable(audioFile)) {
            invalidate(key)
            return false
        }
        val entry = entryFor(key, audioFile) ?: return false
        cancelPendingPlaybackExcept(entry)
        if (entry.state == State.PLAYING) {
            stopActiveExcept(null)
        } else {
            stopActiveExcept(entry)
        }
        entry.errorCallback = onError
        entry.playWhenPrepared = true
        when (entry.state) {
            State.PREPARED -> start(entry)
            State.PLAYING -> Unit
            State.IDLE -> prepare(entry)
            State.PREPARING -> Unit
        }
        return true
    }

    fun stop() = stopActiveExcept(null)

    fun release() {
        if (released) return
        released = true
        entries.values.toList().forEach(::releaseEntry)
        entries.clear()
        activeEntry = null
    }

    private fun entryFor(key: String, file: File): Entry? {
        val existing = entries[key]
        if (existing != null && matches(existing, file)) return existing
        if (existing != null) releaseEntry(existing)
        return try {
            val player = MediaPlayer()
            val entry = Entry(key, file.absolutePath, file.length(), file.lastModified(), player)
            player.setVolume(1f, 1f)
            player.setOnCompletionListener { completedPlayer ->
                if (!isCurrent(entry) || entry.player !== completedPlayer) return@setOnCompletionListener
                entry.state = State.IDLE
                entry.errorCallback = null
                if (activeEntry === entry) activeEntry = null
            }
            player.setOnErrorListener { failedPlayer, _, _ ->
                if (isCurrent(entry) && entry.player === failedPlayer) {
                    val callback = entry.errorCallback
                    entry.errorCallback = null
                    removeEntry(entry)
                    callback?.invoke()
                } else {
                    safelyRelease(failedPlayer)
                }
                true
            }
            player.setDataSource(file.absolutePath)
            entries[key] = entry
            entry
        } catch (_: Exception) {
            null
        }
    }

    private fun prepare(entry: Entry) {
        if (!isCurrent(entry) || entry.released) return
        entry.state = State.PREPARING
        val prepareToken = entry.prepareToken + 1
        entry.prepareToken = prepareToken
        entry.player.setOnPreparedListener { preparedPlayer ->
            if (!isCurrent(entry) || entry.player !== preparedPlayer ||
                entry.prepareToken != prepareToken
            ) return@setOnPreparedListener
            entry.state = State.PREPARED
            if (entry.playWhenPrepared) start(entry)
        }
        try {
            entry.player.reset()
            entry.player.setDataSource(entry.path)
            entry.player.prepareAsync()
        } catch (_: Exception) {
            fail(entry)
        }
    }

    private fun start(entry: Entry) {
        if (!isCurrent(entry) || entry.released || entry.state != State.PREPARED) return
        try {
            stopActiveExcept(entry)
            entry.playWhenPrepared = false
            entry.player.start()
            entry.state = State.PLAYING
            activeEntry = entry
        } catch (_: IllegalStateException) {
            fail(entry)
        }
    }

    private fun stopActiveExcept(except: Entry?) {
        val current = activeEntry ?: return
        if (current === except) return
        try {
            if (current.state == State.PLAYING || current.state == State.PREPARED) current.player.stop()
            current.state = State.IDLE
            current.playWhenPrepared = false
            current.errorCallback = null
        } catch (_: IllegalStateException) {
            removeEntry(current)
        }
        if (activeEntry === current) activeEntry = null
    }

    private fun cancelPendingPlaybackExcept(accepted: Entry) {
        entries.values.forEach { entry ->
            if (entry !== accepted) {
                entry.playWhenPrepared = false
                entry.errorCallback = null
            }
        }
    }

    private fun invalidate(key: String) = entries.remove(key)?.let(::releaseEntry)

    private fun removeEntry(entry: Entry) {
        if (entries[entry.key] === entry) entries.remove(entry.key)
        if (activeEntry === entry) activeEntry = null
        releaseEntry(entry)
    }

    private fun releaseEntry(entry: Entry) {
        if (entry.released) return
        if (activeEntry === entry) activeEntry = null
        entry.released = true
        entry.playWhenPrepared = false
        entry.errorCallback = null
        safelyRelease(entry.player)
    }

    private fun fail(entry: Entry) {
        if (!isCurrent(entry)) return
        val callback = entry.errorCallback
        entry.errorCallback = null
        removeEntry(entry)
        callback?.invoke()
    }

    private fun isCurrent(entry: Entry) = !released && !entry.released && entries[entry.key] === entry

    private fun matches(entry: Entry, file: File) =
        entry.path == file.absolutePath && entry.length == file.length() &&
            entry.lastModified == file.lastModified() && isUsable(file)

    private fun isUsable(file: File) = file.exists() && file.length() > 0L

    private fun safelyRelease(player: MediaPlayer) {
        try { player.release() } catch (_: Exception) { /* Ya liberado. */ }
    }
}
