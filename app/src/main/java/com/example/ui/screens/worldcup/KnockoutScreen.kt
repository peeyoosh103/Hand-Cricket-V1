package com.example.ui.screens.worldcup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.StadiumBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnockoutScreen(
    viewModel: GameViewModel
) {
    val tournamentState by viewModel.tournamentState.collectAsState()
    val tournament = tournamentState ?: return

    val sf1 = tournament.fixtures.firstOrNull { it.id == tournament.semiFinal1FixtureId }
    val sf2 = tournament.fixtures.firstOrNull { it.id == tournament.semiFinal2FixtureId }
    val finalFixture = tournament.fixtures.firstOrNull { it.id == tournament.finalFixtureId }
    val userTeamId = tournament.userTeamId

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "KNOCKOUT BRACKET 🏆",
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
                            modifier = Modifier.testTag("knockout_back_button")
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
                // Bracket Header
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
                            text = "ROAD TO THE WORLD CUP GLORY",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = CricketGold
                            )
                        )
                        Text(
                            text = if (tournament.isLeagueFinished) "Top 4 teams qualified for the Semi-Finals" else "Complete all 45 league matches to unlock knockouts",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Semi-Final 1 Card
                KnockoutMatchCard(
                    title = "SEMI-FINAL 1 (1st vs 4th)",
                    fixture = sf1,
                    userTeamId = userTeamId,
                    onPlay = { fixture ->
                        launchKnockoutMatch(viewModel, fixture, userTeamId)
                    },
                    onSimulate = { fixture ->
                        viewModel.simulateFixture(fixture.id)
                    }
                )

                // Semi-Final 2 Card
                KnockoutMatchCard(
                    title = "SEMI-FINAL 2 (2nd vs 3rd)",
                    fixture = sf2,
                    userTeamId = userTeamId,
                    onPlay = { fixture ->
                        launchKnockoutMatch(viewModel, fixture, userTeamId)
                    },
                    onSimulate = { fixture ->
                        viewModel.simulateFixture(fixture.id)
                    }
                )

                // Grand Final Card
                KnockoutMatchCard(
                    title = "🏆 WORLD CUP GRAND FINAL",
                    fixture = finalFixture,
                    userTeamId = userTeamId,
                    isFinal = true,
                    onPlay = { fixture ->
                        launchKnockoutMatch(viewModel, fixture, userTeamId)
                    },
                    onSimulate = { fixture ->
                        viewModel.simulateFixture(fixture.id)
                    }
                )

                if (tournament.stage == TournamentStage.CHAMPION && tournament.championTeam != null) {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.WorldCupChampion) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("view_celebration_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketGold)
                    ) {
                        Text(
                            text = "VIEW CHAMPIONSHIP CEREMONY 👑",
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

private fun launchKnockoutMatch(viewModel: GameViewModel, fixture: Fixture, userTeamId: String) {
    val isUserTeam1 = fixture.team1Id.equals(userTeamId, ignoreCase = true)
    val playerTeam = if (isUserTeam1) fixture.team1 else fixture.team2
    val opponentTeam = if (isUserTeam1) fixture.team2 else fixture.team1

    val matchType = if (fixture.stage == TournamentStage.FINAL) MatchType.WORLD_CUP_FINAL else MatchType.WORLD_CUP_SEMI_FINAL

    viewModel.startMatch(
        matchType = matchType,
        fixtureId = fixture.id,
        playerTeam = playerTeam,
        opponentTeam = opponentTeam,
        format = fixture.format
    )
}

@Composable
private fun KnockoutMatchCard(
    title: String,
    fixture: Fixture?,
    userTeamId: String,
    isFinal: Boolean = false,
    onPlay: (Fixture) -> Unit,
    onSimulate: (Fixture) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(if (isFinal) "knockout_final_card" else "knockout_sf_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (isFinal) ScoreboardBg else StadiumCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = if (isFinal) Brush.horizontalGradient(listOf(CricketGold, CricketGoldLight))
            else Brush.horizontalGradient(listOf(StadiumCardBorder, CricketRoyalBlue)),
            width = if (isFinal) 2.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isFinal) CricketGold else CricketPitchLightGreen,
                    letterSpacing = 0.5.sp
                )
            )

            if (fixture == null) {
                Text(
                    text = "Awaiting preceding match results...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                )
            } else {
                val team1 = fixture.team1
                val team2 = fixture.team2
                val isCompleted = (fixture.status == FixtureStatus.COMPLETED)
                val isUserMatch = fixture.involvesTeam(userTeamId)

                // Matchup
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
                        Text(text = team1.flagEmoji, fontSize = 22.sp)
                        Text(
                            text = team1.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (fixture.winnerTeamId == team1.id) FontWeight.Black else FontWeight.Bold,
                                color = if (fixture.winnerTeamId == team1.id) CricketGold else TextPrimary
                            )
                        )
                        if (fixture.winnerTeamId == team1.id) {
                            Text(text = "👑", fontSize = 14.sp)
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
                        Text(text = team2.flagEmoji, fontSize = 22.sp)
                        Text(
                            text = team2.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (fixture.winnerTeamId == team2.id) FontWeight.Black else FontWeight.Bold,
                                color = if (fixture.winnerTeamId == team2.id) CricketGold else TextPrimary
                            )
                        )
                        if (fixture.winnerTeamId == team2.id) {
                            Text(text = "👑", fontSize = 14.sp)
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

                if (isCompleted) {
                    fixture.resultSummary?.let { summary ->
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CricketPitchLightGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    if (isUserMatch) {
                        Button(
                            onClick = { onPlay(fixture) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("play_knockout_${fixture.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PLAY KNOCKOUT MATCH 🏏", fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    } else {
                        Button(
                            onClick = { onSimulate(fixture) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("simulate_knockout_${fixture.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketRoyalBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Simulate Match", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
