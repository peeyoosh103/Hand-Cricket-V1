package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.history.MatchHistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.match.GameplayScreen
import com.example.ui.screens.match.MatchResultScreen
import com.example.ui.screens.match.QuickMatchSetupScreen
import com.example.ui.screens.match.ScorecardScreen
import com.example.ui.screens.practice.PracticeScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.stats.StatisticsScreen
import com.example.ui.screens.worldcup.*
import com.example.ui.theme.HandCricketTheme
import com.example.ui.theme.StadiumDeepNavy
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameViewModel = viewModel()
            val settings by viewModel.settings.collectAsState()

            HandCricketTheme(darkTheme = settings.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = StadiumDeepNavy
                ) {
                    HandCricketApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun HandCricketApp(viewModel: GameViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            is Screen.Home -> HomeScreen(viewModel = viewModel)
            is Screen.QuickMatchSetup -> QuickMatchSetupScreen(viewModel = viewModel)
            is Screen.Gameplay -> GameplayScreen(
                matchType = screen.matchType,
                fixtureId = screen.fixtureId,
                playerTeam = screen.playerTeam,
                opponentTeam = screen.opponentTeam,
                format = screen.format,
                viewModel = viewModel
            )
            is Screen.ResultScreen -> MatchResultScreen(
                result = screen.result,
                fromTournament = screen.fromTournament,
                viewModel = viewModel
            )
            is Screen.ScorecardScreen -> ScorecardScreen(
                result = screen.result,
                viewModel = viewModel
            )
            is Screen.WorldCupHub -> WorldCupHubScreen(viewModel = viewModel)
            is Screen.WorldCupSetup -> WorldCupSetupScreen(viewModel = viewModel)
            is Screen.WorldCupFixtures -> FixturesScreen(viewModel = viewModel)
            is Screen.WorldCupPointsTable -> PointsTableScreen(viewModel = viewModel)
            is Screen.WorldCupKnockout -> KnockoutScreen(viewModel = viewModel)
            is Screen.WorldCupTournamentStats -> TournamentStatsScreen(viewModel = viewModel)
            is Screen.WorldCupChampion -> ChampionScreen(viewModel = viewModel)
            is Screen.Practice -> PracticeScreen(viewModel = viewModel)
            is Screen.Statistics -> StatisticsScreen(viewModel = viewModel)
            is Screen.MatchHistory -> MatchHistoryScreen(viewModel = viewModel)
            is Screen.Settings -> SettingsScreen(viewModel = viewModel)
        }
    }
}
