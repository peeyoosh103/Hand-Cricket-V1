package com.example.ui.screens.worldcup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.StadiumBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldCupHubScreen(
    viewModel: GameViewModel
) {
    BackHandler {
        viewModel.navigateToHome()
    }

    val tournamentState by viewModel.tournamentState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    val tournament = tournamentState

    if (tournament == null) {
        viewModel.navigateTo(Screen.WorldCupSetup)
        return
    }

    val userTeam = tournament.userTeam
    val userStanding = tournament.pointsTable.indexOfFirst { it.teamId == userTeam.id } + 1
    val nextFixture = tournament.nextUserFixture

    LaunchedEffect(Unit) {
        viewModel.checkAndAutoProgressTournament()
    }

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "WORLD CUP 🏆",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = CricketGold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateToHome() },
                            modifier = Modifier.testTag("wc_hub_home_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Home",
                                tint = TextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.testTag("wc_hub_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Reset Tournament",
                                tint = CricketWicketRed
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
                // Tournament Status & User Team Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wc_status_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CricketGold, CricketPitchGreen)),
                        width = 1.5.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.linearGradient(
                                                listOf(userTeam.primaryColor, userTeam.secondaryColor)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = userTeam.flagEmoji, fontSize = 26.sp)
                                }

                                Column {
                                    Text(
                                        text = "${userTeam.name.uppercase()} (CAPTAIN)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Position: #$userStanding in Points Table ${if (userStanding <= 4) "🟢 Qualify Zone" else "🔴 In Danger"}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (userStanding <= 4) CricketPitchLightGreen else CricketWicketRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StadiumCardNavy
                            ) {
                                Text(
                                    text = "${tournament.format.displayName} · ${tournament.format.wicketRuleDescription}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CricketGold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Progress bar (45 matches)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TOURNAMENT PROGRESS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "${tournament.completedFixturesCount} / ${tournament.fixtures.size} Matches (${tournament.stage.name})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CricketPitchLightGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            LinearProgressIndicator(
                                progress = {
                                    if (tournament.fixtures.isEmpty()) 0f
                                    else tournament.completedFixturesCount.toFloat() / tournament.fixtures.size.toFloat()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CricketPitchGreen,
                                trackColor = Color(0xFF1E293B)
                            )
                        }
                    }
                }

                // Next User Match Banner (Call to Action)
                if (nextFixture != null) {
                    val opponent = nextFixture.getOpponentOf(userTeam.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wc_next_match_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(CricketPitchGreen, CricketRoyalBlue)),
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
                            Text(
                                text = "⚔️ NEXT SCHEDULED MATCH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CricketGold,
                                    letterSpacing = 1.sp
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = userTeam.flagEmoji, fontSize = 28.sp)
                                    Text(
                                        text = userTeam.name,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                }

                                Text(
                                    text = "VS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = CricketGold
                                    )
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = opponent.flagEmoji, fontSize = 28.sp)
                                    Text(
                                        text = opponent.name,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val matchType = when (nextFixture.stage) {
                                        TournamentStage.LEAGUE -> MatchType.WORLD_CUP_LEAGUE
                                        TournamentStage.SEMI_FINALS -> MatchType.WORLD_CUP_SEMI_FINAL
                                        TournamentStage.FINAL -> MatchType.WORLD_CUP_FINAL
                                        else -> MatchType.WORLD_CUP_LEAGUE
                                    }
                                    viewModel.startMatch(
                                        matchType = matchType,
                                        fixtureId = nextFixture.id,
                                        playerTeam = userTeam,
                                        opponentTeam = opponent,
                                        format = tournament.format
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("play_next_wc_match_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen)
                            ) {
                                Text(
                                    text = "PLAY MATCH NOW 🏏",
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // If Tournament has Champion crowned
                if (tournament.stage == TournamentStage.CHAMPION && tournament.championTeam != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(Screen.WorldCupChampion) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(CricketGold, CricketGoldLight)),
                            width = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "👑", fontSize = 32.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "WORLD CUP CHAMPION CROWNED!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = CricketGold
                                    )
                                )
                                Text(
                                    text = "${tournament.championTeam?.name} won the World Cup! Tap to view ceremony.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }
                    }
                }

                // 6 World Cup Main Sections Grid
                Text(
                    text = "TOURNAMENT SECTIONS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CricketGold,
                        letterSpacing = 0.5.sp
                    )
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Fixtures
                        WcHubCard(
                            title = "Fixtures",
                            subtitle = "45 League Matches",
                            icon = "📅",
                            modifier = Modifier.weight(1f),
                            testTag = "wc_fixtures_card",
                            onClick = { viewModel.navigateTo(Screen.WorldCupFixtures) }
                        )

                        // 2. Points Table / Leaderboard
                        WcHubCard(
                            title = "Points Table",
                            subtitle = "Standings & NRR",
                            icon = "📋",
                            modifier = Modifier.weight(1f),
                            testTag = "wc_points_table_card",
                            onClick = { viewModel.navigateTo(Screen.WorldCupPointsTable) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 3. Knockout Bracket
                        WcHubCard(
                            title = "Knockout Bracket",
                            subtitle = "Semi-Finals & Final",
                            icon = "🏆",
                            modifier = Modifier.weight(1f),
                            testTag = "wc_knockout_card",
                            onClick = { viewModel.navigateTo(Screen.WorldCupKnockout) }
                        )

                        // 4. Results
                        WcHubCard(
                            title = "Results",
                            subtitle = "Completed Match Cards",
                            icon = "📜",
                            modifier = Modifier.weight(1f),
                            testTag = "wc_results_card",
                            onClick = { viewModel.navigateTo(Screen.WorldCupResults) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 5. Teams
                        WcHubCard(
                            title = "Teams",
                            subtitle = "10 Nations Profile",
                            icon = "🌍",
                            modifier = Modifier.weight(1f),
                            testTag = "wc_teams_card",
                            onClick = { viewModel.navigateTo(Screen.WorldCupTeams) }
                        )

                        // 6. Tournament Stats
                        WcHubCard(
                            title = "Tournament Stats",
                            subtitle = "Runs, Wickets & Records",
                            icon = "📊",
                            modifier = Modifier.weight(1f),
                            testTag = "wc_stats_card",
                            onClick = { viewModel.navigateTo(Screen.WorldCupTournamentStats) }
                        )
                    }
                }

                // Reset Tournament Confirmation Dialog
                if (showResetDialog) {
                    ConfirmationDialog(
                        title = "Reset World Cup?",
                        message = "Are you sure you want to delete and reset the entire World Cup tournament progress? This cannot be undone.",
                        confirmText = "Reset Tournament",
                        isDestructive = true,
                        onConfirm = {
                            showResetDialog = false
                            viewModel.resetTournament()
                        },
                        onDismiss = {
                            showResetDialog = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun WcHubCard(
    title: String,
    subtitle: String,
    icon: String,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(94.dp)
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(StadiumCardBorder, Color.Transparent)),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 24.sp)
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
