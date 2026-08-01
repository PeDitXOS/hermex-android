package com.hermex.android.chat

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import java.io.File
import java.util.UUID

/**
 * Records short voice notes to M4A/AAC files using MediaRecorder.
 * Hold-to-talk pattern: begin() → recording → finish() returns RecordedVoiceNote or null.
 * Slide up to cancel(). Max 5 min, min 0.5s (accidental tap filter).
 */
class VoiceNoteRecorder(private val cacheDir: File) {
    sealed class State {
        data object Idle : State()
        data object RequestingPermission : State()
        data object Recording : State()
    }

    data class RecordedVoiceNote(
        val file: File,
        val filename: String,
        val durationMs: Long,
    )

    var state: State = State.Idle
        private set
    var elapsedMs: Long = 0L
        private set
    var errorMessage: String? = null
        private set

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var ticker: Job? = null
    private var startTime: Long = 0L

    companion object {
        const val MAX_DURATION_MS = 5 * 60 * 1000L
        const val MIN_DURATION_MS = 500L
    }

    val isRecording: Boolean get() = state is State.Recording
    val hasReachedMaxDuration: Boolean get() = elapsedMs >= MAX_DURATION_MS

    fun begin(scope: CoroutineScope) {
        if (state !is State.Idle) return
        errorMessage = null
        elapsedMs = 0L
        state = State.Recording

        try {
            val filename = "voice-note-${UUID.randomUUID().toString().take(8)}.m4a"
            val file = File(cacheDir, filename)
            outputFile = file

            val mr = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(32000)
                setAudioSamplingRate(44100)
                setAudioChannels(1)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            recorder = mr
            startTime = System.currentTimeMillis()
            startTicker(scope)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Could not start recording"
            cancel()
        }
    }

    fun finish(): RecordedVoiceNote? {
        if (state !is State.Recording) return null
        val duration = elapsedMs
        stopRecorder()
        stopTicker()
        val file = outputFile
        outputFile = null
        state = State.Idle
        elapsedMs = 0L

        if (duration < MIN_DURATION_MS || file == null || !file.exists()) {
            file?.delete()
            return null
        }
        return RecordedVoiceNote(
            file = file,
            filename = file.name,
            durationMs = duration,
        )
    }

    fun cancel() {
        stopRecorder()
        stopTicker()
        outputFile?.delete()
        outputFile = null
        state = State.Idle
        elapsedMs = 0L
    }

    private fun stopRecorder() {
        try {
            recorder?.stop()
        } catch (_: Exception) {}
        recorder?.release()
        recorder = null
    }

    private fun startTicker(scope: CoroutineScope) {
        stopTicker()
        ticker = scope.launch(Dispatchers.Main) {
            while (isActive) {
                elapsedMs = System.currentTimeMillis() - startTime
                if (elapsedMs >= MAX_DURATION_MS) {
                    // Auto-stop at max duration
                    break
                }
                delay(100)
            }
        }
    }

    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }
}
