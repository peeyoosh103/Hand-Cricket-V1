package com.example

import com.example.data.local.PreferencesManager
import com.example.data.model.*
import com.example.data.repository.GameRepository
import com.example.domain.engine.FixtureGenerator
import com.example.domain.engine.HandCricketEngine
import com.example.domain.engine.NrrCalculator
import com.example.domain.engine.SimulationEngine
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider
import android.content.Context

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    private val engine = HandCricketEngine()
    private val simEngine = SimulationEngine()
    private val india = Team.ALL_TEAMS[0] // IND
    private val australia = Team.ALL_TEAMS[1] // AUS

    @Test
    fun testWicketLimit_OneOver_MaxOneWicket() {
        var innings = engine.createFirstInnings(india, australia, MatchFormat.ONE_OVER)
        assertEquals(1, innings.maxWickets)
        assertEquals(6, innings.maxBalls)

        innings = engine.processBall(innings, 4, 4)
        assertEquals(1, innings.wickets)
        assertEquals(0, innings.runs)
        assertTrue("Innings must complete on 1st wicket in 1-Over match", innings.isCompleted)
    }

    @Test
    fun testWicketLimit_TwoOvers_MaxOneWicket() {
        var innings = engine.createFirstInnings(india, australia, MatchFormat.TWO_OVERS)
        assertEquals(1, innings.maxWickets)
        assertEquals(12, innings.maxBalls)

        innings = engine.processBall(innings, 6, 2)
        assertEquals(6, innings.runs)
        assertEquals(0, innings.wickets)
        assertFalse(innings.isCompleted)

        innings = engine.processBall(innings, 3, 3)
        assertEquals(6, innings.runs)
        assertEquals(1, innings.wickets)
        assertTrue("Innings must complete on 1st wicket in 2-Over match", innings.isCompleted)
    }

    @Test
    fun testWicketLimit_FiveOvers_MaxThreeWickets() {
        var innings = engine.createFirstInnings(india, australia, MatchFormat.FIVE_OVERS)
        assertEquals(3, innings.maxWickets)
        assertEquals(30, innings.maxBalls)

        innings = engine.processBall(innings, 2, 2)
        assertEquals(1, innings.wickets)
        assertFalse(innings.isCompleted)

        innings = engine.processBall(innings, 4, 4)
        assertEquals(2, innings.wickets)
        assertFalse(innings.isCompleted)

        innings = engine.processBall(innings, 6, 6)
        assertEquals(3, innings.wickets)
        assertTrue("Innings must complete on 3rd wicket in 5-Over match", innings.isCompleted)
    }

    @Test
    fun testWicketLimit_TenOvers_MaxSixWickets() {
        var innings = engine.createFirstInnings(india, australia, MatchFormat.TEN_OVERS)
        assertEquals(6, innings.maxWickets)
        assertEquals(60, innings.maxBalls)

        innings = engine.processBall(innings, 6, 1)
        innings = engine.processBall(innings, 4, 2)
        assertEquals(10, innings.runs)

        for (i in 1..6) {
            innings = engine.processBall(innings, i, i)
        }
        assertEquals(6, innings.wickets)
        assertTrue("Innings must complete on 6th wicket in 10-Over match", innings.isCompleted)
    }

    @Test
    fun testTargetChaseCompletion() {
        var inn1 = engine.createFirstInnings(india, australia, MatchFormat.FIVE_OVERS)
        inn1 = engine.processBall(inn1, 6, 1)
        inn1 = engine.processBall(inn1, 6, 2)
        inn1 = engine.processBall(inn1, 3, 4)
        assertEquals(15, inn1.runs)

        var inn2 = engine.createSecondInnings(inn1, australia, india, MatchFormat.FIVE_OVERS)
        assertEquals(16, inn2.target)

        inn2 = engine.processBall(inn2, 6, 1)
        inn2 = engine.processBall(inn2, 6, 2)
        assertFalse(inn2.isCompleted)

        inn2 = engine.processBall(inn2, 4, 3)
        assertEquals(16, inn2.runs)
        assertTrue("Innings must finish when target is achieved", inn2.isCompleted)

        val result = engine.determineMatchResult(
            matchId = "M1",
            matchType = MatchType.QUICK_MATCH,
            format = MatchFormat.FIVE_OVERS,
            team1 = india,
            team2 = australia,
            innings1 = inn1,
            innings2 = inn2
        )
        assertEquals(australia.id, result.winner?.id)
        assertFalse(result.isTie)
    }

    @Test
    fun testWorldCupLeagueFixtureGeneration() {
        val fixtures = FixtureGenerator.generateLeagueFixtures(MatchFormat.FIVE_OVERS)
        assertEquals(45, fixtures.size)

        for (team in Team.ALL_TEAMS) {
            val teamMatches = fixtures.filter { it.involvesTeam(team.id) }
            assertEquals("Team ${team.id} must play exactly 9 matches", 9, teamMatches.size)
        }

        for (round in 1..9) {
            val roundMatches = fixtures.filter { it.round == round }
            assertEquals(5, roundMatches.size)
        }
    }

    @Test
    fun testPointsTableAndNrrCalculation() {
        val fixtures = FixtureGenerator.generateLeagueFixtures(MatchFormat.FIVE_OVERS)
        val initialTable = NrrCalculator.calculatePointsTable(fixtures)
        assertEquals(10, initialTable.size)

        val simulatedFixtures = fixtures.toMutableList()
        for (i in 0 until 5) {
            val (updated, _) = simEngine.simulateMatch(simulatedFixtures[i])
            simulatedFixtures[i] = updated
        }

        val updatedTable = NrrCalculator.calculatePointsTable(simulatedFixtures)
        assertEquals(10, updatedTable.size)
        assertTrue(updatedTable[0].points >= updatedTable.last().points)
    }

    /**
     * Test the exact requirement:
     * When player completes each India match (Round 1 to Round 9):
     * - CPU matches for that round are automatically simulated.
     * - Every team's match count progresses consistently.
     * - No duplicates exist.
     * - All 45 league matches finish after Round 9.
     * - Semi-Finals are created and CPU Semi-Final is auto-simulated.
     */
    @Test
    fun testAutomaticLeagueAndKnockoutProgression() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefsManager = PreferencesManager(context)
        prefsManager.clearTournament()
        val repo = GameRepository(prefsManager)

        // 1. Create Tournament with India
        val tournament = repo.createNewTournament(india.id, MatchFormat.FIVE_OVERS)
        assertEquals(45, tournament.fixtures.size)
        assertEquals(TournamentStage.LEAGUE, tournament.stage)

        // 2. Play all 9 user matches sequentially
        for (round in 1..9) {
            val currentTournament = repo.tournamentState.value!!
            val userFixture = currentTournament.fixtures.first {
                it.stage == TournamentStage.LEAGUE &&
                it.involvesTeam(india.id) &&
                it.status == FixtureStatus.UPCOMING
            }
            assertEquals(round, userFixture.round)

            // Simulate India playing and winning
            val opponent = userFixture.getOpponentOf(india.id)
            var inn1 = engine.createFirstInnings(india, opponent, MatchFormat.FIVE_OVERS)
            inn1 = engine.processBall(inn1, 6, 1) // 6 runs
            inn1 = engine.processBall(inn1, 6, 2) // 12 runs
            inn1 = engine.processBall(inn1, 6, 3) // 18 runs
            inn1 = engine.processBall(inn1, 4, 1) // 22 runs
            inn1 = engine.processBall(inn1, 5, 5) // Wicket 1
            inn1 = engine.processBall(inn1, 4, 4) // Wicket 2
            inn1 = engine.processBall(inn1, 3, 3) // Wicket 3 -> All out (limit 3 wkts)

            var inn2 = engine.createSecondInnings(inn1, opponent, india, MatchFormat.FIVE_OVERS)
            inn2 = engine.processBall(inn2, 1, 2)
            inn2 = engine.processBall(inn2, 3, 3) // Wicket 1
            inn2 = engine.processBall(inn2, 4, 4) // Wicket 2
            inn2 = engine.processBall(inn2, 2, 2) // Wicket 3 -> All out

            val playerResult = engine.determineMatchResult(
                matchId = userFixture.id,
                matchType = MatchType.WORLD_CUP_LEAGUE,
                format = MatchFormat.FIVE_OVERS,
                team1 = india,
                team2 = opponent,
                innings1 = inn1,
                innings2 = inn2
            )

            // Complete match
            repo.updateTournamentAfterMatch(playerResult)

            // VERIFY TOURNAMENT PROGRESSION:
            val afterMatchTournament = repo.tournamentState.value!!
            val completedLeague = afterMatchTournament.fixtures.filter {
                it.stage == TournamentStage.LEAGUE && it.status == FixtureStatus.COMPLETED
            }

            // Total completed league matches should be round * 5
            assertEquals("Round $round must have ${round * 5} completed matches", round * 5, completedLeague.size)

            // Verify every single team in points table has played == round
            val pointsTable = afterMatchTournament.pointsTable
            assertEquals(10, pointsTable.size)
            for (row in pointsTable) {
                assertEquals("Team ${row.teamId} in round $round must have played == $round", round, row.played)
            }
        }

        // 3. After Round 9:
        val leagueDoneTournament = repo.tournamentState.value!!
        val completedLeagueTotal = leagueDoneTournament.fixtures.filter {
            it.stage == TournamentStage.LEAGUE && it.status == FixtureStatus.COMPLETED
        }
        assertEquals(45, completedLeagueTotal.size)

        // Semi-Finals must be created!
        assertTrue(leagueDoneTournament.stage == TournamentStage.SEMI_FINALS || leagueDoneTournament.stage == TournamentStage.FINAL || leagueDoneTournament.stage == TournamentStage.CHAMPION)
        assertNotNull(leagueDoneTournament.semiFinal1FixtureId)
        assertNotNull(leagueDoneTournament.semiFinal2FixtureId)

        // Check semi-finals: India won all 9 matches, so India is 1st and plays in SF1
        val sf1 = leagueDoneTournament.fixtures.first { it.id == leagueDoneTournament.semiFinal1FixtureId }
        val sf2 = leagueDoneTournament.fixtures.first { it.id == leagueDoneTournament.semiFinal2FixtureId }

        assertTrue("India must be in SF1 (as #1 team)", sf1.involvesTeam(india.id))
        assertFalse("India must NOT be in SF2", sf2.involvesTeam(india.id))

        // SF2 is CPU vs CPU, so it must be AUTOMATICALLY SIMULATED!
        assertEquals("CPU Semi-Final 2 must be automatically simulated and COMPLETED", FixtureStatus.COMPLETED, sf2.status)
        assertNotNull(sf2.winnerTeamId)

        // SF1 (India's match) remains UPCOMING for manual play!
        assertEquals("Player's Semi-Final 1 must remain UPCOMING for manual play", FixtureStatus.UPCOMING, sf1.status)

        // Now simulate India playing and winning SF1:
        val sfOpponent = sf1.getOpponentOf(india.id)
        var sfInn1 = engine.createFirstInnings(india, sfOpponent, MatchFormat.FIVE_OVERS)
        sfInn1 = engine.processBall(sfInn1, 6, 1)
        sfInn1 = engine.processBall(sfInn1, 6, 2)
        sfInn1 = engine.processBall(sfInn1, 1, 1) // Wicket 1
        sfInn1 = engine.processBall(sfInn1, 2, 2) // Wicket 2
        sfInn1 = engine.processBall(sfInn1, 3, 3) // Wicket 3 -> All out (12 runs)

        var sfInn2 = engine.createSecondInnings(sfInn1, sfOpponent, india, MatchFormat.FIVE_OVERS)
        sfInn2 = engine.processBall(sfInn2, 1, 1) // Wkt 1
        sfInn2 = engine.processBall(sfInn2, 2, 2) // Wkt 2
        sfInn2 = engine.processBall(sfInn2, 3, 3) // Wkt 3 -> All out (0 runs)

        val sfResult = engine.determineMatchResult(
            matchId = sf1.id,
            matchType = MatchType.WORLD_CUP_SEMI_FINAL,
            format = MatchFormat.FIVE_OVERS,
            team1 = india,
            team2 = sfOpponent,
            innings1 = sfInn1,
            innings2 = sfInn2
        )
        repo.updateTournamentAfterMatch(sfResult)

        // After SF1 completed:
        val finalsTournament = repo.tournamentState.value!!
        assertEquals(TournamentStage.FINAL, finalsTournament.stage)
        assertNotNull(finalsTournament.finalFixtureId)

        val finalFixture = finalsTournament.fixtures.first { it.id == finalsTournament.finalFixtureId }
        assertTrue("Final must involve India", finalFixture.involvesTeam(india.id))
        assertEquals("Final must be UPCOMING for manual play", FixtureStatus.UPCOMING, finalFixture.status)
        assertEquals(sf2.winnerTeamId, finalFixture.getOpponentOf(india.id).id)

        // Now India plays and wins the Final:
        val finalOpponent = finalFixture.getOpponentOf(india.id)
        var fInn1 = engine.createFirstInnings(india, finalOpponent, MatchFormat.FIVE_OVERS)
        fInn1 = engine.processBall(fInn1, 6, 1) // 6 runs
        fInn1 = engine.processBall(fInn1, 1, 1)
        fInn1 = engine.processBall(fInn1, 2, 2)
        fInn1 = engine.processBall(fInn1, 3, 3) // All out

        var fInn2 = engine.createSecondInnings(fInn1, finalOpponent, india, MatchFormat.FIVE_OVERS)
        fInn2 = engine.processBall(fInn2, 1, 1)
        fInn2 = engine.processBall(fInn2, 2, 2)
        fInn2 = engine.processBall(fInn2, 3, 3) // All out (0 runs)

        val finalResult = engine.determineMatchResult(
            matchId = finalFixture.id,
            matchType = MatchType.WORLD_CUP_FINAL,
            format = MatchFormat.FIVE_OVERS,
            team1 = india,
            team2 = finalOpponent,
            innings1 = fInn1,
            innings2 = fInn2
        )
        repo.updateTournamentAfterMatch(finalResult)

        // After Final:
        val championTournament = repo.tournamentState.value!!
        assertEquals(TournamentStage.CHAMPION, championTournament.stage)
        assertEquals(india.id, championTournament.championTeamId)
        assertEquals(finalOpponent.id, championTournament.runnerUpTeamId)
        assertEquals(1, repo.careerStats.value.worldCupWins)
    }

    /**
     * Test when player does NOT qualify for Top 4:
     * Both Semi-Finals and Final must be automatically simulated!
     */
    @Test
    fun testKnockoutProgression_WhenPlayerDoesNotQualify() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefsManager = PreferencesManager(context)
        prefsManager.clearTournament()
        val repo = GameRepository(prefsManager)

        // 1. Create Tournament with West Indies (rank 10)
        val wi = Team.ALL_TEAMS.first { it.id == "WI" }
        repo.createNewTournament(wi.id, MatchFormat.FIVE_OVERS)

        // 2. Play all 9 WI matches and make WI lose every match (0 runs, all out immediately)
        for (round in 1..9) {
            val currentTournament = repo.tournamentState.value!!
            val userFixture = currentTournament.fixtures.first {
                it.stage == TournamentStage.LEAGUE &&
                it.involvesTeam(wi.id) &&
                it.status == FixtureStatus.UPCOMING
            }
            val opponent = userFixture.getOpponentOf(wi.id)

            // WI scores 0 runs (3 wickets)
            var inn1 = engine.createFirstInnings(wi, opponent, MatchFormat.FIVE_OVERS)
            inn1 = engine.processBall(inn1, 1, 1)
            inn1 = engine.processBall(inn1, 2, 2)
            inn1 = engine.processBall(inn1, 3, 3)

            // Opponent hits 1 run (target 1 achieved)
            var inn2 = engine.createSecondInnings(inn1, opponent, wi, MatchFormat.FIVE_OVERS)
            inn2 = engine.processBall(inn2, 1, 2) // 1 run -> Target achieved

            val lossResult = engine.determineMatchResult(
                matchId = userFixture.id,
                matchType = MatchType.WORLD_CUP_LEAGUE,
                format = MatchFormat.FIVE_OVERS,
                team1 = wi,
                team2 = opponent,
                innings1 = inn1,
                innings2 = inn2
            )
            repo.updateTournamentAfterMatch(lossResult)
        }

        val afterTournament = repo.tournamentState.value!!
        // Since WI has 0 points and did not qualify for Top 4:
        // Both SF1 and SF2 are CPU vs CPU -> BOTH auto simulated!
        // Final is CPU vs CPU -> auto simulated!
        // Tournament automatically reaches CHAMPION stage!
        assertEquals(TournamentStage.CHAMPION, afterTournament.stage)
        assertNotNull(afterTournament.championTeamId)
        assertNotNull(afterTournament.runnerUpTeamId)
        assertNotEquals(wi.id, afterTournament.championTeamId)

        // Verify total completed fixtures is 45 (league) + 2 (SF) + 1 (Final) = 48!
        val completedCount = afterTournament.fixtures.count { it.status == FixtureStatus.COMPLETED }
        assertEquals(48, completedCount)
    }
}
