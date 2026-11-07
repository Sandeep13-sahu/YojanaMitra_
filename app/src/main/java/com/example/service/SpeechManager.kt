package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _status = MutableStateFlow(com.example.data.model.VoiceStatus.IDLE)
    val status: StateFlow<com.example.data.model.VoiceStatus> = _status.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _lastSpokenResponse = MutableStateFlow("")
    val lastSpokenResponse: StateFlow<String> = _lastSpokenResponse.asStateFlow()

    private var isSlowSpeed = false

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("SpeechManager", "Failed to init TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            textToSpeech?.language = Locale("hi", "IN")
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _status.value = com.example.data.model.VoiceStatus.SPEAKING
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _status.value = com.example.data.model.VoiceStatus.IDLE
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _status.value = com.example.data.model.VoiceStatus.IDLE
                }
            })
        }
    }

    fun startListening(
        language: AppLanguage = AppLanguage.HINDI,
        onResult: (String) -> Unit = {}
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w("SpeechManager", "Speech recognition not available")
            return
        }

        stopSpeaking()

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    _isListening.value = true
                    _status.value = com.example.data.model.VoiceStatus.LISTENING
                }

                override fun onBeginningOfSpeech() {
                    _status.value = com.example.data.model.VoiceStatus.LISTENING
                }
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _isListening.value = false
                    _status.value = com.example.data.model.VoiceStatus.PROCESSING
                }

                override fun onError(error: Int) {
                    _isListening.value = false
                    _status.value = com.example.data.model.VoiceStatus.IDLE
                }

                override fun onResults(results: Bundle?) {
                    _isListening.value = false
                    _status.value = com.example.data.model.VoiceStatus.PROCESSING
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""
                    if (text.isNotBlank()) {
                        _spokenText.value = text
                        onResult(text)
                    } else {
                        _status.value = com.example.data.model.VoiceStatus.IDLE
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""
                    if (text.isNotBlank()) {
                        _spokenText.value = text
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            val langTag = when (language) {
                AppLanguage.HINDI, AppLanguage.BHOJPURI, AppLanguage.MAITHILI -> "hi-IN"
                AppLanguage.ENGLISH -> "en-IN"
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "बोलकर योजना खोजें...")
        }

        try {
            _status.value = com.example.data.model.VoiceStatus.LISTENING
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            _status.value = com.example.data.model.VoiceStatus.IDLE
            Log.e("SpeechManager", "Failed to start listening", e)
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechManager", "Error stopping listening", e)
        } finally {
            _isListening.value = false
            _status.value = com.example.data.model.VoiceStatus.IDLE
        }
    }

    fun setStatus(newStatus: com.example.data.model.VoiceStatus) {
        _status.value = newStatus
    }

    fun speak(text: String, language: AppLanguage = AppLanguage.HINDI) {
        if (!isTtsReady || textToSpeech == null) return

        stopSpeaking()
        _lastSpokenResponse.value = text

        val locale = when (language) {
            AppLanguage.ENGLISH -> Locale("en", "IN")
            else -> Locale("hi", "IN")
        }
        textToSpeech?.language = locale
        textToSpeech?.setSpeechRate(if (isSlowSpeed) 0.75f else 0.95f)

        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "yojanamitra_utterance")
    }

    fun repeatLastResponse(language: AppLanguage) {
        if (_lastSpokenResponse.value.isNotBlank()) {
            speak(_lastSpokenResponse.value, language)
        }
    }

    fun toggleSlowSpeed(language: AppLanguage): Boolean {
        isSlowSpeed = !isSlowSpeed
        if (_lastSpokenResponse.value.isNotBlank() && _isSpeaking.value) {
            repeatLastResponse(language)
        }
        return isSlowSpeed
    }

    fun stopSpeaking() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
        _isSpeaking.value = false
        _status.value = com.example.data.model.VoiceStatus.IDLE
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
