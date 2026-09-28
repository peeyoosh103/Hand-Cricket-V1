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

    /**
     * Called when a match (player or simulated) completes in World Cup.
     * Updates fixture, saves match result, and triggers automatic tournament progression.
     */
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

        val updatedTournament = currentTournament.copy(
            fixtures = currentFixtures,
            pointsTable = updatedPointsTable,
            updatedAt = System.currentTimeMillis()
        )

        _tournamentState.value = updatedTournament
        preferencesManager.saveTournament(updatedTournament)

        // Run automatic tournament progression for CPU matches and next stages!
        autoProgressTournament()
    }

    /**
     * Automatic Tournament Progression Engine:
     * - Simulates eligible CPU-vs-CPU league fixtures up to the player's current round.
     * - If player completes all league matches, ensures all 45 league matches are simulated.
     * - Automatically creates Semi-Final 1 and Semi-Final 2 from Top 4 teams.
     * - Automatically simulates CPU Semi-Finals.
     * - Automatically creates the Final when both Semi-Finals complete.
     * - If player is not in Final, automatically simulates the Final and declares champion.
     */
    fun autoProgressTournament() {
        val tournament = _tournamentState.value ?: return
        val userTeamId = tournament.userTeamId
        val fixtures = tournament.fixtures.toMutableList()
        var stage = tournament.stage
        var sf1Id = tournament.semiFinal1FixtureId
        var sf2Id = tournament.semiFinal2FixtureId
        var finalId = tournament.finalFixtureId
        var championId = tournament.championTeamId
        var runnerUpId = tournament.runnerUpTeamId

        var stateChanged = false

        // ----------------------------------------------------
        // PHASE 1: LEAGUE STAGE CPU PROGRESSION
        // ----------------------------------------------------
        val userCompletedLeagueMatches = fixtures.filter {
            it.stage == TournamentStage.LEAGUE &&
            it.involvesTeam(userTeamId) &&
            it.status == FixtureStatus.COMPLETED
        }
        val maxPlayerRound = userCompletedLeagueMatches.maxOfOrNull { it.round } ?: 0
        val isAllUserLeagueDone = (userCompletedLeagueMatches.size >= 9)

        // Only simulate CPU matches if player has played their match(es)
        val targetRound = if (isAllUserLeagueDone) 9 else maxPlayerRound

        if (targetRound > 0) {
            val eligibleCpuFixtures = fixtures.filter {
                it.stage == TournamentStage.LEAGUE &&
                it.status == FixtureStatus.UPCOMING &&
                !it.involvesTeam(userTeamId) &&
                (isAllUserLeagueDone || it.round <= targetRound)
            }

            for (fixture in eligibleCpuFixtures) {
                // Ensure duplicate protection: check status in list again
                val currentIdx = fixtures.indexOfFirst { it.id == fixture.id }
                if (currentIdx == -1 || fixtures[currentIdx].status != FixtureStatus.UPCOMING) continue

                val (updatedFixture, result) = simulationEngine.simulateMatch(fixture)
                fixtures[currentIdx] = updatedFixture
                saveMatchResult(result, isUserPlayed = false)
                stateChanged = true
            }
        }

        // Recalculate Points Table strictly for league fixtures
        var pointsTable = NrrCalculator.calculatePointsTable(fixtures)

        // ----------------------------------------------------
        // PHASE 2: LEAGUE COMPLETION & SEMI-FINALS CREATION
        // ----------------------------------------------------
        val allLeagueCompleted = fixtures.filter { it.stage == TournamentStage.LEAGUE }
            .all { it.status == FixtureStatus.COMPLETED }

        if (allLeagueCompleted && sf1Id == null) {
            stage = TournamentStage.SEMI_FINALS
            val top4 = pointsTable.take(4).map { it.teamId }
            if (top4.size >= 4) {
                val (sf1, sf2) = FixtureGenerator.createSemiFinalFixtures(tournament.format, top4)
                // Add with duplicate protection
                if (fixtures.none { it.id == sf1.id }) fixtures.add(sf1)
                if (fixtures.none { it.id == sf2.id }) fixtures.add(sf2)
                sf1Id = sf1.id
                sf2Id = sf2.id
                stateChanged = true
            }
        }

        // ----------------------------------------------------
        // PHASE 3: SEMI-FINALS SIMULATION (CPU VS CPU)
        // ----------------------------------------------------
        if (stage == TournamentStage.SEMI_FINALS || stage == TournamentStage.FINAL || stage == TournamentStage.CHAMPION) {
            val pendingCpuSemiFinals = fixtures.filter {
                it.stage == TournamentStage.SEMI_FINALS &&
                it.status == FixtureStatus.UPCOMING &&
                !it.involvesTeam(userTeamId)
            }

            for (fixture in pendingCpuSemiFinals) {
                val currentIdx = fixtures.indexOfFirst { it.id == fixture.id }
                if (currentIdx == -1 || fixtures[currentIdx].status != FixtureStatus.UPCOMING) continue

                val (updatedFixture, result) = simulationEngine.simulateMatch(fixture, MatchType.WORLD_CUP_SEMI_FINAL)
                fixtures[currentIdx] = updatedFixture
                saveMatchResult(result, isUserPlayed = false)
                stateChanged = true
            }
        }

        // ----------------------------------------------------
        // PHASE 4: FINAL FIXTURE CREATION
        // ----------------------------------------------------
        val sfFixtures = fixtures.filter { it.stage == TournamentStage.SEMI_FINALS }
        val allSfCompleted = sfFixtures.size == 2 && sfFixtures.all { it.status == FixtureStatus.COMPLETED }

        if (allSfCompleted && finalId == null) {
            val sf1Winner = fixtures.firstOrNull { it.id == sf1Id }?.winnerTeamId
            val sf2Winner = fixtures.firstOrNull { it.id == sf2Id }?.winnerTeamId
            if (sf1Winner != null && sf2Winner != null) {
                val finalFixture = FixtureGenerator.createFinalFixture(tournament.format, sf1Winner, sf2Winner)
                if (fixtures.none { it.id == finalFixture.id }) {
                    fixtures.add(finalFixture)
                }
                finalId = finalFixture.id
                stage = TournamentStage.FINAL
                stateChanged = true
            }
        }

        // ----------------------------------------------------
        // PHASE 5: FINAL SIMULATION (IF BOTH FINALISTS ARE CPU)
        // ----------------------------------------------------
        if (stage == TournamentStage.FINAL || stage == TournamentStage.CHAMPION) {
            val finalFixture = fixtures.firstOrNull { it.id == finalId }
            if (finalFixture != null && finalFixture.status == FixtureStatus.UPCOMING && !finalFixture.involvesTeam(userTeamId)) {
                val currentIdx = fixtures.indexOfFirst { it.id == finalFixture.id }
                if (currentIdx != -1 && fixtures[currentIdx].status == FixtureStatus.UPCOMING) {
                    val (updatedFinal, result) = simulationEngine.simulateMatch(finalFixture, MatchType.WORLD_CUP_FINAL)
                    fixtures[currentIdx] = updatedFinal
                    saveMatchResult(result, isUserPlayed = false)
                    stateChanged = true
                }
            }

            // Check if Final is completed and declare champion
            val completedFinal = fixtures.firstOrNull { it.id == finalId && it.status == FixtureStatus.COMPLETED }
            if (completedFinal != null && championId == null) {
                championId = completedFinal.winnerTeamId
                runnerUpId = if (completedFinal.winnerTeamId == completedFinal.team1Id) completedFinal.team2Id else completedFinal.team1Id
                stage = TournamentStage.CHAMPION
                stateChanged = true

                if (championId != null && championId.equals(userTeamId, ignoreCase = true)) {
                    updateCareerStats { it.copy(worldCupWins = it.worldCupWins + 1) }
                }
            }
        }

        if (stateChanged) {
            val updatedTournament = tournament.copy(
                stage = stage,
                fixtures = fixtures,
                pointsTable = pointsTable,
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
    }

    fun simulateFixture(fixtureId: String): MatchResult? {
        val currentTournament = _tournamentState.value ?: return null
        val fixture = currentTournament.fixtures.firstOrNull { it.id == fixtureId } ?: return null
        if (fixture.status == FixtureStatus.COMPLETED) return null

        val (updatedFixture, result) = simulationEngine.simulateMatch(fixture)
        updateTournamentAfterMatch(result)
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
        autoProgressTournament()
    }

    fun resetTournament() {
        _tournamentState.value = null
        preferencesManager.clearTournament()
    }

    // Match History & Career Stats
    fun saveMatchResult(result: MatchResult, isUserPlayed: Boolean = false) {
        preferencesManager.saveMatchToHistory(result)
        _matchHistory.value = preferencesManager.getMatchHistory()

        // Only update player career stats if the user actually played the match
        if (isUserPlayed) {
            val userTeamId = _tournamentState.value?.userTeamId ?: result.team1.id
            val userTeam = if (result.team1.id.equals(userTeamId, ignoreCase = true)) result.team1
                           else if (result.team2.id.equals(userTeamId, ignoreCase = true)) result.team2
                           else result.team1

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
