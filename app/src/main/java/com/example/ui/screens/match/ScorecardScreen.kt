package com.example.ui.screens.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.InningsState
import com.example.data.model.MatchResult
import com.example.ui.components.BallChip
import com.example.ui.components.StadiumBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorecardScreen(
    result: MatchResult,
    viewModel: GameViewModel
) {
    var selectedInningsTab by remember { mutableIntStateOf(1) }

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "OFFICIAL SCORECARD",
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
                            modifier = Modifier.testTag("scorecard_back_button")
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Match Header Result summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CricketGold, CricketPitchGreen)),
                        width = 1.5.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = result.marginText,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CricketGold
                            )
                        )
                        Text(
                            text = "${result.format.displayName} · Wicket Limit: ${result.format.maxWickets} ${if (result.format.maxWickets == 1) "Wicket" else "Wickets"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Innings Switcher Tabs
                TabRow(
                    selectedTabIndex = selectedInningsTab - 1,
                    containerColor = StadiumCardNavy,
                    contentColor = CricketPitchLightGreen,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedInningsTab == 1,
                        onClick = { selectedInningsTab = 1 },
                        text = {
                            Text(
                                text = "1ST INN: ${result.innings1.battingTeam.shortName} (${result.innings1.runs}/${result.innings1.wickets})",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedInningsTab == 1) CricketPitchLightGreen else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedInningsTab == 2,
                        onClick = { selectedInningsTab = 2 },
                        text = {
                            Text(
                                text = "2ND INN: ${result.innings2.battingTeam.shortName} (${result.innings2.runs}/${result.innings2.wickets})",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedInningsTab == 2) CricketPitchLightGreen else TextSecondary
                            )
                        }
                    )
                }

                val currentInnings = if (selectedInningsTab == 1) result.innings1 else result.innings2

                // Detailed Innings Breakdown
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Innings Totals Card
                    item {
                        InningsSummaryCard(innings = currentInnings)
                    }

                    // Run Distribution Card
                    item {
                        RunDistributionCard(innings = currentInnings)
                    }

                    // Ball by Ball Deliveries Header
                    item {
                        Text(
                            text = "BALL-BY-BALL DELIVERIES (${currentInnings.ballEvents.size} balls)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CricketGold,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Ball events list
                    items(currentInnings.ballEvents) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(
                                    if (event.isWicket) listOf(CricketWicketRed, Color(0xFF7F1D1D))
                                    else listOf(StadiumCardBorder, Color.Transparent)
                                ),
                                width = 1.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                BallChip(event = event)

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Over ${event.overNumber}.${event.ballInOver} · Batsman: ${event.batsmanChoice} vs Bowler: ${event.bowlerChoice}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                    Text(
                                        text = event.commentary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (event.isWicket) CricketWicketRed else TextPrimary,
                                            fontWeight = if (event.isWicket || event.runsScored >= 4) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }

                                Text(
                                    text = "${event.totalRunsAfter}/${event.totalWicketsAfter}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InningsSummaryCard(innings: InningsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(StadiumCardBorder, CricketPitchGreen.copy(alpha = 0.4f))),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = innings.battingTeam.flagEmoji, fontSize = 28.sp)
                    Column {
                        Text(
                            text = innings.battingTeam.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "vs ${innings.bowlingTeam.name}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${innings.runs}/${innings.wickets}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${innings.oversFormatted}/${innings.maxOvers}.0 Overs",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(StadiumCardNavy)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Run Rate: ${String.format("%.2f", innings.runRate)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "Wickets Used: ${innings.wickets}/${innings.maxWickets}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (innings.wickets >= innings.maxWickets) CricketWicketRed else CricketPitchLightGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun RunDistributionCard(innings: InningsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumCardNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "RUN SCORING DISTRIBUTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatPill(label = "1s", count = innings.countOnes, color = ChipOneBg)
                StatPill(label = "2s", count = innings.countTwos, color = CricketRoyalBlue)
                StatPill(label = "3s", count = innings.countThrees, color = Color(0xFF6366F1))
                StatPill(label = "4s", count = innings.countFours, color = ChipFourBg)
                StatPill(label = "5s", count = innings.countFives, color = CricketGold)
                StatPill(label = "6s", count = innings.countSixes, color = ChipSixBg)
            }
        }
    }
}

@Composable
private fun StatPill(label: String, count: Int, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(color, color.copy(alpha = 0.5f))),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            )
        }
    }
}
