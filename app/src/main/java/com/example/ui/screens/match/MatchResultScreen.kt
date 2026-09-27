package com.example.ui.screens.match

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.model.MatchResult
import com.example.ui.components.StadiumBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchResultScreen(
    result: MatchResult,
    fromTournament: Boolean,
    viewModel: GameViewModel
) {
    BackHandler {
        if (fromTournament) {
            viewModel.navigateTo(Screen.WorldCupHub)
        } else {
            viewModel.navigateToHome()
        }
    }

    val winner = result.winner
    val isTie = result.isTie
    val innings1 = result.innings1
    val innings2 = result.innings2

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "MATCH RESULT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = CricketGold
                            )
                        )
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
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Winner Banner Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_winner_banner"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            if (isTie) listOf(CricketBoundaryPurple, CricketRoyalBlue)
                            else listOf(CricketGold, CricketPitchGreen)
                        ),
                        width = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isTie) "🤝" else "🏆",
                            fontSize = 42.sp
                        )

                        Text(
                            text = if (isTie) "MATCH TIED!" else "${winner?.name?.uppercase()} WON!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = if (isTie) CricketGoldLight else CricketGold,
                                letterSpacing = 1.sp
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StadiumCardNavy
                        ) {
                            Text(
                                text = result.marginText,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CricketPitchLightGreen
                                )
                            )
                        }
                    }
                }

                // Scores Comparison Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(StadiumCardBorder, Color.Transparent)),
                        width = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "MATCH SUMMARY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )

                        // Innings 1 Team Row
                        InningsResultRow(
                            inningsNumber = 1,
                            team = innings1.battingTeam,
                            runs = innings1.runs,
                            wickets = innings1.wickets,
                            maxWickets = innings1.maxWickets,
                            oversFormatted = innings1.oversFormatted,
                            maxOvers = innings1.maxOvers,
                            isWinner = winner?.id == innings1.battingTeam.id
                        )

                        Divider(color = StadiumCardBorder, thickness = 1.dp)

                        // Innings 2 Team Row
                        InningsResultRow(
                            inningsNumber = 2,
                            team = innings2.battingTeam,
                            runs = innings2.runs,
                            wickets = innings2.wickets,
                            maxWickets = innings2.maxWickets,
                            oversFormatted = innings2.oversFormatted,
                            maxOvers = innings2.maxOvers,
                            isWinner = winner?.id == innings2.battingTeam.id
                        )
                    }
                }

                // Player of the Match Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CricketGold, CricketPitchGreen)),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "⭐", fontSize = 28.sp)
                        Column {
                            Text(
                                text = "PLAYER OF THE MATCH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CricketGold
                                )
                            )
                            Text(
                                text = result.playerOfTheMatch,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                }

                // Action Buttons: Rematch, Scorecard, Home/Tournament
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // View Scorecard Button
                    Button(
                        onClick = { viewModel.navigateTo(Screen.ScorecardScreen(result)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("result_view_scorecard_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketRoyalBlue)
                    ) {
                        Icon(imageVector = Icons.Default.Scoreboard, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "VIEW FULL SCORECARD", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    if (!fromTournament) {
                        // Rematch Button
                        OutlinedButton(
                            onClick = {
                                viewModel.startMatch(
                                    matchType = result.matchType,
                                    playerTeam = result.team1,
                                    opponentTeam = result.team2,
                                    format = result.format
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("result_rematch_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CricketPitchLightGreen),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(CricketPitchGreen, CricketPitchLightGreen)),
                                width = 1.5.dp
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "PLAY REMATCH", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Back to Home or Tournament Hub Button
                    Button(
                        onClick = {
                            if (fromTournament) {
                                viewModel.navigateTo(Screen.WorldCupHub)
                            } else {
                                viewModel.navigateToHome()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("result_back_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (fromTournament) "CONTINUE WORLD CUP" else "BACK TO HOME",
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

@Composable
private fun InningsResultRow(
    inningsNumber: Int,
    team: com.example.data.model.Team,
    runs: Int,
    wickets: Int,
    maxWickets: Int,
    oversFormatted: String,
    maxOvers: Int,
    isWinner: Boolean
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
            Text(text = team.flagEmoji, fontSize = 24.sp)
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isWinner) FontWeight.Black else FontWeight.Bold,
                            color = if (isWinner) CricketGold else TextPrimary
                        )
                    )
                    if (isWinner) {
                        Text(text = "👑", fontSize = 14.sp)
                    }
                }
                Text(
                    text = "Innings $inningsNumber",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$runs/$wickets",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            )
            Text(
                text = "($oversFormatted/$maxOvers.0 ov) · Wkts: $wickets/$maxWickets",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            )
        }
    }
}
