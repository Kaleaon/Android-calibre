package com.universalmedialibrary.services.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ttsDataStore by preferencesDataStore("tts_preferences")

@Singleton
class CoquiTTSService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val SPEECH_RATE_KEY = floatPreferencesKey("speech_rate")
        private val VOICE_MODEL_KEY = stringPreferencesKey("voice_model")
        private val EMOTION_STYLE_KEY = stringPreferencesKey("emotion_style")
        private val VOICE_PITCH_KEY = floatPreferencesKey("voice_pitch")
        
        // Default values
        const val DEFAULT_SPEECH_RATE = 1.0f
        const val DEFAULT_VOICE_MODEL = "neural_voice_v1"
        const val DEFAULT_EMOTION_STYLE = "neutral"
        const val DEFAULT_VOICE_PITCH = 1.0f
    }

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    // TTS Settings Flow
    val speechRate: Flow<Float> = context.ttsDataStore.data.map { preferences ->
        preferences[SPEECH_RATE_KEY] ?: DEFAULT_SPEECH_RATE
    }

    val voiceModel: Flow<String> = context.ttsDataStore.data.map { preferences ->
        preferences[VOICE_MODEL_KEY] ?: DEFAULT_VOICE_MODEL
    }

    val emotionStyle: Flow<String> = context.ttsDataStore.data.map { preferences ->
        preferences[EMOTION_STYLE_KEY] ?: DEFAULT_EMOTION_STYLE
    }

    val voicePitch: Flow<Float> = context.ttsDataStore.data.map { preferences ->
        preferences[VOICE_PITCH_KEY] ?: DEFAULT_VOICE_PITCH
    }

    suspend fun initializeTTS() {
        if (!isInitialized) {
            // Initialize Android TTS for now, Coqui integration planned for future
            initializeAndroidTTS()
        }
    }

    private suspend fun initializeAndroidTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
            }
        }
    }

    suspend fun speak(
        text: String,
        emotion: EmotionStyle = EmotionStyle.NEUTRAL,
        onProgress: ((Float) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        if (!isInitialized) {
            initializeTTS()
        }

        // Use Android TTS for now - Coqui integration coming soon
        speakWithAndroidTTS(text, onProgress, onComplete)
    }

    private fun speakWithAndroidTTS(
        text: String,
        onProgress: ((Float) -> Unit)?,
        onComplete: (() -> Unit)?
    ) {
        textToSpeech?.let { tts ->
            // Apply settings from flow (would need to be properly handled)
            tts.setSpeechRate(DEFAULT_SPEECH_RATE)
            
            // For Android TTS, we don't have real-time progress, so simulate it
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
            
            // Simulate progress (this would be improved with actual TTS callbacks)
            onProgress?.invoke(1.0f)
            onComplete?.invoke()
        }
    }

    suspend fun stop() {
        textToSpeech?.stop()
    }

    suspend fun pause() {
        // Android TTS doesn't have pause, so we stop
        textToSpeech?.stop()
    }

    suspend fun resume() {
        // For Android TTS, we would need to re-speak from the current position
    }

    suspend fun setSpeechRate(rate: Float) {
        context.ttsDataStore.edit { preferences ->
            preferences[SPEECH_RATE_KEY] = rate
        }
        textToSpeech?.setSpeechRate(rate)
    }

    suspend fun setVoiceModel(model: String) {
        context.ttsDataStore.edit { preferences ->
            preferences[VOICE_MODEL_KEY] = model
        }
        // Coqui voice model switching will be implemented in future update
    }

    suspend fun setEmotionStyle(style: String) {
        context.ttsDataStore.edit { preferences ->
            preferences[EMOTION_STYLE_KEY] = style
        }
        // Emotion control will be available with Coqui TTS integration
    }

    suspend fun setVoicePitch(pitch: Float) {
        context.ttsDataStore.edit { preferences ->
            preferences[VOICE_PITCH_KEY] = pitch
        }
        textToSpeech?.setPitch(pitch)
    }

    fun getAvailableVoices(): List<VoiceModel> {
        // Return placeholder voices for now - Coqui voices coming soon
        return listOf(
            VoiceModel("android_default", "Android Default", VoiceQuality.STANDARD),
            VoiceModel("android_high_quality", "Android High Quality", VoiceQuality.HIGH),
            VoiceModel("coqui_neural_female", "Coqui Neural Female (Coming Soon)", VoiceQuality.NEURAL),
            VoiceModel("coqui_neural_male", "Coqui Neural Male (Coming Soon)", VoiceQuality.NEURAL)
        )
    }

    fun getAvailableEmotions(): List<EmotionStyle> {
        // Return all emotion styles - will be functional with Coqui integration
        return listOf(
            EmotionStyle.NEUTRAL,
            EmotionStyle.HAPPY,
            EmotionStyle.SAD,
            EmotionStyle.ANGRY,
            EmotionStyle.CALM,
            EmotionStyle.EXCITED,
            EmotionStyle.MYSTERIOUS,
            EmotionStyle.DRAMATIC,
            EmotionStyle.ROMANTIC,
            EmotionStyle.SUSPENSEFUL
        )
    }

    fun release() {
        textToSpeech?.shutdown()
        isInitialized = false
    }
}

enum class EmotionStyle(val displayName: String) {
    NEUTRAL("Neutral"),
    HAPPY("Happy"),
    SAD("Sad"),
    ANGRY("Angry"),
    EXCITED("Excited"),
    CALM("Calm"),
    MYSTERIOUS("Mysterious"),
    DRAMATIC("Dramatic"),
    ROMANTIC("Romantic"),
    SUSPENSEFUL("Suspenseful")
}

data class VoiceModel(
    val id: String,
    val name: String,
    val quality: VoiceQuality,
    val description: String = "",
    val language: String = "en-US",
    val gender: VoiceGender = VoiceGender.NEUTRAL
)

enum class VoiceQuality(val displayName: String) {
    STANDARD("Standard"),
    HIGH("High Quality"),
    NEURAL("Neural (Premium)")
}

enum class VoiceGender(val displayName: String) {
    MALE("Male"),
    FEMALE("Female"),
    NEUTRAL("Neutral")
}