package com.example.ui.screens.worldcup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
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
import com.example.data.model.*
import com.example.ui.components.StadiumBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixturesScreen(
    viewModel: GameViewModel
) {
    val tournamentState by viewModel.tournamentState.collectAsState()
    var selectedRoundFilter by remember { mutableIntStateOf(0) } // 0 = All, 1..9 = Rounds, 10 = Knockouts

    val tournament = tournamentState ?: return
    val userTeamId = tournament.userTeamId

    val displayedFixtures = when (selectedRoundFilter) {
        0 -> tournament.fixtures
        10 -> tournament.fixtures.filter { it.stage != TournamentStage.LEAGUE }
        else -> tournament.fixtures.filter { it.round == selectedRoundFilter }
    }

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "TOURNAMENT FIXTURES",
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
                            modifier = Modifier.testTag("fixtures_back_button")
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
                // Batch Simulate Pending CPU Matches Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CricketRoyalBlue, CricketBoundaryPurple)),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CPU MATCH SIMULATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CricketGold
                                )
                            )
                            Text(
                                text = "Fast forward non-player matches",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.simulateAllUpcomingLeagueMatches() },
                            modifier = Modifier.testTag("simulate_all_cpu_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketRoyalBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "SIMULATE ALL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Round Filter Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedRoundFilter == 0,
                            onClick = { selectedRoundFilter = 0 },
                            label = { Text("All Matches (45+)") }
                        )
                    }
                    for (r in 1..9) {
                        item {
                            FilterChip(
                                selected = selectedRoundFilter == r,
                                onClick = { selectedRoundFilter = r },
                                label = { Text("Round $r") }
                            )
                        }
                    }
                    item {
                        FilterChip(
                            selected = selectedRoundFilter == 10,
                            onClick = { selectedRoundFilter = 10 },
                            label = { Text("Knockouts") }
                        )
                    }
                }

                // Fixtures List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedFixtures) { fixture ->
                        FixtureCard(
                            fixture = fixture,
                            userTeamId = userTeamId,
                            onPlayMatch = {
                                val playerTeam = if (fixture.team1Id.equals(userTeamId, ignoreCase = true)) fixture.team1 else fixture.team2
                                val opponentTeam = if (fixture.team1Id.equals(userTeamId, ignoreCase = true)) fixture.team2 else fixture.team1

                                val matchType = when (fixture.stage) {
                                    TournamentStage.LEAGUE -> MatchType.WORLD_CUP_LEAGUE
                                    TournamentStage.SEMI_FINALS -> MatchType.WORLD_CUP_SEMI_FINAL
                                    TournamentStage.FINAL -> MatchType.WORLD_CUP_FINAL
                                    else -> MatchType.WORLD_CUP_LEAGUE
                                }

                                viewModel.startMatch(
                                    matchType = matchType,
                                    fixtureId = fixture.id,
                                    playerTeam = playerTeam,
                                    opponentTeam = opponentTeam,
                                    format = fixture.format
                                )
                            },
                            onSimulateMatch = {
                                viewModel.simulateFixture(fixture.id)
                            }
                        )
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
private fun FixtureCard(
    fixture: Fixture,
    userTeamId: String,
    onPlayMatch: () -> Unit,
    onSimulateMatch: () -> Unit
) {
    val isUserMatch = fixture.involvesTeam(userTeamId)
    val isCompleted = (fixture.status == FixtureStatus.COMPLETED)
    val team1 = fixture.team1
    val team2 = fixture.team2

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fixture_card_${fixture.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUserMatch) StadiumCardNavy else ScoreboardBg
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isUserMatch) Brush.horizontalGradient(listOf(CricketPitchGreen, CricketGold))
            else Brush.horizontalGradient(listOf(StadiumCardBorder, Color.Transparent)),
            width = if (isUserMatch) 1.5.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Match Header Tag & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "MATCH ${fixture.matchNumber.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = CricketGold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "· ${fixture.format.displayName} (${fixture.format.wicketRuleDescription})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        isCompleted -> Color(0xFF1E293B)
                        isUserMatch -> CricketPitchGreen.copy(alpha = 0.2f)
                        else -> Color(0xFF1E1B4B)
                    }
                ) {
                    Text(
                        text = when {
                            isCompleted -> "COMPLETED"
                            isUserMatch -> "⭐ YOUR MATCH"
                            else -> "UPCOMING"
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCompleted -> TextSecondary
                                isUserMatch -> CricketPitchLightGreen
                                else -> Color(0xFF93C5FD)
                            },
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Teams Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = team1.flagEmoji, fontSize = 20.sp)
                    Text(
                        text = team1.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (fixture.winnerTeamId == team1.id) FontWeight.Black else FontWeight.Bold,
                            color = if (fixture.winnerTeamId == team1.id) CricketGold else TextPrimary
                        )
                    )
                    if (fixture.winnerTeamId == team1.id) {
                        Text(text = "👑", fontSize = 12.sp)
                    }
                }

                if (isCompleted) {
                    Text(
                        text = "${fixture.team1Runs ?: 0}/${fixture.team1Wickets ?: 0}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 2
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = team2.flagEmoji, fontSize = 20.sp)
                    Text(
                        text = team2.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (fixture.winnerTeamId == team2.id) FontWeight.Black else FontWeight.Bold,
                            color = if (fixture.winnerTeamId == team2.id) CricketGold else TextPrimary
                        )
                    )
                    if (fixture.winnerTeamId == team2.id) {
                        Text(text = "👑", fontSize = 12.sp)
                    }
                }

                if (isCompleted) {
                    Text(
                        text = "${fixture.team2Runs ?: 0}/${fixture.team2Wickets ?: 0}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            // Result Summary or Action Buttons
            if (isCompleted) {
                fixture.resultSummary?.let { summary ->
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CricketPitchLightGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isUserMatch) {
                        Button(
                            onClick = onPlayMatch,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("play_fixture_${fixture.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PLAY MATCH 🏏", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    } else {
                        Button(
                            onClick = onSimulateMatch,
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("simulate_fixture_${fixture.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketRoyalBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Simulate Match", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
