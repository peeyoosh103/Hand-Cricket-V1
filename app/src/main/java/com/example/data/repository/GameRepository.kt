package com.example.data.repository

import com.example.data.local.PreferencesManager
import com.example.data.model.*
import com.example.domain.engine.FixtureGenerator
import com.example.domain.engine.NrrCalculator
import com.example.domain.engine.SimulationEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepository(
    private val preferencesManager: PreferencesManager,
    private val simulationEngine: SimulationEngine = SimulationEngine()
) {
    private val _tournamentState = MutableStateFlow<TournamentState?>(preferencesManager.getTournament())
    val tournamentState: StateFlow<TournamentState?> = _tournamentState.asStateFlow()

    private val _careerStats = MutableStateFlow(preferencesManager.getCareerStats())
    val careerStats: StateFlow<PlayerCareerStats> = _careerStats.asStateFlow()

    private val _matchHistory = MutableStateFlow(preferencesManager.getMatchHistory())
    val matchHistory: StateFlow<List<MatchResult>> = _matchHistory.asStateFlow()

    private val _settings = MutableStateFlow(preferencesManager.getSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    // Tournament Management
    fun createNewTournament(userTeamId: String, format: MatchFormat): TournamentState {
        val fixtures = FixtureGenerator.generateLeagueFixtures(format)
        val initialPointsTable = NrrCalculator.calculatePointsTable(fixtures)

        val newTournament = TournamentState(
            tournamentId = "TOURNAMENT_${System.currentTimeMillis()}",
            userTeamId = userTeamId,
            format = format,
            stage = TournamentStage.LEAGUE,
            fixtures = fixtures,
            pointsTable = initialPointsTable,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        _tournamentState.value = newTournament
        preferencesManager.saveTournament(newTournament)

        // Increment WC appearances in career stats
        updateCareerStats { current ->
            current.copy(worldCupAppearances = current.worldCupAppearances + 1)
        }

        return newTournament
    }

    fun updateTournamentAfterMatch(matchResult: MatchResult) {
        val currentTournament = _tournamentState.value ?: return
        val currentFixtures = currentTournament.fixtures.toMutableList()

        val fixtureIndex = currentFixtures.indexOfFirst { it.id == matchResult.matchId }
        if (fixtureIndex == -1) return

        val fixture = currentFixtures[fixtureIndex]

        val team1IsInnings1 = (matchResult.innings1.battingTeam.id == fixture.team1Id)
        val team1Runs = if (team1IsInnings1) matchResult.innings1.runs else matchResult.innings2.runs
        val team1Wickets = if (team1IsInnings1) matchResult.innings1.wickets else matchResult.innings2.wickets
        val team1Balls = if (team1IsInnings1) matchResult.innings1.ballsBowled else matchResult.innings2.ballsBowled

        val team2Runs = if (!team1IsInnings1) matchResult.innings1.runs else matchResult.innings2.runs
        val team2Wickets = if (!team1IsInnings1) matchResult.innings1.wickets else matchResult.innings2.wickets
        val team2Balls = if (!team1IsInnings1) matchResult.innings1.ballsBowled else matchResult.innings2.ballsBowled

        val updatedFixture = fixture.copy(
            status = FixtureStatus.COMPLETED,
            resultSummary = matchResult.marginText,
            winnerTeamId = matchResult.winner?.id,
            isTie = matchResult.isTie,
            team1Runs = team1Runs,
            team1Wickets = team1Wickets,
            team1Balls = team1Balls,
            team2Runs = team2Runs,
            team2Wickets = team2Wickets,
            team2Balls = team2Balls
        )

        currentFixtures[fixtureIndex] = updatedFixture

        // Recalculate Points Table for league fixtures
        val updatedPointsTable = NrrCalculator.calculatePointsTable(currentFixtures)

        var newStage = currentTournament.stage
        var sf1Id = currentTournament.semiFinal1FixtureId
        var sf2Id = currentTournament.semiFinal2FixtureId
        var finalId = currentTournament.finalFixtureId
        var championId = currentTournament.championTeamId
        var runnerUpId = currentTournament.runnerUpTeamId

        // Check if League finished and semi-finals need creation
        val leagueDone = currentFixtures.filter { it.stage == TournamentStage.LEAGUE }.all { it.status == FixtureStatus.COMPLETED }
        if (leagueDone && (currentTournament.stage == TournamentStage.LEAGUE)) {
            val top4 = updatedPointsTable.take(4).map { it.teamId }
            if (top4.size >= 4 && sf1Id == null) {
                val (sf1, sf2) = FixtureGenerator.createSemiFinalFixtures(currentTournament.format, top4)
                currentFixtures.add(sf1)
                currentFixtures.add(sf2)
                sf1Id = sf1.id
                sf2Id = sf2.id
                newStage = TournamentStage.SEMI_FINALS
            }
        }

        // Check if Semi Finals finished and final needs creation
        val sfFixtures = currentFixtures.filter { it.stage == TournamentStage.SEMI_FINALS }
        if (sfFixtures.isNotEmpty() && sfFixtures.all { it.status == FixtureStatus.COMPLETED } && finalId == null) {
            val sf1Winner = currentFixtures.first { it.id == sf1Id }.winnerTeamId
            val sf2Winner = currentFixtures.first { it.id == sf2Id }.winnerTeamId
            if (sf1Winner != null && sf2Winner != null) {
                val finalFixture = FixtureGenerator.createFinalFixture(currentTournament.format, sf1Winner, sf2Winner)
                currentFixtures.add(finalFixture)
                finalId = finalFixture.id
                newStage = TournamentStage.FINAL
            }
        }

        // Check if Final finished and champion crowned
        val finalFixture = currentFixtures.firstOrNull { it.stage == TournamentStage.FINAL }
        if (finalFixture != null && finalFixture.status == FixtureStatus.COMPLETED) {
            championId = finalFixture.winnerTeamId
            runnerUpId = if (finalFixture.winnerTeamId == finalFixture.team1Id) finalFixture.team2Id else finalFixture.team1Id
            newStage = TournamentStage.CHAMPION

            // If user won the tournament, increment World Cup win
            if (championId != null && championId.equals(currentTournament.userTeamId, ignoreCase = true)) {
                updateCareerStats { it.copy(worldCupWins = it.worldCupWins + 1) }
            }
        }

        val updatedTournament = currentTournament.copy(
            stage = newStage,
            fixtures = currentFixtures,
            pointsTable = updatedPointsTable,
            semiFinal1FixtureId = sf1Id,
            semiFinal2FixtureId = sf2Id,
            finalFixtureId = finalId,
            championTeamId = championId,
            runnerUpTeamId = runnerUpId,
            updatedAt = System.currentTimeMillis()
        )

        _tournamentState.value = updatedTournament
        preferencesManager.saveTournament(updatedTournament)
    }

    fun simulateFixture(fixtureId: String): MatchResult? {
        val currentTournament = _tournamentState.value ?: return null
        val fixture = currentTournament.fixtures.firstOrNull { it.id == fixtureId } ?: return null
        if (fixture.status == FixtureStatus.COMPLETED) return null

        val (updatedFixture, result) = simulationEngine.simulateMatch(fixture)
        updateTournamentAfterMatch(result)
        saveMatchResult(result)
        return result
    }

    fun simulateAllUpcomingCpuLeagueMatches() {
        val currentTournament = _tournamentState.value ?: return
        val userTeamId = currentTournament.userTeamId

        val upcomingCpuMatches = currentTournament.fixtures.filter {
            it.stage == TournamentStage.LEAGUE &&
            it.status == FixtureStatus.UPCOMING &&
            !it.involvesTeam(userTeamId)
        }

        for (fixture in upcomingCpuMatches) {
            simulateFixture(fixture.id)
        }
    }

    fun resetTournament() {
        _tournamentState.value = null
        preferencesManager.clearTournament()
    }

    // Match History & Career Stats
    fun saveMatchResult(result: MatchResult) {
        preferencesManager.saveMatchToHistory(result)
        _matchHistory.value = preferencesManager.getMatchHistory()

        // Update player career stats if user played
        // In Quick Match or World Cup where user is team1 (or played batting/bowling)
        val userTeam = result.team1
        val isUserWin = result.winner?.id == userTeam.id
        val isUserLoss = result.winner != null && result.winner.id != userTeam.id
        val isTie = result.isTie

        val userBattingInnings = if (result.innings1.battingTeam.id == userTeam.id) result.innings1 else result.innings2
        val userBowlingInnings = if (result.innings1.bowlingTeam.id == userTeam.id) result.innings1 else result.innings2

        updateCareerStats { current ->
            val newTotalRuns = current.totalRuns + userBattingInnings.runs
            val newBallsFaced = current.totalBallsFaced + userBattingInnings.ballsBowled
            val newWicketsTaken = current.totalWicketsTaken + userBowlingInnings.wickets
            val newBallsBowled = current.totalBallsBowled + userBowlingInnings.ballsBowled
            val newHighest = maxOf(current.highestScore, userBattingInnings.runs)
            val newFours = current.foursCount + userBattingInnings.countFours
            val newSixes = current.sixesCount + userBattingInnings.countSixes
            val isDuck = userBattingInnings.runs == 0 && userBattingInnings.wickets > 0

            val bestWkts = maxOf(current.bestBowlingWickets, userBowlingInnings.wickets)
            val bestRuns = if (userBowlingInnings.wickets > current.bestBowlingWickets) {
                userBowlingInnings.runs
            } else current.bestBowlingRuns

            current.copy(
                matchesPlayed = current.matchesPlayed + 1,
                wins = current.wins + (if (isUserWin) 1 else 0),
                losses = current.losses + (if (isUserLoss) 1 else 0),
                ties = current.ties + (if (isTie) 1 else 0),
                totalRuns = newTotalRuns,
                totalBallsFaced = newBallsFaced,
                totalWicketsTaken = newWicketsTaken,
                totalBallsBowled = newBallsBowled,
                highestScore = newHighest,
                foursCount = newFours,
                sixesCount = newSixes,
                ducksCount = current.ducksCount + (if (isDuck) 1 else 0),
                bestBowlingWickets = bestWkts,
                bestBowlingRuns = bestRuns
            )
        }
    }

    private fun updateCareerStats(transform: (PlayerCareerStats) -> PlayerCareerStats) {
        val updated = transform(_careerStats.value)
        _careerStats.value = updated
        preferencesManager.saveCareerStats(updated)
    }

    fun resetMatchHistory() {
        preferencesManager.clearMatchHistory()
        _matchHistory.value = emptyList()
    }

    fun resetCareerStats() {
        preferencesManager.clearCareerStats()
        _careerStats.value = PlayerCareerStats()
    }

    // Settings
    fun updateSettings(settings: GameSettings) {
        _settings.value = settings
        preferencesManager.saveSettings(settings)
    }
}
