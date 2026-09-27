package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InningsState
import com.example.data.model.MatchFormat
import com.example.ui.theme.*

@Composable
fun InningsBreakModal(
    format: MatchFormat,
    firstInnings: InningsState,
    onStartSecondInnings: () -> Unit
) {
    val target = firstInnings.runs + 1
    val chasingTeam = firstInnings.bowlingTeam
    val defendingTeam = firstInnings.battingTeam

    Dialog(
        onDismissRequest = { /* Non-dismissable until button click */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("innings_break_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(CricketBoundaryPurple, CricketRoyalBlue)),
                width = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Innings Break Title
                Text(
                    text = "⏸️ INNINGS BREAK",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = CricketGold,
                        letterSpacing = 1.sp
                    )
                )

                // 1st Innings Recap Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StadiumCardNavy)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "1ST INNINGS SUMMARY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = defendingTeam.flagEmoji, fontSize = 24.sp)
                            Text(
                                text = defendingTeam.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Text(
                            text = "${firstInnings.runs}/${firstInnings.wickets}",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = "Overs: ${firstInnings.oversFormatted} / ${firstInnings.maxOvers}.0 | Wickets: ${firstInnings.wickets}/${firstInnings.maxWickets}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary
                            )
                        )

                        firstInnings.completedReason?.let { reason ->
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CricketGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                // Target Announcement
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(CricketGold, CricketPitchGreen)),
                        width = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "TARGET FOR ${chasingTeam.name.uppercase()}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = CricketPitchLightGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "$target RUNS",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = CricketGold
                            )
                        )
                        Text(
                            text = "from ${format.overs * 6} balls (Wicket limit: ${format.maxWickets})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary
                            )
                        )
                    }
                }

                // CTA Button
                Button(
                    onClick = onStartSecondInnings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_second_innings_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen)
                ) {
                    Text(
                        text = "START 2ND INNINGS 🚀",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    )
                }
            }
        }
    }
}
