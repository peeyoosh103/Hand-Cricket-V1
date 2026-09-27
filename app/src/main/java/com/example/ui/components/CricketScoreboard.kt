package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InningsState
import com.example.data.model.MatchFormat
import com.example.data.model.Team
import com.example.ui.theme.*

@Composable
fun CricketScoreboard(
    format: MatchFormat,
    currentInnings: InningsState,
    isUserBatting: Boolean,
    playerTeam: Team,
    opponentTeam: Team,
    isSuperOver: Boolean = false,
    modifier: Modifier = Modifier
) {
    val battingTeam = currentInnings.battingTeam
    val bowlingTeam = currentInnings.bowlingTeam

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cricket_scoreboard"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    if (isSuperOver) CricketGold else CricketPitchGreen,
                    CricketRoyalBlue
                )
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Format Tag, Super Over Tag, Innings Tag & Batting Role
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Match Format Pill with Wicket limit
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StadiumCardNavy,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(listOf(StadiumCardBorder, CricketRoyalBlue)),
                            width = 1.dp
                        )
                    ) {
                        Text(
                            text = if (isSuperOver) "SUPER OVER" else "${format.displayName} · ${format.wicketRuleDescription}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSuperOver) CricketGold else CricketPitchLightGreen
                            )
                        )
                    }

                    // Innings Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "Innings ${currentInnings.inningsNumber}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Player Role Badge (You are BATTING or BOWLING)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isUserBatting) CricketPitchGreen.copy(alpha = 0.2f) else CricketRoyalBlue.copy(alpha = 0.2f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (isUserBatting) listOf(CricketPitchGreen, CricketPitchLightGreen)
                            else listOf(CricketRoyalBlue, CricketBoundaryPurple)
                        ),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isUserBatting) CricketPitchGreen else CricketRoyalBlue)
                        )
                        Text(
                            text = if (isUserBatting) "YOU ARE BATTING" else "YOU ARE BOWLING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isUserBatting) CricketPitchLightGreen else Color(0xFF93C5FD)
                            )
                        )
                    }
                }
            }

            // Middle Score Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Team & Big Score Display
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = battingTeam.flagEmoji,
                            fontSize = 22.sp
                        )
                        Text(
                            text = battingTeam.name.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Score: Runs / Wickets
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${currentInnings.runs}/${currentInnings.wickets}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                fontFamily = FontFamily.SansSerif
                            ),
                            modifier = Modifier.testTag("scoreboard_runs_wickets")
                        )

                        Text(
                            text = "(${currentInnings.oversFormatted}/${currentInnings.maxOvers}.0 ov)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                // Wickets Limit Status Indicator Box
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = StadiumCardNavy,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(StadiumCardBorder, Color(0xFF334155))),
                            width = 1.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Wickets",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "${currentInnings.wickets}/${currentInnings.maxWickets}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentInnings.remainingWickets <= 1) CricketWicketRed else CricketGold
                                )
                            )
                            Text(
                                text = "${currentInnings.remainingWickets} left",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (currentInnings.remainingWickets <= 1) CricketWicketRed else TextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    // Current Run Rate
                    Text(
                        text = "CRR: ${String.format("%.2f", currentInnings.runRate)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Target Banner for 2nd Innings
            if (currentInnings.target != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E1B4B),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CricketBoundaryPurple, CricketRoyalBlue)),
                        width = 1.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TARGET: ${currentInnings.target}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CricketGold
                                )
                            )
                            val reqRuns = currentInnings.requiredRuns ?: 0
                            val remBalls = currentInnings.remainingBalls
                            Text(
                                text = "Need $reqRuns runs from $remBalls balls",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        currentInnings.requiredRunRate?.let { rrr ->
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "RRR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = String.format("%.2f", rrr),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (rrr > 12.0) CricketWicketRed else CricketPitchLightGreen
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
