package com.example.domain.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class SoundType {
    BAT_HIT,
    FOUR_BOUNDARY,
    SIX_MASSIVE,
    WICKET_OUT,
    TOSS_COIN,
    CROWD_CHEER,
    BUTTON_CLICK,
    MATCH_WIN
}

class SoundManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            // Audio stream initialization fallback
        }
    }

    fun playSound(type: SoundType, enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            try {
                when (type) {
                    SoundType.BUTTON_CLICK -> {
                        // Generic UI button click beep sound disabled/removed completely
                    }
                    SoundType.BAT_HIT -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
                    }
                    SoundType.FOUR_BOUNDARY -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 160)
                    }
                    SoundType.SIX_MASSIVE -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 220)
                    }
                    SoundType.WICKET_OUT -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 280)
                    }
                    SoundType.TOSS_COIN -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
                    }
                    SoundType.CROWD_CHEER, SoundType.MATCH_WIN -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE, 350)
                    }
                }
            } catch (e: Exception) {
                // Ignore tone generator errors gracefully
            }
        }
    }

    fun playHaptic(type: SoundType, enabled: Boolean) {
        if (!enabled) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    SoundType.BUTTON_CLICK -> VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                    SoundType.BAT_HIT -> VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
                    SoundType.FOUR_BOUNDARY -> VibrationEffect.createWaveform(longArrayOf(0, 50, 40, 60), -1)
                    SoundType.SIX_MASSIVE -> VibrationEffect.createWaveform(longArrayOf(0, 70, 50, 90), -1)
                    SoundType.WICKET_OUT -> VibrationEffect.createWaveform(longArrayOf(0, 120, 60, 150), -1)
                    SoundType.CROWD_CHEER, SoundType.MATCH_WIN -> VibrationEffect.createWaveform(longArrayOf(0, 80, 50, 80, 50, 120), -1)
                    else -> VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (e: Exception) {
            // Ignore vibration error
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
