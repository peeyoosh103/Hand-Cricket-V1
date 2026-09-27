package com.example.ui.screens.worldcup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PointsTableRow
import com.example.data.model.TournamentStage
import com.example.ui.components.StadiumBackground
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsTableScreen(
    viewModel: GameViewModel
) {
    val tournamentState by viewModel.tournamentState.collectAsState()
    val tournament = tournamentState ?: return

    val pointsTable = tournament.pointsTable
    val userTeamId = tournament.userTeamId

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "WORLD CUP POINTS TABLE",
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
                            modifier = Modifier.testTag("points_table_back_button")
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
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Table Legend & Qualification Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ScoreboardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CricketPitchGreen))
                            Text(
                                text = "Top 4 Qualify for Semi-Finals",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CricketPitchLightGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Text(
                            text = "Win: 2 pts | Tie: 1 pt",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Table Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StadiumCardNavy)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "POS", modifier = Modifier.width(32.dp), style = tableHeaderStyle, textAlign = TextAlign.Center)
                    Text(text = "TEAM", modifier = Modifier.weight(1.3f), style = tableHeaderStyle)
                    Text(text = "P", modifier = Modifier.width(24.dp), style = tableHeaderStyle, textAlign = TextAlign.Center)
                    Text(text = "W", modifier = Modifier.width(24.dp), style = tableHeaderStyle, textAlign = TextAlign.Center)
                    Text(text = "L", modifier = Modifier.width(24.dp), style = tableHeaderStyle, textAlign = TextAlign.Center)
                    Text(text = "T", modifier = Modifier.width(24.dp), style = tableHeaderStyle, textAlign = TextAlign.Center)
                    Text(text = "PTS", modifier = Modifier.width(32.dp), style = tableHeaderStyle.copy(color = CricketGold), textAlign = TextAlign.Center)
                    Text(text = "NRR", modifier = Modifier.width(44.dp), style = tableHeaderStyle, textAlign = TextAlign.End)
                }

                // Table Body (10 Teams)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(pointsTable) { index, row ->
                        val position = index + 1
                        val isUserTeam = row.teamId.equals(userTeamId, ignoreCase = true)
                        val isTop4 = (position <= 4)
                        val isLeagueFinished = tournament.isLeagueFinished

                        PointsTableRowItem(
                            position = position,
                            row = row,
                            isUserTeam = isUserTeam,
                            isTop4 = isTop4,
                            isLeagueFinished = isLeagueFinished
                        )

                        // Qualification Divider after 4th place
                        if (position == 4) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Divider(
                                    modifier = Modifier.weight(1f),
                                    color = CricketPitchGreen,
                                    thickness = 1.dp
                                )
                                Text(
                                    text = "── QUALIFICATION CUT-OFF ──",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CricketPitchLightGreen,
                                        fontSize = 9.sp
                                    )
                                )
                                Divider(
                                    modifier = Modifier.weight(1f),
                                    color = CricketPitchGreen,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

private val tableHeaderStyle = androidx.compose.ui.text.TextStyle(
    fontWeight = FontWeight.Black,
    fontSize = 10.sp,
    color = TextSecondary
)

@Composable
private fun PointsTableRowItem(
    position: Int,
    row: PointsTableRow,
    isUserTeam: Boolean,
    isTop4: Boolean,
    isLeagueFinished: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("points_row_${row.teamId}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isUserTeam -> StadiumCardNavy
                isTop4 -> ScoreboardBg
                else -> Color(0xFF0F172A)
            }
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = when {
                isUserTeam -> Brush.horizontalGradient(listOf(CricketPitchGreen, CricketGold))
                isTop4 -> Brush.horizontalGradient(listOf(CricketPitchGreen.copy(alpha = 0.5f), Color.Transparent))
                else -> Brush.horizontalGradient(listOf(StadiumCardBorder.copy(alpha = 0.4f), Color.Transparent))
            },
            width = if (isUserTeam) 1.5.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position
            Text(
                text = "$position",
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = when {
                        position == 1 -> CricketGold
                        isTop4 -> CricketPitchLightGreen
                        else -> TextSecondary
                    }
                )
            )

            // Team Name & Flag
            Row(
                modifier = Modifier.weight(1.3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = row.team.flagEmoji, fontSize = 16.sp)
                Text(
                    text = row.team.shortName,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isUserTeam) FontWeight.Black else FontWeight.Bold,
                        color = if (isUserTeam) CricketGold else TextPrimary
                    )
                )
                if (isUserTeam) {
                    Text(text = "👤", fontSize = 10.sp)
                }
                if (isLeagueFinished && isTop4) {
                    Text(text = "Q", fontSize = 9.sp, color = CricketPitchLightGreen, fontWeight = FontWeight.Black)
                } else if (isLeagueFinished && !isTop4) {
                    Text(text = "E", fontSize = 9.sp, color = CricketWicketRed, fontWeight = FontWeight.Black)
                }
            }

            // P, W, L, T
            Text(text = "${row.played}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, style = tableCellStyle)
            Text(text = "${row.won}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, style = tableCellStyle.copy(fontWeight = FontWeight.Bold, color = CricketPitchLightGreen))
            Text(text = "${row.lost}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, style = tableCellStyle.copy(color = TextSecondary))
            Text(text = "${row.tied}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center, style = tableCellStyle.copy(color = TextSecondary))

            // Points
            Text(
                text = "${row.points}",
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = CricketGold
                )
            )

            // NRR
            val nrrFormatted = if (row.nrr > 0) "+${String.format("%.3f", row.nrr)}" else String.format("%.3f", row.nrr)
            Text(
                text = nrrFormatted,
                modifier = Modifier.width(44.dp),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (row.nrr >= 0) CricketPitchLightGreen else CricketWicketRed,
                    fontSize = 10.sp
                )
            )
        }
    }
}

private val tableCellStyle = androidx.compose.ui.text.TextStyle(
    fontSize = 11.sp,
    color = TextPrimary
)
