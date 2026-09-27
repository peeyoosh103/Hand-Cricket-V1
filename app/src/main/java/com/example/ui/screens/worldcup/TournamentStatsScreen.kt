package com.example.ui.screens.worldcup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.FixtureStatus
import com.example.ui.components.StadiumBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentStatsScreen(
    viewModel: GameViewModel
) {
    val tournamentState by viewModel.tournamentState.collectAsState()
    val tournament = tournamentState ?: return

    val completedFixtures = tournament.fixtures.filter { it.status == FixtureStatus.COMPLETED }
    val pointsTable = tournament.pointsTable

    // Calculate aggregated team and match statistics
    val highestRunTeam = pointsTable.maxByOrNull { it.runsScored }
    val highestMatchScoreFixture = completedFixtures.maxByOrNull { maxOf(it.team1Runs ?: 0, it.team2Runs ?: 0) }
    val highestMatchScore = highestMatchScoreFixture?.let { maxOf(it.team1Runs ?: 0, it.team2Runs ?: 0) } ?: 0

    val mostWinsTeam = pointsTable.maxByOrNull { it.won }
    val bestNrrTeam = pointsTable.maxByOrNull { it.nrr }

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "TOURNAMENT STATISTICS 📊",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = CricketGold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("tournament_stats_back_button")
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
                // Section 1: Team Leaderboard Stats
                Text(
                    text = "🏆 TEAM TOURNAMENT LEADERBOARD",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CricketGold,
                        letterSpacing = 0.5.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "MOST RUNS SCORED",
                        value = "${highestRunTeam?.runsScored ?: 0} Runs",
                        subtitle = "${highestRunTeam?.team?.name ?: "N/A"} (${highestRunTeam?.team?.flagEmoji ?: ""})",
                        accentColor = CricketPitchLightGreen,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "MOST WINS",
                        value = "${mostWinsTeam?.won ?: 0} Wins",
                        subtitle = "${mostWinsTeam?.team?.name ?: "N/A"} (${mostWinsTeam?.team?.flagEmoji ?: ""})",
                        accentColor = CricketGold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "HIGHEST MATCH INNINGS",
                        value = "$highestMatchScore Runs",
                        subtitle = "Match #${highestMatchScoreFixture?.matchNumber ?: 0}",
                        accentColor = CricketBoundaryPurple,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "BEST NET RUN RATE",
                        value = if ((bestNrrTeam?.nrr ?: 0.0) >= 0) "+${String.format("%.3f", bestNrrTeam?.nrr ?: 0.0)}" else String.format("%.3f", bestNrrTeam?.nrr ?: 0.0),
                        subtitle = "${bestNrrTeam?.team?.name ?: "N/A"} (${bestNrrTeam?.team?.flagEmoji ?: ""})",
                        accentColor = Color(0xFF60A5FA),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Section 2: Tournament Standings Quick View
                Text(
                    text = "📋 AGGREGATE RUN TALLIES",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pointsTable.sortedByDescending { it.runsScored }.forEachIndexed { index, row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(text = row.team.flagEmoji, fontSize = 16.sp)
                                    Text(
                                        text = row.team.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                }

                                Text(
                                    text = "${row.runsScored} runs in ${row.played} matches",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = CricketPitchLightGreen
                                    )
                                )
                            }
                            if (index < pointsTable.size - 1) {
                                Divider(color = Color(0xFF1E293B), thickness = 0.8.dp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(listOf(StadiumCardBorder, accentColor.copy(alpha = 0.4f))),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    fontSize = 18.sp
                )
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
