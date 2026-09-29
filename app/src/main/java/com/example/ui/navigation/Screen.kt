package com.example.ui.navigation

import com.example.data.model.MatchFormat
import com.example.data.model.MatchResult
import com.example.data.model.MatchType
import com.example.data.model.Team

sealed class Screen {
    data object Home : Screen()
    data object QuickMatchSetup : Screen()
    data object WorldCupHub : Screen()
    data object WorldCupSetup : Screen()
    data object WorldCupFixtures : Screen()
    data object WorldCupPointsTable : Screen()
    data object WorldCupKnockout : Screen()
    data object WorldCupTournamentStats : Screen()
    data object WorldCupResults : Screen()
    data object WorldCupTeams : Screen()
    data object WorldCupChampion : Screen()
    
    data class Gameplay(
        val matchType: MatchType,
        val fixtureId: String? = null,
        val playerTeam: Team,
        val opponentTeam: Team,
        val format: MatchFormat
    ) : Screen()

    data class ResultScreen(
        val result: MatchResult,
        val fromTournament: Boolean = false
    ) : Screen()

    data class ScorecardScreen(
        val result: MatchResult
    ) : Screen()

    data object Practice : Screen()
    data object Statistics : Screen()
    data object MatchHistory : Screen()
    data object Settings : Screen()
    data object GameRules : Screen()
}
