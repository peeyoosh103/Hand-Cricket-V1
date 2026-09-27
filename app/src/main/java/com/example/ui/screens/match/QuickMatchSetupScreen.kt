package com.example.ui.screens.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.data.model.MatchFormat
import com.example.data.model.MatchType
import com.example.data.model.Team
import com.example.ui.components.StadiumBackground
import com.example.ui.components.TeamBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMatchSetupScreen(
    viewModel: GameViewModel
) {
    var selectedFormat by remember { mutableStateOf(MatchFormat.FIVE_OVERS) }
    var selectedPlayerTeam by remember { mutableStateOf(Team.ALL_TEAMS[0]) } // India default
    var selectedOpponentTeam by remember { mutableStateOf(Team.ALL_TEAMS[1]) } // Australia default

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "QUICK MATCH SETUP",
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
                            modifier = Modifier.testTag("quick_match_back_button")
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
            containerColor = Color.Transparent,
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = StadiumDeepNavy,
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(listOf(StadiumCardBorder, Color.Transparent)),
                        width = 1.dp
                    )
                ) {
                    Button(
                        onClick = {
                            viewModel.startMatch(
                                matchType = MatchType.QUICK_MATCH,
                                playerTeam = selectedPlayerTeam,
                                opponentTeam = selectedOpponentTeam,
                                format = selectedFormat
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp)
                            .testTag("start_quick_match_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen)
                    ) {
                        Text(
                            text = "START MATCH 🏏",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: Match Format & Strict Wicket Limits Banner
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. SELECT MATCH FORMAT & WICKET RULE",
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
                        MatchFormat.entries.forEach { format ->
                            val isSelected = (format == selectedFormat)
                            FormatCard(
                                format = format,
                                isSelected = isSelected,
                                onClick = { selectedFormat = format },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Strict Wicket Rule Explanation Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(CricketGold, CricketPitchGreen)),
                            width = 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "ℹ️", fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "Wicket Rule for ${selectedFormat.displayName}: ${selectedFormat.maxWickets} ${if (selectedFormat.maxWickets == 1) "Wicket" else "Wickets"} per Innings",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CricketGoldLight
                                    )
                                )
                                Text(
                                    text = "Innings ends immediately when either ${selectedFormat.overs}.0 overs are bowled OR ${selectedFormat.maxWickets} ${if (selectedFormat.maxWickets == 1) "wicket falls" else "wickets fall"}.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Section 2: Player's Team Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "2. YOUR TEAM (PLAYER)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CricketPitchLightGreen,
                            letterSpacing = 0.5.sp
                        )
                    )

                    TeamSelectorGrid(
                        selectedTeam = selectedPlayerTeam,
                        onTeamSelected = { team ->
                            selectedPlayerTeam = team
                            // If opponent is the same team, pick another
                            if (selectedOpponentTeam.id == team.id) {
                                selectedOpponentTeam = Team.ALL_TEAMS.first { it.id != team.id }
                            }
                        },
                        testTagPrefix = "player_team"
                    )
                }

                // Section 3: CPU Opponent Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "3. OPPONENT TEAM (CPU)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CricketRoyalBlue,
                            letterSpacing = 0.5.sp
                        )
                    )

                    TeamSelectorGrid(
                        selectedTeam = selectedOpponentTeam,
                        disabledTeamId = selectedPlayerTeam.id,
                        onTeamSelected = { team ->
                            selectedOpponentTeam = team
                        },
                        testTagPrefix = "opponent_team"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FormatCard(
    format: MatchFormat,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .testTag("format_${format.overs}_over")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
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
                text = "${format.overs}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) CricketGold else TextPrimary,
                    fontSize = 22.sp
                )
            )
            Text(
                text = if (format.overs == 1) "OVER" else "OVERS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isSelected) CricketPitchGreen.copy(alpha = 0.2f) else Color(0xFF1E293B)
            ) {
                Text(
                    text = "${format.maxWickets} ${if (format.maxWickets == 1) "Wkt" else "Wkts"}",
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) CricketPitchLightGreen else TextSecondary,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun TeamSelectorGrid(
    selectedTeam: Team,
    disabledTeamId: String? = null,
    onTeamSelected: (Team) -> Unit,
    testTagPrefix: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = Team.ALL_TEAMS.chunked(5)
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (team in row) {
                    val isSelected = (team.id == selectedTeam.id)
                    val isDisabled = (team.id == disabledTeamId)

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(68.dp)
                            .testTag("${testTagPrefix}_${team.id}")
                            .clickable(enabled = !isDisabled) { onTeamSelected(team) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSelected -> StadiumCardNavy
                                isDisabled -> Color(0xFF131B2E)
                                else -> ScoreboardBg
                            }
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = if (isSelected) Brush.linearGradient(listOf(CricketGold, CricketPitchGreen))
                            else Brush.linearGradient(listOf(StadiumCardBorder, Color.Transparent)),
                            width = if (isSelected) 2.dp else 1.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = team.flagEmoji,
                                fontSize = 20.sp
                            )
                            Text(
                                text = team.shortName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isSelected -> CricketGold
                                        isDisabled -> TextTertiary
                                        else -> TextPrimary
                                    },
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
