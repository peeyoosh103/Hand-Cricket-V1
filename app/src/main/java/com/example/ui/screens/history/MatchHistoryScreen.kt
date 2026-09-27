package com.example.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.data.model.MatchResult
import com.example.data.model.MatchType
import com.example.ui.components.StadiumBackground
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchHistoryScreen(
    viewModel: GameViewModel
) {
    val matchHistory by viewModel.matchHistory.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, QUICK, WORLD_CUP

    val filteredList = when (selectedFilter) {
        "QUICK" -> matchHistory.filter { it.matchType == MatchType.QUICK_MATCH }
        "WORLD_CUP" -> matchHistory.filter { it.matchType.name.startsWith("WORLD_CUP") }
        else -> matchHistory
    }

    StadiumBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "MATCH HISTORY 📜",
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
                            modifier = Modifier.testTag("history_back_button")
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
                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("All Matches (${matchHistory.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "QUICK",
                        onClick = { selectedFilter = "QUICK" },
                        label = { Text("Quick Matches") }
                    )
                    FilterChip(
                        selected = selectedFilter == "WORLD_CUP",
                        onClick = { selectedFilter = "WORLD_CUP" },
                        label = { Text("World Cup") }
                    )
                }

                if (filteredList.isEmpty()) {
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
                            Text(text = "🏏", fontSize = 48.sp)
                            Text(
                                text = "No matches played yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            )
                            Text(
                                text = "Play a Quick Match or start World Cup to record history",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextTertiary
                                )
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredList) { result ->
                            MatchHistoryCard(
                                result = result,
                                onClick = {
                                    viewModel.navigateTo(Screen.ScorecardScreen(result))
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
private fun MatchHistoryCard(
    result: MatchResult,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateString = dateFormat.format(Date(result.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_card_${result.matchId}")
            .clickable(onClick = onClick),
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
            // Header: Date & Match Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (result.matchType) {
                        MatchType.QUICK_MATCH -> "QUICK MATCH"
                        MatchType.WORLD_CUP_LEAGUE -> "WORLD CUP LEAGUE"
                        MatchType.WORLD_CUP_SEMI_FINAL -> "WORLD CUP SEMI-FINAL"
                        MatchType.WORLD_CUP_FINAL -> "WORLD CUP FINAL"
                        else -> "MATCH"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = CricketGold,
                        fontSize = 10.sp
                    )
                )

                Text(
                    text = "$dateString · ${result.format.displayName} (${result.format.maxWickets} ${if (result.format.maxWickets == 1) "wkt" else "wkts"})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }

            // Teams and Scores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = result.innings1.battingTeam.flagEmoji, fontSize = 20.sp)
                    Text(
                        text = result.innings1.battingTeam.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (result.winner?.id == result.innings1.battingTeam.id) FontWeight.Black else FontWeight.Bold,
                            color = if (result.winner?.id == result.innings1.battingTeam.id) CricketGold else TextPrimary
                        )
                    )
                }

                Text(
                    text = "${result.innings1.runs}/${result.innings1.wickets} (${result.innings1.oversFormatted} ov)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 2
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = result.innings2.battingTeam.flagEmoji, fontSize = 20.sp)
                    Text(
                        text = result.innings2.battingTeam.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (result.winner?.id == result.innings2.battingTeam.id) FontWeight.Black else FontWeight.Bold,
                            color = if (result.winner?.id == result.innings2.battingTeam.id) CricketGold else TextPrimary
                        )
                    )
                }

                Text(
                    text = "${result.innings2.runs}/${result.innings2.wickets} (${result.innings2.oversFormatted} ov)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            // Result margin text & View Scorecard affordance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.marginText,
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
                        style = MaterialTheme.typography.labelSmall.copy(color = CricketRoyalBlue, fontWeight = FontWeight.Bold)
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
