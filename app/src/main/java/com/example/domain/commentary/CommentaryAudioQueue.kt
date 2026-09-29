package com.example.domain.commentary

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.*
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue

class CommentaryAudioQueue(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var isHindiSupported = false

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val speechQueue = ConcurrentLinkedQueue<QueueItem>()
    private var isSpeaking = false
    private var currentPriority = CommentaryPriority.LOW

    private var defaultVoice: Voice? = null
    private var maleVoice: Voice? = null
    private var femaleVoice: Voice? = null

    // Configurable settings
    var isVoiceEnabled: Boolean = true
    var speechSpeedMultiplier: Float = 1.0f
    var volumePercent: Int = 100

    private data class QueueItem(
        val dialogue: CommentaryDialogue,
        val utteranceId: String,
        val priority: CommentaryPriority
    )

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            isTtsReady = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return
            val hindiLocale = Locale("hi", "IN")
            val result = ttsEngine.setLanguage(hindiLocale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Try generic Hindi
                val genericHindi = Locale("hi")
                val genericResult = ttsEngine.setLanguage(genericHindi)
                isHindiSupported = (genericResult != TextToSpeech.LANG_MISSING_DATA && genericResult != TextToSpeech.LANG_NOT_SUPPORTED)
            } else {
                isHindiSupported = true
            }

            // Inspect available voices
            try {
                defaultVoice = ttsEngine.voice
                val voices = ttsEngine.voices
                if (voices != null) {
                    val hindiVoices = voices.filter { it.locale.language == "hi" }
                    if (hindiVoices.isNotEmpty()) {
                        maleVoice = hindiVoices.firstOrNull { voice ->
                            val n = voice.name.lowercase()
                            n.contains("male") || n.contains("man") || n.contains("hic") || n.contains("hie")
                        } ?: hindiVoices.getOrNull(1)

                        femaleVoice = hindiVoices.firstOrNull { voice ->
                            val n = voice.name.lowercase()
                            n.contains("female") || n.contains("woman") || n.contains("hia") || n.contains("hid")
                        } ?: hindiVoices.firstOrNull()
                    }
                }
            } catch (e: Exception) {
                // Older Android / restricted engine fallback
            }

            ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeaking = true
                }

                override fun onDone(utteranceId: String?) {
                    isSpeaking = false
                    currentPriority = CommentaryPriority.LOW
                    scope.launch {
                        delay(100)
                        processNextInQueue()
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isSpeaking = false
                    currentPriority = CommentaryPriority.LOW
                    scope.launch {
                        processNextInQueue()
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    isSpeaking = false
                    currentPriority = CommentaryPriority.LOW
                    scope.launch {
                        processNextInQueue()
                    }
                }
            })

            isTtsReady = true
        } else {
            isTtsReady = false
        }
    }

    fun enqueueDialogue(dialogues: List<CommentaryDialogue>, priority: CommentaryPriority) {
        if (!isVoiceEnabled || !isTtsReady || dialogues.isEmpty()) return

        // Always stop previous dialogue instantly on new game action to avoid lag or overlap
        stopCurrentAudio()

        dialogues.forEachIndexed { index, dialogue ->
            val utteranceId = "comm_${System.currentTimeMillis()}_$index"
            speechQueue.add(QueueItem(dialogue, utteranceId, priority))
        }

        processNextInQueue()
    }

    private fun processNextInQueue() {
        if (!isVoiceEnabled || !isTtsReady) {
            speechQueue.clear()
            isSpeaking = false
            return
        }

        val item = speechQueue.poll() ?: run {
            isSpeaking = false
            currentPriority = CommentaryPriority.LOW
            return
        }

        val ttsEngine = tts ?: return
        isSpeaking = true
        currentPriority = item.priority

        try {
            // Distinct Male and Female voice acoustics ensuring <= 1 second playback
            val baseRate = (1.38f * speechSpeedMultiplier).coerceIn(1.1f, 2.0f)
            if (item.dialogue.speaker.isMale) {
                // Rahul - deep resonant male pitch (~0.78f)
                ttsEngine.setPitch(0.78f)
                ttsEngine.setSpeechRate(baseRate)
                maleVoice?.let { ttsEngine.voice = it } ?: defaultVoice?.let { ttsEngine.voice = it }
            } else {
                // Neha - crisp bright female pitch (~1.35f)
                ttsEngine.setPitch(1.35f)
                ttsEngine.setSpeechRate(baseRate * 1.05f)
                femaleVoice?.let { ttsEngine.voice = it } ?: defaultVoice?.let { ttsEngine.voice = it }
            }

            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, (volumePercent / 100f).coerceIn(0f, 1f))
            }

            // QUEUE_FLUSH ensures previous audio is cleared and new short line plays instantly
            ttsEngine.speak(item.dialogue.text, TextToSpeech.QUEUE_FLUSH, params, item.utteranceId)
        } catch (e: Exception) {
            isSpeaking = false
            processNextInQueue()
        }
    }

    fun stopCurrentAudio() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            // Ignore
        }
        speechQueue.clear()
        isSpeaking = false
        currentPriority = CommentaryPriority.LOW
    }

    fun shutdown() {
        try {
            scope.cancel()
            tts?.stop()
            tts?.shutdown()
            tts = null
            isTtsReady = false
        } catch (e: Exception) {
            // Ignore
        }
    }
}
