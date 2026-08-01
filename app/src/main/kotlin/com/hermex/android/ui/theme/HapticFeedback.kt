package com.hermex.android.ui.theme

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic feedback utility — iOS HapticButton equivalent.
 * Uses Android VibrationEffect for light/medium/heavy taps.
 */
object HapticFeedback {
    enum class Style {
        Light,   // Selection feedback
        Medium,  // Impact feedback
        Heavy,   // Heavy impact
        Success, // Notification success
        Error,   // Notification error
    }

    fun perform(context: Context, style: Style = Style.Light) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return

        val effect = when (style) {
            Style.Light -> VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
            Style.Medium -> VibrationEffect.createOneShot(20, 128)
            Style.Heavy -> VibrationEffect.createOneShot(40, 200)
            Style.Success -> VibrationEffect.createWaveform(longArrayOf(0, 30, 50, 30), intArrayOf(0, 80, 0, 200), -1)
            Style.Error -> VibrationEffect.createWaveform(longArrayOf(0, 50, 30, 50), intArrayOf(0, 200, 0, 200), -1)
        }
        vibrator.vibrate(effect)
    }
}
