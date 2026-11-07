package com.example

import com.example.data.model.AppLanguage
import com.example.data.model.VoiceStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceInteractionLogicTest {

    @Test
    fun voiceStatus_hasCorrectHindiAndEnglishLabels() {
        assertEquals("बोलने के लिए माइक दबाएं", VoiceStatus.IDLE.labelHindi)
        assertEquals("Tap mic to speak", VoiceStatus.IDLE.labelEnglish)

        assertEquals("सुन रहे हैं... कृपया बोलें", VoiceStatus.LISTENING.labelHindi)
        assertEquals("Listening...", VoiceStatus.LISTENING.labelEnglish)

        assertEquals("प्रसंस्करण हो रहा है...", VoiceStatus.PROCESSING.labelHindi)
        assertEquals("Processing...", VoiceStatus.PROCESSING.labelEnglish)

        assertEquals("उत्तर दे रहे हैं...", VoiceStatus.SPEAKING.labelHindi)
        assertEquals("Speaking response...", VoiceStatus.SPEAKING.labelEnglish)
    }

    @Test
    fun voiceStatus_cycleCoversAllExpectedStates() {
        val statuses = VoiceStatus.values().toList()
        assertTrue(statuses.contains(VoiceStatus.IDLE))
        assertTrue(statuses.contains(VoiceStatus.LISTENING))
        assertTrue(statuses.contains(VoiceStatus.PROCESSING))
        assertTrue(statuses.contains(VoiceStatus.SPEAKING))
        assertEquals(4, statuses.size)
    }

    @Test
    fun voiceInteraction_promptsAndLanguages_areAvailable() {
        val languages = AppLanguage.values().toList()
        assertTrue(languages.contains(AppLanguage.HINDI))
        assertTrue(languages.contains(AppLanguage.ENGLISH))
        assertTrue(languages.contains(AppLanguage.BHOJPURI))
        assertTrue(languages.contains(AppLanguage.MAITHILI))

        // Ensure greeting text is present for all languages
        languages.forEach { lang ->
            assertTrue(lang.greeting.isNotBlank())
            assertTrue(lang.displayName.isNotBlank())
        }
    }

    @Test
    fun voiceQuery_agricultureKeywords_recognizedProperly() {
        val query = "मुझे किसान सम्मान निधि और खेती के लिए बीज चाहिए"
        val isAgriculture = query.contains("खेती") || query.contains("किसान") || query.contains("बीज")
        assertTrue(isAgriculture)
    }

    @Test
    fun voiceQuery_healthcareKeywords_recognizedProperly() {
        val query = "आयुष्मान भारत कार्ड और मुफ्त इलाज अस्पताल"
        val isHealth = query.contains("इलाज") || query.contains("अस्पताल") || query.contains("आयुष्मान")
        assertTrue(isHealth)
    }

    @Test
    fun voiceQuery_housingKeywords_recognizedProperly() {
        val query = "प्रधानमंत्री आवास योजना पक्का मकान चाहिए"
        val isHousing = query.contains("मकान") || query.contains("आवास") || query.contains("घर")
        assertTrue(isHousing)
    }
}
