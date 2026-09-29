package com.example.ui.screens.match

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.model.MatchFormat
import com.example.data.model.MatchStage
import com.example.data.model.MatchType
import com.example.data.model.Team
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameplayScreen(
    matchType: MatchType,
    fixtureId: String?,
    playerTeam: Team,
    opponentTeam: Team,
    format: MatchFormat,
    viewModel: GameViewModel
) {
    val activeMatch by viewModel.activeMatch.collectAsState()
    val settings by viewModel.settings.collectAsState()
    var showQuitDialog by remember { mutableStateOf(false) }

    // Intercept back button to show quit confirmation
    BackHandler {
        showQuitDialog = true
    }

    val match = activeMatch ?: return

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = when (matchType) {
                                    MatchType.WORLD_CUP_LEAGUE -> "WORLD CUP LEAGUE"
                                    MatchType.WORLD_CUP_SEMI_FINAL -> "WORLD CUP SEMI-FINAL"
                                    MatchType.WORLD_CUP_FINAL -> "WORLD CUP FINAL"
                                    MatchType.PRACTICE -> "PRACTICE MATCH"
                                    else -> "QUICK MATCH"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = CricketGold
                                )
                            )
                        }
                    },
                    actions = {
                        // Quick Voice Toggle Button
                        IconButton(
                            onClick = {
                                viewModel.setCommentaryVoiceEnabled(!settings.commentaryVoiceEnabled)
                            },
                            modifier = Modifier.testTag("gameplay_voice_toggle")
                        ) {
                            Icon(
                                imageVector = if (settings.commentaryVoiceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = if (settings.commentaryVoiceEnabled) "Mute Voice" else "Unmute Voice",
                                tint = if (settings.commentaryVoiceEnabled) CricketPitchLightGreen else TextSecondary
                            )
                        }
                        IconButton(
                            onClick = { showQuitDialog = true },
                            modifier = Modifier.testTag("gameplay_pause_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pause,
                                contentDescription = "Pause",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Main Match Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val currentInnings = match.currentInnings

                    if (currentInnings != null) {
                        val isSuperOver = match.stage == MatchStage.SUPER_OVER_INNINGS_1 || match.stage == MatchStage.SUPER_OVER_INNINGS_2
                        // 1. Live Professional Scoreboard
                        CricketScoreboard(
                            format = if (isSuperOver) MatchFormat.ONE_OVER else match.format,
                            currentInnings = currentInnings,
                            isUserBatting = match.isUserBatting,
                            playerTeam = playerTeam,
                            opponentTeam = opponentTeam,
                            isSuperOver = isSuperOver
                        )

                        // 2. Hand Number Clash Visual Zone (Batsman vs Bowler Clash)
                        HandDeliveryClashCard(
                            match = match,
                            isUserBatting = match.isUserBatting,
                            playerTeam = playerTeam,
                            opponentTeam = opponentTeam
                        )

                        // 3. Ball History Row
                        BallHistoryRow(
                            ballEvents = currentInnings.ballEvents
                        )

                        // 4. Large 1-6 Hand Controls Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.verticalGradient(
                                    listOf(StadiumCardBorder, CricketPitchGreen.copy(alpha = 0.5f))
                                ),
                                width = 1.2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = if (match.isUserBatting) "CHOOSE YOUR BATTING NUMBER 👇" else "CHOOSE YOUR BOWLING NUMBER 👇",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (match.isUserBatting) CricketPitchLightGreen else Color(0xFF93C5FD),
                                        letterSpacing = 0.5.sp
                                    )
                                )

                                HandNumberButtons(
                                    onNumberSelected = { number ->
                                        viewModel.onPlayerSelectNumber(number)
                                    },
                                    isEnabled = !currentInnings.isCompleted && !match.isBallProcessing,
                                    isBatting = match.isUserBatting
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Toss Dialog Overlay
                if (match.stage == MatchStage.TOSS || match.stage == MatchStage.SUPER_OVER_TOSS) {
                    TossDialog(
                        playerTeam = playerTeam,
                        opponentTeam = opponentTeam,
                        onCallHeadsOrTails = { isHeads ->
                            viewModel.executeToss(isHeads)
                        },
                        tossWinner = match.tossWinner,
                        isPlayerTossWinner = match.isPlayerTossWinner,
                        onPlayerDecision = { choice ->
                            match.tossWinner?.let { winner ->
                                viewModel.applyTossDecision(winner, choice, match.isPlayerTossWinner)
                            }
                        }
                    )
                }

                // Innings Break Modal Overlay
                if (match.stage == MatchStage.INNINGS_BREAK && match.innings1 != null) {
                    InningsBreakModal(
                        format = match.format,
                        firstInnings = match.innings1,
                        onStartSecondInnings = {
                            viewModel.startSecondInnings()
                        }
                    )
                }

                // Super Over Innings Break Modal
                if (match.stage == MatchStage.SUPER_OVER_BREAK && match.superOverInnings1 != null) {
                    InningsBreakModal(
                        format = MatchFormat.ONE_OVER,
                        firstInnings = match.superOverInnings1,
                        onStartSecondInnings = {
                            viewModel.startSuperOverSecondInnings()
                        }
                    )
                }

                // Quit Confirmation Dialog
                if (showQuitDialog) {
                    ConfirmationDialog(
                        title = "Leave Match?",
                        message = "Are you sure you want to quit this match? Current progress will be lost.",
                        confirmText = "Quit Match",
                        dismissText = "Resume Playing",
                        isDestructive = true,
                        onConfirm = {
                            showQuitDialog = false
                            viewModel.navigateBack()
                        },
                        onDismiss = {
                            showQuitDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HandDeliveryClashCard(
    match: com.example.ui.viewmodel.ActiveMatchState,
    isUserBatting: Boolean,
    playerTeam: Team,
    opponentTeam: Team
) {
    val batsmanTeam = if (isUserBatting) playerTeam else opponentTeam
    val bowlerTeam = if (isUserBatting) opponentTeam else playerTeam

    val batsmanChoice = if (isUserBatting) match.lastPlayerChoice else match.lastCpuChoice
    val bowlerChoice = if (isUserBatting) match.lastCpuChoice else match.lastPlayerChoice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("delivery_clash_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                if (match.lastBallWicket) listOf(CricketWicketRed, Color(0xFF991B1B))
                else if (match.lastRunsScored == 6) listOf(CricketGold, CricketBoundaryPurple)
                else listOf(StadiumCardBorder, CricketPitchGreen)
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Batsman vs Bowler Hand Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Batsman Hand
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isUserBatting) "YOU (BATSMAN)" else "${opponentTeam.shortName} (BATSMAN)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CricketPitchLightGreen,
                            fontSize = 10.sp
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFF065F46), Color(0xFF047857))
                                )
                            )
                            .border(2.dp, CricketPitchLightGreen, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = batsmanChoice?.toString() ?: "🏏",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = if (batsmanChoice != null) 36.sp else 28.sp
                            )
                        )
                    }
                }

                // VS / Outcome Icon
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "VS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextSecondary
                        )
                    )
                }

                // Bowler Hand
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (!isUserBatting) "YOU (BOWLER)" else "${opponentTeam.shortName} (BOWLER)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF93C5FD),
                            fontSize = 10.sp
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFF1E3A8A), Color(0xFF1E40AF))
                                )
                            )
                            .border(2.dp, Color(0xFF60A5FA), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bowlerChoice?.toString() ?: "⚾",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = if (bowlerChoice != null) 36.sp else 28.sp
                            )
                        )
                    }
                }
            }

            // Outcome Announcement Banner
            if (batsmanChoice != null && bowlerChoice != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        match.lastBallWicket -> CricketWicketRed
                        match.lastRunsScored == 6 -> CricketGold
                        match.lastRunsScored == 4 -> CricketBoundaryPurple
                        else -> CricketPitchGreen
                    }
                ) {
                    Text(
                        text = when {
                            match.lastBallWicket -> "🚨 OUT! BOTH PLAYED $batsmanChoice!"
                            match.lastRunsScored == 6 -> "🔥 SIX! 6 RUNS ADDED!"
                            match.lastRunsScored == 4 -> "⚡ FOUR! 4 RUNS ADDED!"
                            match.lastRunsScored == 1 -> "1 RUN SCORED"
                            else -> "${match.lastRunsScored} RUNS SCORED"
                        },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (match.lastBallWicket || match.lastRunsScored == 4) Color.White else Color.Black,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            } else {
                Text(
                    text = "Pick a number 1 to 6 to bowl/bat!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary
                    )
                )
            }
        }
    }
}
