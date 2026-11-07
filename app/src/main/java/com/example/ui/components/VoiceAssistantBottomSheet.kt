package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Scheme
import com.example.service.SpeechManager
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VoiceAssistantBottomSheet(
    speechManager: SpeechManager,
    currentLanguage: AppLanguage,
    assistantMessage: String,
    matchedSchemes: List<Scheme>,
    onVoiceQuery: (String) -> Unit,
    onSchemeSelected: (Scheme) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isListening by speechManager.isListening.collectAsState()
    val isSpeaking by speechManager.isSpeaking.collectAsState()
    val spokenText by speechManager.spokenText.collectAsState()

    var isSlowMode by remember { mutableStateOf(false) }

    // Pulsing animation for microphone
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF9FCFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(EmeraldContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "YojanaMitra AI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                        Text(
                            text = "आपकी सहायता के लिए हमेशा तैयार",
                            fontSize = 11.sp,
                            color = Color(0xFF66756D)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_voice_assistant_btn")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Assistant Spoken Message Card
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = assistantMessage,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = OnEmeraldContainer,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Repeat Button
                        IconButton(
                            onClick = { speechManager.repeatLastResponse(currentLanguage) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Repeat",
                                tint = EmeraldPrimary
                            )
                        }

                        // Slow Speed Toggle Button
                        IconButton(
                            onClick = {
                                isSlowMode = speechManager.toggleSlowSpeed(currentLanguage)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Slow speed",
                                tint = if (isSlowMode) WarmAmber else EmeraldPrimary
                            )
                        }

                        // Stop Speaking Button
                        if (isSpeaking) {
                            IconButton(
                                onClick = { speechManager.stopSpeaking() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop",
                                    tint = Color(0xFFC74747)
                                )
                            }
                        }
                    }
                }
            }

            val voiceStatus by speechManager.status.collectAsState()

            // Voice Interaction Component (Central Microphone + Status Indicator)
            VoiceInteractionComponent(
                status = voiceStatus,
                spokenText = spokenText,
                currentLanguage = currentLanguage,
                onMicClick = {
                    when (voiceStatus) {
                        com.example.data.model.VoiceStatus.LISTENING -> {
                            speechManager.stopListening()
                        }
                        com.example.data.model.VoiceStatus.SPEAKING -> {
                            speechManager.stopSpeaking()
                        }
                        com.example.data.model.VoiceStatus.PROCESSING -> {
                            // in progress
                        }
                        com.example.data.model.VoiceStatus.IDLE -> {
                            speechManager.startListening(currentLanguage) { recognized ->
                                onVoiceQuery(recognized)
                            }
                        }
                    }
                },
                onQuickPromptClick = { prompt ->
                    onVoiceQuery(prompt)
                },
                isCardStyle = false
            )

            // If schemes matched by voice query
            if (matchedSchemes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "उपयुक्त योजनाएं (${matchedSchemes.size}):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                matchedSchemes.take(3).forEach { scheme ->
                    Card(
                        onClick = {
                            onSchemeSelected(scheme)
                            onDismiss()
                        },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = scheme.schemeNameHindi,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = scheme.benefitSummaryHindi,
                                    fontSize = 11.sp,
                                    color = EmeraldPrimary
                                )
                            }
                            Button(
                                onClick = {
                                    onSchemeSelected(scheme)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("देखें", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
