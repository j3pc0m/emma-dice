package org.emmadice.app.audio

import android.media.MediaPlayer
import java.io.File

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    fun play(audioFile: File): Boolean {
        if (!audioFile.exists() || audioFile.length() == 0L) {
            return false
        }

        stop()

        return try {
            val player = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)

                setOnCompletionListener {
                    it.release()

                    if (mediaPlayer === it) {
                        mediaPlayer = null
                    }
                }

                setOnErrorListener { failedPlayer, _, _ ->
                    failedPlayer.release()

                    if (mediaPlayer === failedPlayer) {
                        mediaPlayer = null
                    }

                    true
                }

                setVolume(1f, 1f)
                prepare()
                start()
            }

            mediaPlayer = player
            true
        } catch (_: Exception) {
            mediaPlayer?.release()
            mediaPlayer = null
            false
        }
    }

    fun stop() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
            } catch (_: IllegalStateException) {
                // El reproductor ya no estaba en un estado válido.
            } finally {
                player.release()
            }
        }

        mediaPlayer = null
    }
}