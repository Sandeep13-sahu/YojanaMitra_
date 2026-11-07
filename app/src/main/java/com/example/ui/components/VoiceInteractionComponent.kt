package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.VoiceStatus
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.WarmAmber

/**
 * VoiceInteractionComponent
 *
 * A voice interaction UI component featuring:
 * 1. A central microphone button with dynamic state animations:
 *    - IDLE: Calm emerald accent with soft shadow.
 *    - LISTENING: Double pulsing concentric ripple rings with active mic icon.
 *    - PROCESSING: Smooth rotating circular progress indicator around the button.
 *    - SPEAKING: Soundwave ripples and stop/pause interaction.
 * 2. A text-based status indicator with status icon/spinner explicitly communicating
 *    'listening', 'processing', 'speaking', or 'idle' states.
 * 3. Real-time spoken transcript display and quick prompt chips for accessibility.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceInteractionComponent(
    status: VoiceStatus,
    spokenText: String = "",
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onMicClick: () -> Unit,
    onQuickPromptClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isCardStyle: Boolean = true
) {
    val content = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCardStyle) 16.dp else 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Spoken Transcript (if citizen has spoken or speech recognizer emitted partial text)
            AnimatedVisibility(
                visible = spokenText.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .testTag("voice_interaction_transcript")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "You said: " else "आपने कहा: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF55655D)
                        )
                        Text(
                            text = "\"$spokenText\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDeep
                        )
                    }
                }
            }

            // Central Microphone Button with Multi-layered State Animations
            CentralMicrophoneButton(
                status = status,
                onClick = onMicClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Text-Based Status Indicator
            VoiceStatusIndicator(
                status = status,
                currentLanguage = currentLanguage
            )

            // Optional Quick Prompt Chips for effortless testing and fast queries
            if (onQuickPromptClick != null && status == VoiceStatus.IDLE) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "Quick queries (or speak your own):" else "त्वरित विषय (या अपनी भाषा में बोलें):",
                    fontSize = 12.sp,
                    color = if (isCardStyle) EmeraldDeep.copy(alpha = 0.8f) else Color(0xFF55655D),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                val samplePrompts = when (currentLanguage) {
                    AppLanguage.ENGLISH -> listOf("Farming schemes", "Health card", "Housing", "Old age pension")
                    AppLanguage.BHOJPURI -> listOf("खेती योजना", "इलाज खातिर पैसा", "मकान योजना", "पेंशन")
                    AppLanguage.MAITHILI -> listOf("खेती लेल सहायता", "अस्पताल कार्ड", "मकान योजना", "वृद्धा पेंशन")
                    else -> listOf("खेती के लिए सहायता", "इलाज के लिए पैसे", "मकान योजना", "वृद्धावस्था पेंशन")
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    samplePrompts.forEach { prompt ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .height(32.dp)
                                .clickable { onQuickPromptClick(prompt) }
                                .testTag("quick_prompt_${prompt.replace(" ", "_")}")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDeep
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (isCardStyle) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .testTag("voice_interaction_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            content()
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .testTag("voice_interaction_section")
        ) {
            content()
        }
    }
}

/**
 * Central Microphone Button with animated concentric ripples, spinners, and status transitions.
 */
@Composable
fun CentralMicrophoneButton(
    status: VoiceStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mic_animations")

    // Ripple Pulse Scale for LISTENING
    val pulseScale1 by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (status == VoiceStatus.LISTENING) 1.35f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale_1"
    )

    val pulseAlpha1 by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (status == VoiceStatus.LISTENING) 0.05f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha_1"
    )

    // Speaking pulse
    val speakingPulse by transition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (status == VoiceStatus.SPEAKING) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaking_pulse"
    )

    val buttonColor = when (status) {
        VoiceStatus.LISTENING -> Color(0xFFD9383A) // Energetic recording crimson
        VoiceStatus.PROCESSING -> WarmAmber // Amber processing
        VoiceStatus.SPEAKING -> Color(0xFF1E88E5) // Blue speaking
        VoiceStatus.IDLE -> EmeraldPrimary // Lush Emerald
    }

    val contentDesc = when (status) {
        VoiceStatus.LISTENING -> "Listening. Tap to stop recording."
        VoiceStatus.PROCESSING -> "Processing query. Please wait."
        VoiceStatus.SPEAKING -> "Speaking response. Tap to stop."
        VoiceStatus.IDLE -> "Tap to speak and ask about government schemes."
    }

    Box(
        modifier = modifier
            .size(112.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer animated ripple ring when LISTENING
        if (status == VoiceStatus.LISTENING) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(pulseScale1)
                    .clip(CircleShape)
                    .background(Color(0xFFD9383A).copy(alpha = pulseAlpha1))
            )
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .scale(pulseScale1 * 0.9f)
                    .clip(CircleShape)
                    .background(Color(0xFFD9383A).copy(alpha = 0.25f))
            )
        } else if (status == VoiceStatus.SPEAKING) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(speakingPulse)
                    .clip(CircleShape)
                    .background(Color(0xFF1E88E5).copy(alpha = 0.2f))
            )
        }

        // Processing Spinner Ring
        if (status == VoiceStatus.PROCESSING) {
            CircularProgressIndicator(
                modifier = Modifier.size(86.dp),
                color = WarmAmber,
                strokeWidth = 4.dp
            )
        }

        // Central Primary Mic Button (72dp size, well above 48dp accessibility touch target)
        Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
            modifier = Modifier
                .size(72.dp)
                .testTag("voice_interaction_mic_button")
        ) {
            when (status) {
                VoiceStatus.LISTENING -> {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = contentDesc,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
                VoiceStatus.PROCESSING -> {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = contentDesc,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                VoiceStatus.SPEAKING -> {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = contentDesc,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                VoiceStatus.IDLE -> {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = contentDesc,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}

/**
 * Text-Based Status Indicator explicitly showing:
 * - 'listening'
 * - 'processing'
 * - 'speaking'
 * - 'idle'
 */
@Composable
fun VoiceStatusIndicator(
    status: VoiceStatus,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "indicator_dot_anim")
    val dotAlpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_interaction_status_indicator"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            when (status) {
                VoiceStatus.LISTENING -> {
                    // Pulsing Red Recording Dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD9383A).copy(alpha = dotAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Listening... (बोलें)" else "सुन रहे हैं... कृपया बोलें",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD9383A),
                        modifier = Modifier.testTag("voice_interaction_status_text")
                    )
                }
                VoiceStatus.PROCESSING -> {
                    // Small inline spinning circle
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = WarmAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Processing..." else "प्रसंस्करण हो रहा है... (Processing)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB26B00),
                        modifier = Modifier.testTag("voice_interaction_status_text")
                    )
                }
                VoiceStatus.SPEAKING -> {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Speaking response... (Tap mic to stop)" else "उत्तर दे रहे हैं... (रोकने के लिए दबाएं)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5),
                        modifier = Modifier.testTag("voice_interaction_status_text")
                    )
                }
                VoiceStatus.IDLE -> {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = EmeraldDeep,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Tap mic to speak (बोलने के लिए दबाएं)" else "बोलने के लिए माइक दबाएं",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep,
                        modifier = Modifier.testTag("voice_interaction_status_text")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Secondary descriptive helper text for users
        val helperText = when (status) {
            VoiceStatus.LISTENING -> if (currentLanguage == AppLanguage.ENGLISH) "Speak clearly into your microphone" else "अपनी आवश्यकता स्पष्ट आवाज़ में बताएं"
            VoiceStatus.PROCESSING -> if (currentLanguage == AppLanguage.ENGLISH) "Matching government schemes & eligibility..." else "सरकारी योजनाओं और पात्रता से मिलान जारी है..."
            VoiceStatus.SPEAKING -> if (currentLanguage == AppLanguage.ENGLISH) "Audio guidance playing via speaker" else "ध्वनि संदेश स्पीकर पर सुनाया जा रहा है"
            VoiceStatus.IDLE -> if (currentLanguage == AppLanguage.ENGLISH) "Ask about farming, health, pension or housing" else "खेती, आवास, पेंशन या छात्रवृत्ति के बारे में पूछें"
        }

        Text(
            text = helperText,
            fontSize = 12.sp,
            color = Color(0xFF55655D),
            textAlign = TextAlign.Center
        )
    }
}
