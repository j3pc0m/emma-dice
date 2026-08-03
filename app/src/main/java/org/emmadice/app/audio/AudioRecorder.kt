package org.emmadice.app.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorder(
    private val context: Context
) {
    private var mediaRecorder: MediaRecorder? = null

    fun start(outputFile: File) {
        check(mediaRecorder == null) {
            "Ya existe una grabación en curso"
        }

        outputFile.parentFile?.mkdirs()

        if (outputFile.exists()) {
            outputFile.delete()
        }

        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        try {
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
        } catch (exception: Exception) {
            recorder.release()
            mediaRecorder = null
            outputFile.delete()
            throw exception
        }
    }

    fun stop(): Boolean {
        val recorder = mediaRecorder ?: return false

        return try {
            recorder.stop()
            true
        } catch (_: RuntimeException) {
            false
        } finally {
            recorder.release()
            mediaRecorder = null
        }
    }

    fun cancel() {
        val recorder = mediaRecorder ?: return

        try {
            recorder.stop()
        } catch (_: RuntimeException) {
            // Una grabación demasiado corta puede fallar al detenerse.
        } finally {
            recorder.release()
            mediaRecorder = null
        }
    }

    fun isRecording(): Boolean = mediaRecorder != null
}