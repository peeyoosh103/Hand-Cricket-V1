package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.commentary.CommentaryOutput
import com.example.domain.commentary.Commentator
import com.example.ui.theme.*

@Composable
fun LiveCommentaryCard(
    currentCommentary: CommentaryOutput?,
    commentaryHistory: List<CommentaryOutput>,
    isVoiceEnabled: Boolean,
    onToggleVoice: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showHistoryDialog by remember { mutableStateOf(false) }

    // Pulsing animation for the red LIVE dot
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_commentary_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFF1E293B), CricketPitchGreen.copy(alpha = 0.6f), Color(0xFF1E293B))
            ),
            width = 1.2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Live Indicator, Title, Audio & History Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CricketWicketRed.copy(alpha = alpha))
                    )
                    Text(
                        text = "LIVE COMMENTARY | सीधा प्रसारण",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CricketGold,
                            letterSpacing = 0.6.sp,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // History Icon
                    if (commentaryHistory.isNotEmpty()) {
                        IconButton(
                            onClick = { showHistoryDialog = true },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("commentary_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Commentary Log",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Audio Mute/Unmute Quick Toggle
                    IconButton(
                        onClick = { onToggleVoice(!isVoiceEnabled) },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("commentary_audio_toggle")
                    ) {
                        Icon(
                            imageVector = if (isVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isVoiceEnabled) "Mute Voice" else "Unmute Voice",
                            tint = if (isVoiceEnabled) CricketPitchLightGreen else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Dialogue Content
            val output = currentCommentary
            if (output != null && output.lines.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    output.lines.forEach { dialogue ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Speaker Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (dialogue.speaker.isMale) Color(0xFF0F766E).copy(alpha = 0.25f)
                                else Color(0xFF9333EA).copy(alpha = 0.25f),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.horizontalGradient(
                                        if (dialogue.speaker.isMale) listOf(Color(0xFF14B8A6), Color(0xFF0D9488))
                                        else listOf(Color(0xFFA855F7), Color(0xFFC084FC))
                                    ),
                                    width = 1.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = dialogue.speaker.emojiAvatar,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = dialogue.speaker.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (dialogue.speaker.isMale) Color(0xFF5EEAD4) else Color(0xFFE9D5FF),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Commentary Hindi text
                            Text(
                                text = dialogue.text,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "मैच शुरू होने जा रहा है... पहली गेंद का इंतज़ार!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }

    // Commentary History Modal
    if (showHistoryDialog) {
        Dialog(onDismissRequest = { showHistoryDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumCardNavy)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎙️ COMMENTARY LOG",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = CricketGold
                            )
                        )
                        IconButton(onClick = { showHistoryDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                        }
                    }

                    HorizontalDivider(color = StadiumCardBorder)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(commentaryHistory) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = ScoreboardBg)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    item.lines.forEach { line ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${line.speaker.emojiAvatar} ${line.speaker.displayName}:",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (line.speaker.isMale) Color(0xFF5EEAD4) else Color(0xFFE9D5FF)
                                                )
                                            )
                                            Text(
                                                text = line.text,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = TextPrimary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
