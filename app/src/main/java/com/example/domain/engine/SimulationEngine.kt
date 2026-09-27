package com.example.domain.engine

import com.example.data.model.*
import kotlin.random.Random

class SimulationEngine(
    private val handCricketEngine: HandCricketEngine = HandCricketEngine(),
    private val cpuAiEngine: CpuAiEngine = CpuAiEngine()
) {

    /**
     * Simulates a full CPU vs CPU match adhering strictly to the match format and wicket limits.
     */
    fun simulateMatch(
        fixture: Fixture,
        matchType: MatchType = when (fixture.stage) {
            TournamentStage.LEAGUE -> MatchType.WORLD_CUP_LEAGUE
            TournamentStage.SEMI_FINALS -> MatchType.WORLD_CUP_SEMI_FINAL
            TournamentStage.FINAL -> MatchType.WORLD_CUP_FINAL
            else -> MatchType.QUICK_MATCH
        }
    ): Pair<Fixture, MatchResult> {
        val team1 = fixture.team1
        val team2 = fixture.team2
        val format = fixture.format

        // Toss
        val tossWinner = if (Random.nextBoolean()) team1 else team2
        val tossChoice = if (Random.nextBoolean()) TossChoice.BAT else TossChoice.BOWL

        val battingFirst = if (tossWinner == team1) {
            if (tossChoice == TossChoice.BAT) team1 else team2
        } else {
            if (tossChoice == TossChoice.BAT) team2 else team1
        }
        val bowlingFirst = if (battingFirst == team1) team2 else team1

        // Innings 1
        var innings1 = handCricketEngine.createFirstInnings(battingFirst, bowlingFirst, format)
        while (!innings1.isCompleted) {
            val batChoice = Random.nextInt(1, 7)
            val bowlChoice = Random.nextInt(1, 7)
            innings1 = handCricketEngine.processBall(innings1, batChoice, bowlChoice)
        }

        // Innings 2
        var innings2 = handCricketEngine.createSecondInnings(innings1, bowlingFirst, battingFirst, format)
        while (!innings2.isCompleted) {
            val batChoice = Random.nextInt(1, 7)
            val bowlChoice = Random.nextInt(1, 7)
            innings2 = handCricketEngine.processBall(innings2, batChoice, bowlChoice)
        }

        var superOver1: InningsState? = null
        var superOver2: InningsState? = null

        // If knockout match and tie, simulate Super Over until winner found
        if (innings1.runs == innings2.runs && (matchType == MatchType.WORLD_CUP_SEMI_FINAL || matchType == MatchType.WORLD_CUP_FINAL)) {
            var soWinnerFound = false
            while (!soWinnerFound) {
                var so1 = handCricketEngine.createFirstInnings(battingFirst, bowlingFirst, MatchFormat.ONE_OVER)
                while (!so1.isCompleted) {
                    so1 = handCricketEngine.processBall(so1, Random.nextInt(1, 7), Random.nextInt(1, 7))
                }
                var so2 = handCricketEngine.createSecondInnings(so1, bowlingFirst, battingFirst, MatchFormat.ONE_OVER)
                while (!so2.isCompleted) {
                    so2 = handCricketEngine.processBall(so2, Random.nextInt(1, 7), Random.nextInt(1, 7))
                }

                if (so1.runs != so2.runs) {
                    soWinnerFound = true
                    superOver1 = so1
                    superOver2 = so2
                }
            }
        }

        val result = handCricketEngine.determineMatchResult(
            matchId = fixture.id,
            matchType = matchType,
            format = format,
            team1 = team1,
            team2 = team2,
            innings1 = innings1,
            innings2 = innings2,
            superOver1 = superOver1,
            superOver2 = superOver2
        )

        val team1IsInnings1 = (innings1.battingTeam.id == team1.id)
        val team1Runs = if (team1IsInnings1) innings1.runs else innings2.runs
        val team1Wickets = if (team1IsInnings1) innings1.wickets else innings2.wickets
        val team1Balls = if (team1IsInnings1) innings1.ballsBowled else innings2.ballsBowled

        val team2Runs = if (!team1IsInnings1) innings1.runs else innings2.runs
        val team2Wickets = if (!team1IsInnings1) innings1.wickets else innings2.wickets
        val team2Balls = if (!team1IsInnings1) innings1.ballsBowled else innings2.ballsBowled

        val updatedFixture = fixture.copy(
            status = FixtureStatus.COMPLETED,
            resultSummary = result.marginText,
            winnerTeamId = result.winner?.id,
            isTie = result.isTie,
            team1Runs = team1Runs,
            team1Wickets = team1Wickets,
            team1Balls = team1Balls,
            team2Runs = team2Runs,
            team2Wickets = team2Wickets,
            team2Balls = team2Balls
        )

        return Pair(updatedFixture, result)
    }
}
