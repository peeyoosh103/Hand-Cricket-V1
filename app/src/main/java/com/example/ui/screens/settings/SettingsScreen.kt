package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.example.data.model.CommentarySpeed
import com.example.data.model.Difficulty
import com.example.data.model.MatchFormat
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.StadiumBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GameViewModel
) {
    val settings by viewModel.settings.collectAsState()

    var showResetHistoryDialog by remember { mutableStateOf(false) }
    var showResetStatsDialog by remember { mutableStateOf(false) }
    var showResetWcDialog by remember { mutableStateOf(false) }

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "GAME SETTINGS ⚙️",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = TextPrimary
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: AI Difficulty
                Text(
                    text = "🤖 CPU AI DIFFICULTY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CricketGold,
                        letterSpacing = 0.5.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Difficulty.entries.forEach { diff ->
                        val isSelected = (settings.difficulty == diff)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .testTag("difficulty_${diff.name.lowercase()}")
                                .clickable {
                                    viewModel.updateSettings(settings.copy(difficulty = diff))
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) StadiumCardNavy else ScoreboardBg
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = if (isSelected) Brush.verticalGradient(listOf(CricketPitchGreen, CricketGold))
                                else Brush.verticalGradient(listOf(StadiumCardBorder, Color.Transparent)),
                                width = if (isSelected) 2.dp else 1.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = diff.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) CricketPitchLightGreen else TextPrimary
                                    )
                                )
                                Text(
                                    text = when (diff) {
                                        Difficulty.EASY -> "Random"
                                        Difficulty.MEDIUM -> "Adaptive"
                                        Difficulty.HARD -> "Predictive AI"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Section 2: Audio & Haptic Feedback
                Text(
                    text = "🔊 AUDIO & VIBRATION",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CricketPitchLightGreen,
                        letterSpacing = 0.5.sp
                    )
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StadiumCardNavy)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Sound FX Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Sound Effects",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Bat hits, boundaries, wickets & crowd sounds",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }

                            Switch(
                                checked = settings.soundEffectsEnabled,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateSettings(settings.copy(soundEffectsEnabled = isChecked))
                                },
                                modifier = Modifier.testTag("sound_effects_switch"),
                                colors = SwitchDefaults.colors(checkedThumbColor = CricketPitchGreen)
                            )
                        }

                        Divider(color = StadiumCardBorder, thickness = 1.dp)

                        // Vibration Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Haptic Vibration",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Tactile feedback when tapping numbers & wickets",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }

                            Switch(
                                checked = settings.vibrationEnabled,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateSettings(settings.copy(vibrationEnabled = isChecked))
                                },
                                modifier = Modifier.testTag("vibration_switch"),
                                colors = SwitchDefaults.colors(checkedThumbColor = CricketPitchGreen)
                            )
                        }
                    }
                }

                // Section 3: Hindi Cricket Commentary
                Text(
                    text = "🎙️ HINDI COMMENTARY (हिंदी कमेंट्री)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CricketGold,
                        letterSpacing = 0.5.sp
                    )
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StadiumCardNavy)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Hindi Commentary Master Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Live Hindi Commentary",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Dynamic ball-by-ball Hindi commentary (Rahul & Neha)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }

                            Switch(
                                checked = settings.commentaryEnabled,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateSettings(settings.copy(commentaryEnabled = isChecked))
                                },
                                modifier = Modifier.testTag("commentary_master_switch"),
                                colors = SwitchDefaults.colors(checkedThumbColor = CricketPitchGreen)
                            )
                        }

                        if (settings.commentaryEnabled) {
                            HorizontalDivider(color = StadiumCardBorder, thickness = 1.dp)

                            // Commentary Voice (TTS) Audio Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Commentary Voice (ऑडियो आवाज़)",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Speaks commentary with male & female Hindi voices",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                    )
                                }

                                Switch(
                                    checked = settings.commentaryVoiceEnabled,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updateSettings(settings.copy(commentaryVoiceEnabled = isChecked))
                                    },
                                    modifier = Modifier.testTag("commentary_voice_switch"),
                                    colors = SwitchDefaults.colors(checkedThumbColor = CricketPitchGreen)
                                )
                            }

                            HorizontalDivider(color = StadiumCardBorder, thickness = 1.dp)

                            // Commentary Speed
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Commentary Speed (बोलने की गति)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CommentarySpeed.entries.forEach { speed ->
                                        val isSelected = (settings.commentarySpeed == speed)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                viewModel.updateSettings(settings.copy(commentarySpeed = speed))
                                            },
                                            label = {
                                                Text(
                                                    text = speed.label,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) CricketPitchLightGreen else TextSecondary
                                                    )
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = StadiumCardBorder.copy(alpha = 0.5f),
                                                containerColor = ScoreboardBg
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 4: Data Management & Resets
                Text(
                    text = "🗑️ DATA MANAGEMENT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CricketWicketRed,
                        letterSpacing = 0.5.sp
                    )
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showResetHistoryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reset_history_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CricketWicketRed)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Reset Match History")
                    }

                    OutlinedButton(
                        onClick = { showResetStatsDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reset_stats_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CricketWicketRed)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Reset Career Statistics")
                    }

                    OutlinedButton(
                        onClick = { showResetWcDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reset_wc_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CricketWicketRed)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Reset Active World Cup Tournament")
                    }
                }

                // Confirmation Dialogs
                if (showResetHistoryDialog) {
                    ConfirmationDialog(
                        title = "Clear Match History?",
                        message = "Are you sure you want to delete all past match scorecards and results? This action cannot be undone.",
                        confirmText = "Delete History",
                        isDestructive = true,
                        onConfirm = {
                            showResetHistoryDialog = false
                            viewModel.resetMatchHistory()
                        },
                        onDismiss = { showResetHistoryDialog = false }
                    )
                }

                if (showResetStatsDialog) {
                    ConfirmationDialog(
                        title = "Reset Career Statistics?",
                        message = "Are you sure you want to reset all your career runs, wickets, boundaries, win rates, and tournament records back to zero?",
                        confirmText = "Reset Statistics",
                        isDestructive = true,
                        onConfirm = {
                            showResetStatsDialog = false
                            viewModel.resetCareerStats()
                        },
                        onDismiss = { showResetStatsDialog = false }
                    )
                }

                if (showResetWcDialog) {
                    ConfirmationDialog(
                        title = "Reset World Cup?",
                        message = "Are you sure you want to delete the active World Cup campaign and standings?",
                        confirmText = "Reset World Cup",
                        isDestructive = true,
                        onConfirm = {
                            showResetWcDialog = false
                            viewModel.resetTournament()
                        },
                        onDismiss = { showResetWcDialog = false }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
