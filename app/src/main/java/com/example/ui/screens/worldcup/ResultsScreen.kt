package com.example.ui.screens.worldcup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Scoreboard
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
import com.example.data.model.Fixture
import com.example.data.model.FixtureStatus
import com.example.data.model.TournamentStage
import com.example.ui.components.StadiumBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: GameViewModel
) {
    val tournamentState by viewModel.tournamentState.collectAsState()
    val matchHistory by viewModel.matchHistory.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = League, 1 = Semi-Finals, 2 = Final

    val tournament = tournamentState ?: return

    val completedLeagueFixtures = tournament.fixtures.filter {
        it.stage == TournamentStage.LEAGUE && it.status == FixtureStatus.COMPLETED
    }
    val completedSfFixtures = tournament.fixtures.filter {
        it.stage == TournamentStage.SEMI_FINALS && it.status == FixtureStatus.COMPLETED
    }
    val completedFinalFixtures = tournament.fixtures.filter {
        it.stage == TournamentStage.FINAL && it.status == FixtureStatus.COMPLETED
    }

    val currentList = when (selectedTab) {
        0 -> completedLeagueFixtures
        1 -> completedSfFixtures
        2 -> completedFinalFixtures
        else -> completedLeagueFixtures
    }

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "WORLD CUP RESULTS",
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
                            modifier = Modifier.testTag("results_back_button")
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
                // Section Tabs: League, Semi-Finals, Final
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = StadiumCardNavy,
                    contentColor = CricketPitchLightGreen,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "League (${completedLeagueFixtures.size}/45)",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) CricketPitchLightGreen else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Semi-Finals (${completedSfFixtures.size}/2)",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) CricketPitchLightGreen else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "Final (${completedFinalFixtures.size}/1)",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 2) CricketPitchLightGreen else TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                if (currentList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "📋", fontSize = 44.sp)
                            Text(
                                text = "No completed matches in this stage yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(currentList) { fixture ->
                            val result = matchHistory.firstOrNull { it.matchId == fixture.id }

                            ResultCard(
                                fixture = fixture,
                                onOpenScorecard = {
                                    if (result != null) {
                                        viewModel.navigateTo(Screen.ScorecardScreen(result))
                                    }
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
}

@Composable
private fun ResultCard(
    fixture: Fixture,
    onOpenScorecard: () -> Unit
) {
    val team1 = fixture.team1
    val team2 = fixture.team2
    val team1Won = fixture.winnerTeamId == team1.id
    val team2Won = fixture.winnerTeamId == team2.id

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_card_${fixture.id}")
            .clickable(onClick = onOpenScorecard),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(StadiumCardBorder, CricketPitchGreen.copy(alpha = 0.3f))),
            width = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Match Number & Stage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MATCH #${fixture.matchNumber.toString().padStart(2, '0')} · ${fixture.stageMatchName ?: fixture.stage.name}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = CricketGold,
                        fontSize = 11.sp
                    )
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "${fixture.format.displayName} · ${fixture.format.wicketRuleDescription}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Team 1 Score Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = team1.flagEmoji, fontSize = 20.sp)
                    Text(
                        text = team1.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (team1Won) FontWeight.Black else FontWeight.Bold,
                            color = if (team1Won) CricketGold else TextPrimary
                        )
                    )
                    if (team1Won) Text(text = "👑", fontSize = 12.sp)
                }

                Text(
                    text = "${fixture.team1Runs ?: 0}/${fixture.team1Wickets ?: 0}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            // Team 2 Score Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = team2.flagEmoji, fontSize = 20.sp)
                    Text(
                        text = team2.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (team2Won) FontWeight.Black else FontWeight.Bold,
                            color = if (team2Won) CricketGold else TextPrimary
                        )
                    )
                    if (team2Won) Text(text = "👑", fontSize = 12.sp)
                }

                Text(
                    text = "${fixture.team2Runs ?: 0}/${fixture.team2Wickets ?: 0}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            // Result summary & View Scorecard button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fixture.resultSummary ?: "Completed",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CricketPitchLightGreen,
                        fontSize = 11.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Scorecard",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CricketRoyalBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = CricketRoyalBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
