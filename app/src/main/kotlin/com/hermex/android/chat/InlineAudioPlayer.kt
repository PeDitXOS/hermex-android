package com.hermex.android.chat

import android.media.MediaPlayer
import kotlinx.coroutines.*
import java.io.File

/**
 * Telegram-style inline audio player. Lazy-loads audio from file, provides
 * play/pause, scrubbing, duration, and current time. One clip at a time via PlaybackCenter.
 */
class InlineAudioPlayer {
    enum class Phase { Idle, Loading, Ready, Failed }

    var phase: Phase = Phase.Idle
        private set
    var isPlaying: Boolean = false
        private set
    var currentTime: Double = 0.0
        private set
    var duration: Double = 0.0
        private set
    var errorMessage: String? = null
        private set

    private var player: MediaPlayer? = null
    private var ticker: Job? = null
    private var didLoad = false

    /** Coordinates one-at-a-time playback across multiple players. */
    object PlaybackCenter {
        private var active: InlineAudioPlayer? = null
        fun playbackWillBegin(for player: InlineAudioPlayer) {
            active?.pause()
            active = player
        }
        fun clearActive(player: InlineAudioPlayer) {
            if (active === player) active = null
        }
    }

    fun loadFromFile(file: File, scope: CoroutineScope) {
        if (didLoad) return
        didLoad = true
        phase = Phase.Loading
        scope.launch(Dispatchers.IO) {
            try {
                val mp = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                }
                withContext(Dispatchers.Main) {
                    player = mp
                    duration = mp.duration / 1000.0
                    phase = Phase.Ready
                    mp.setOnCompletionListener {
                        isPlaying = false
                        currentTime = 0.0
                        stopTicker()
                        PlaybackCenter.clearActive(this@InlineAudioPlayer)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                    phase = Phase.Failed
                }
            }
        }
    }

    fun togglePlayPause(scope: CoroutineScope) {
        if (phase != Phase.Ready) return
        if (isPlaying) pause() else play(scope)
    }

    private fun play(scope: CoroutineScope) {
        val mp = player ?: return
        PlaybackCenter.playbackWillBegin(this)
        mp.start()
        isPlaying = true
        startTicker(scope)
    }

    fun pause() {
        player?.pause()
        isPlaying = false
        stopTicker()
    }

    fun seekTo(timeSeconds: Double) {
        player?.seekTo((timeSeconds * 1000).toInt())
        currentTime = timeSeconds
    }

    fun teardown() {
        pause()
        player?.release()
        player = null
        phase = Phase.Idle
        didLoad = false
    }

    private fun startTicker(scope: CoroutineScope) {
        stopTicker()
        ticker = scope.launch(Dispatchers.Main) {
            while (isActive && isPlaying) {
                player?.let { currentTime = it.currentPosition / 1000.0 }
                delay(200)
            }
        }
    }

    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }
}
