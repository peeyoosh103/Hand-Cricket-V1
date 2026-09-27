package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Team
import com.example.data.model.TossChoice
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TossDialog(
    playerTeam: Team,
    opponentTeam: Team,
    onCallHeadsOrTails: (Boolean) -> Unit,
    tossWinner: Team?,
    isPlayerTossWinner: Boolean,
    onPlayerDecision: (TossChoice) -> Unit
) {
    var isFlipping by remember { mutableStateOf(false) }
    var selectedCall by remember { mutableStateOf<Boolean?>(null) } // true: Heads, false: Tails

    val infiniteTransition = rememberInfiniteTransition(label = "coin_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coin_rotation"
    )

    Dialog(
        onDismissRequest = { /* Non-dismissable until toss complete */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("toss_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(listOf(CricketGold, CricketPitchGreen)),
                width = 2.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header
                Text(
                    text = "🪙 OFFICIAL MATCH TOSS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = CricketGold,
                        letterSpacing = 1.sp
                    )
                )

                // Matchup
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = playerTeam.flagEmoji, fontSize = 32.sp)
                        Text(
                            text = playerTeam.name,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "(You)",
                            style = MaterialTheme.typography.labelSmall.copy(color = CricketPitchLightGreen)
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
                        Text(text = opponentTeam.flagEmoji, fontSize = 32.sp)
                        Text(
                            text = opponentTeam.name,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "(CPU)",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                // Coin Graphic
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(CricketGoldLight, CricketGold, Color(0xFFB45309))
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                        .rotate(if (isFlipping) rotation else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏏",
                        fontSize = 44.sp
                    )
                }

                // Phase 1: Call Heads or Tails
                if (tossWinner == null && !isFlipping) {
                    Text(
                        text = "Captain ${playerTeam.name}, make your call for the coin toss:",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedCall = true
                                isFlipping = true
                                onCallHeadsOrTails(true)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("toss_heads_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "HEADS",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Button(
                            onClick = {
                                selectedCall = false
                                isFlipping = true
                                onCallHeadsOrTails(false)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("toss_tails_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CricketRoyalBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "TAILS",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else if (tossWinner == null && isFlipping) {
                    Text(
                        text = "Flipping coin in the air...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = CricketGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else if (tossWinner != null && isPlayerTossWinner) {
                    // Phase 2: Player won toss -> choose Bat or Bowl
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CricketPitchGreen.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(CricketPitchGreen, CricketPitchLightGreen)),
                                width = 1.dp
                            )
                        ) {
                            Text(
                                text = "🎉 You won the Toss!",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CricketPitchLightGreen
                                )
                            )
                        }

                        Text(
                            text = "What would you like to do first?",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { onPlayerDecision(TossChoice.BAT) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("toss_bat_first_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "🏏 BAT FIRST",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Button(
                                onClick = { onPlayerDecision(TossChoice.BOWL) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("toss_bowl_first_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CricketRoyalBlue),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "⚾ BOWL FIRST",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
