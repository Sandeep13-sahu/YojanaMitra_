package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.AppLanguage
import com.example.data.model.VoiceStatus
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary

/**
 * VoiceToTextInput:
 * A clean, accessible voice-to-text input component for initial user queries.
 * Integrates Android SpeechRecognizer with runtime audio permission checks,
 * live speech state indications (Idle, Listening, Processing), real-time transcription,
 * and direct search query submission.
 */
@Composable
fun VoiceToTextInput(
    query: String,
    onQueryChange: (String) -> Unit,
    onQuerySubmit: (String) -> Unit,
    voiceStatus: VoiceStatus,
    spokenText: String,
    currentLanguage: AppLanguage,
    onStartVoiceInput: () -> Unit,
    onStopVoiceInput: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String? = null
) {
    val context = LocalContext.current
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordPermission = isGranted
        if (isGranted) {
            onStartVoiceInput()
        } else {
            Toast.makeText(
                context,
                "आवाज़ से खोजने के लिए माइक्रोफोन की अनुमति दें (Please grant microphone permission)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val isListening = voiceStatus == VoiceStatus.LISTENING
    val isProcessing = voiceStatus == VoiceStatus.PROCESSING

    // Infinite breathing animation for active listening state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.22f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val micButtonColor by animateColorAsState(
        targetValue = when {
            isListening -> Color(0xFFD93025) // Vibrant Red when listening
            isProcessing -> Color(0xFFF4B400) // Amber when processing
            else -> EmeraldPrimary
        },
        label = "micColor"
    )

    val defaultPlaceholder = when (currentLanguage) {
        AppLanguage.ENGLISH -> "Type or tap mic to speak query (e.g. Kisan, Awas)..."
        else -> "बोलकर या लिखकर खोजें (जैसे: किसान, इलाज, मकान, छात्रवृत्ति)..."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_to_text_input_container")
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = if (isListening) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD93025))
            else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6E2DB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Search Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(EmeraldContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EmeraldDeep,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Query text input field
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text(
                            text = if (isListening) {
                                "सुन रहे हैं... बोलिए (Listening... speak now)"
                            } else if (isProcessing) {
                                "समझ रहे हैं... (Processing query...)"
                            } else {
                                placeholderText ?: defaultPlaceholder
                            },
                            fontSize = 13.sp,
                            color = if (isListening) Color(0xFFD93025) else Color(0xFF718279),
                            maxLines = 1
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("voice_query_text_field")
                )

                // Clear button when query is present
                if (query.isNotBlank() && !isListening) {
                    IconButton(
                        onClick = {
                            onQueryChange("")
                            onQuerySubmit("")
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("clear_voice_query_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear query",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Voice input toggle button (SpeechRecognizer integration)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing glow when listening
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .scale(pulseScale)
                                .background(Color(0xFFD93025).copy(alpha = 0.25f), CircleShape)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(micButtonColor)
                            .clickable {
                                if (isListening) {
                                    onStopVoiceInput()
                                } else {
                                    if (hasRecordPermission) {
                                        onStartVoiceInput()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            }
                            .testTag("voice_to_text_mic_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                                contentDescription = if (isListening) "Stop Listening" else "Speak Query",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Speech Feedback Badge
        AnimatedVisibility(
            visible = isListening || isProcessing || (spokenText.isNotBlank() && isListening),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = if (isListening) Color(0xFFFDE8E8) else EmeraldContainer,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (isListening) Color(0xFFD93025) else Color(0xFFF4B400),
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (spokenText.isNotBlank()) "आप बोल रहे हैं: \"$spokenText\""
                            else if (isListening) "बोलिए... (Listening to your speech in real-time)"
                            else "आवाज़ समझी जा रही है... (Processing speech)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isListening) Color(0xFFC5221F) else EmeraldDeep,
                            maxLines = 1
                        )
                    }

                    if (isListening) {
                        Text(
                            text = "रोकने के लिए टैप करें",
                            fontSize = 10.sp,
                            color = Color(0xFFC5221F),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onStopVoiceInput() }
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
