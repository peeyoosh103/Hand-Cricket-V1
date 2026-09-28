package com.example.ui.screens.worldcup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Team
import com.example.ui.components.StadiumBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    viewModel: GameViewModel
) {
    val tournamentState by viewModel.tournamentState.collectAsState()
    val tournament = tournamentState

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "PARTICIPATING TEAMS (10)",
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
                            modifier = Modifier.testTag("teams_back_button")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(Team.ALL_TEAMS) { team ->
                    val isUserTeam = tournament?.userTeamId.equals(team.id, ignoreCase = true)
                    val teamRow = tournament?.pointsTable?.firstOrNull { it.teamId == team.id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("team_card_${team.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = if (isUserTeam) Brush.horizontalGradient(listOf(CricketPitchGreen, CricketGold))
                            else Brush.horizontalGradient(listOf(StadiumCardBorder, Color.Transparent)),
                            width = if (isUserTeam) 1.5.dp else 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(team.primaryColor, team.secondaryColor))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = team.flagEmoji, fontSize = 24.sp)
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = team.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUserTeam) CricketGold else TextPrimary
                                            )
                                        )
                                        if (isUserTeam) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = CricketPitchGreen.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "CAPTAIN",
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = CricketPitchLightGreen,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 9.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "ICC World Ranking: #${team.worldRanking}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                    )
                                }
                            }

                            if (teamRow != null) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${teamRow.points} PTS",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = CricketGold
                                        )
                                    )
                                    Text(
                                        text = "${teamRow.played}P (${teamRow.won}W - ${teamRow.lost}L)",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                    )
                                }
                            }
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
