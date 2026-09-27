package com.example.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentState
import com.example.ui.components.StadiumBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    viewModel: GameViewModel
) {
    val tournamentState by viewModel.tournamentState.collectAsState()
    val careerStats by viewModel.careerStats.collectAsState()

    StadiumBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Header: Game Branding & Settings Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(CricketPitchGreen, CricketRoyalBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏏", fontSize = 20.sp)
                    }

                    Column {
                        Text(
                            text = "HAND CRICKET",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = CricketGold
                            )
                        )
                        Text(
                            text = "PRO EDITION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CricketPitchLightGreen,
                                letterSpacing = 1.sp,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Settings) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(StadiumCardNavy)
                        .border(1.dp, StadiumCardBorder, CircleShape)
                        .testTag("home_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = TextPrimary
                    )
                }
            }

            // Hero Stadium Logo Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_banner"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ScoreboardBg),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(
                        listOf(CricketGold, CricketPitchGreen, CricketRoyalBlue)
                    ),
                    width = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🏟️",
                        fontSize = 36.sp
                    )

                    Text(
                        text = "HAND CRICKET",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = Color.White
                        )
                    )

                    Text(
                        text = "The classic cricket hand-gesture game with authentic tournament rules, wicket limits & Super Overs!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    )

                    // Quick User Stats Ribbon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StadiumCardNavy)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "MATCHES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = "${careerStats.matchesPlayed}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(StadiumCardBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "WINS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = "${careerStats.wins}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CricketPitchLightGreen
                                )
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(StadiumCardBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "HIGH SCORE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = "${careerStats.highestScore}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CricketGold
                                )
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(StadiumCardBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "WC TITLES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            )
                            Text(
                                text = "${careerStats.worldCupWins} 🏆",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CricketGold
                                )
                            )
                        }
                    }
                }
            }

            // Main Menu Buttons (5 Core Game Modes)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Quick Match (Vibrant Primary)
                HomeMenuCard(
                    title = "QUICK MATCH",
                    subtitle = "Play instant 1, 2, 5 or 10 overs match vs CPU",
                    icon = "⚡",
                    gradient = listOf(CricketPitchGreen, Color(0xFF047857)),
                    accentColor = CricketPitchLightGreen,
                    testTag = "home_quick_match_button",
                    onClick = { viewModel.navigateTo(Screen.QuickMatchSetup) }
                )

                // 2. World Cup (Golden Major Tournament Mode)
                HomeMenuCard(
                    title = "WORLD CUP",
                    subtitle = if (tournamentState != null) {
                        "Continue active tournament as ${tournamentState?.userTeam?.name} (${tournamentState?.completedFixturesCount}/45 matches)"
                    } else {
                        "10 Nations, 45 League Matches, Semi-Finals & World Cup Final"
                    },
                    icon = "🏆",
                    gradient = listOf(Color(0xFFB45309), CricketGold),
                    accentColor = CricketGoldLight,
                    badgeText = if (tournamentState != null) "RESUME" else "MAJOR MODE",
                    testTag = "home_world_cup_button",
                    onClick = {
                        if (tournamentState != null) {
                            viewModel.navigateTo(Screen.WorldCupHub)
                        } else {
                            viewModel.navigateTo(Screen.WorldCupSetup)
                        }
                    }
                )

                // 3. Practice Mode
                HomeMenuCard(
                    title = "PRACTICE",
                    subtitle = "Master batting & bowling number skills with unlimited attempts",
                    icon = "🎯",
                    gradient = listOf(Color(0xFF4338CA), CricketRoyalBlue),
                    accentColor = Color(0xFF93C5FD),
                    testTag = "home_practice_button",
                    onClick = { viewModel.navigateTo(Screen.Practice) }
                )

                // 4. Statistics
                HomeMenuCard(
                    title = "STATISTICS",
                    subtitle = "Career win rates, run aggregates, boundaries & bowling records",
                    icon = "📊",
                    gradient = listOf(Color(0xFF6D28D9), CricketBoundaryPurple),
                    accentColor = Color(0xFFC4B5FD),
                    testTag = "home_statistics_button",
                    onClick = { viewModel.navigateTo(Screen.Statistics) }
                )

                // 5. Match History
                HomeMenuCard(
                    title = "MATCH HISTORY",
                    subtitle = "Review past scorecards, run chases & results",
                    icon = "📜",
                    gradient = listOf(Color(0xFF1E293B), Color(0xFF334155)),
                    accentColor = Color(0xFFCBD5E1),
                    testTag = "home_match_history_button",
                    onClick = { viewModel.navigateTo(Screen.MatchHistory) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HomeMenuCard(
    title: String,
    subtitle: String,
    icon: String,
    gradient: List<Color>,
    accentColor: Color,
    badgeText: String? = null,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = accentColor.copy(alpha = 0.3f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = StadiumCardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(StadiumCardBorder, accentColor.copy(alpha = 0.4f))),
            width = 1.2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 26.sp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = TextPrimary
                        )
                    )

                    badgeText?.let { badge ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CricketGold.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(CricketGold, CricketGoldLight)),
                                width = 1.dp
                            )
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 8.sp,
                                    color = CricketGoldLight
                                )
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = accentColor
            )
        }
    }
}
