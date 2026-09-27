package com.example

import com.example.data.model.*
import com.example.domain.engine.FixtureGenerator
import com.example.domain.engine.HandCricketEngine
import com.example.domain.engine.NrrCalculator
import com.example.domain.engine.SimulationEngine
import org.junit.Assert.*
import org.junit.Test

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

        // Batsman plays 4, Bowler plays 4 -> WICKET (1st wicket)
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

        // Ball 1: 6 runs (6 vs 2)
        innings = engine.processBall(innings, 6, 2)
        assertEquals(6, innings.runs)
        assertEquals(0, innings.wickets)
        assertFalse(innings.isCompleted)

        // Ball 2: Wicket (3 vs 3)
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

        // 1st wicket
        innings = engine.processBall(innings, 2, 2)
        assertEquals(1, innings.wickets)
        assertFalse(innings.isCompleted)

        // 2nd wicket
        innings = engine.processBall(innings, 4, 4)
        assertEquals(2, innings.wickets)
        assertFalse(innings.isCompleted)

        // 3rd wicket -> ALL OUT
        innings = engine.processBall(innings, 6, 6)
        assertEquals(3, innings.wickets)
        assertTrue("Innings must complete on 3rd wicket in 5-Over match", innings.isCompleted)
    }

    @Test
    fun testWicketLimit_TenOvers_MaxSixWickets() {
        var innings = engine.createFirstInnings(india, australia, MatchFormat.TEN_OVERS)
        assertEquals(6, innings.maxWickets)
        assertEquals(60, innings.maxBalls)

        // Score some runs
        innings = engine.processBall(innings, 6, 1) // +6
        innings = engine.processBall(innings, 4, 2) // +4
        assertEquals(10, innings.runs)

        // 6 wickets fall
        for (i in 1..6) {
            innings = engine.processBall(innings, i, i)
        }
        assertEquals(6, innings.wickets)
        assertTrue("Innings must complete on 6th wicket in 10-Over match", innings.isCompleted)
    }

    @Test
    fun testTargetChaseCompletion() {
        // 1st Innings: India scores 15 runs
        var inn1 = engine.createFirstInnings(india, australia, MatchFormat.FIVE_OVERS)
        inn1 = engine.processBall(inn1, 6, 1) // +6
        inn1 = engine.processBall(inn1, 6, 2) // +6
        inn1 = engine.processBall(inn1, 3, 4) // +3
        assertEquals(15, inn1.runs)

        // 2nd Innings: Australia chases target 16
        var inn2 = engine.createSecondInnings(inn1, australia, india, MatchFormat.FIVE_OVERS)
        assertEquals(16, inn2.target)

        inn2 = engine.processBall(inn2, 6, 1) // 6
        inn2 = engine.processBall(inn2, 6, 2) // 12
        assertFalse(inn2.isCompleted)

        inn2 = engine.processBall(inn2, 4, 3) // 16 -> Target achieved!
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

        // Verify all 10 teams play 9 matches each
        for (team in Team.ALL_TEAMS) {
            val teamMatches = fixtures.filter { it.involvesTeam(team.id) }
            assertEquals("Team ${team.id} must play exactly 9 matches", 9, teamMatches.size)
        }

        // Verify 9 rounds with 5 matches each
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

        // Simulate 5 matches
        val simulatedFixtures = fixtures.toMutableList()
        for (i in 0 until 5) {
            val (updated, _) = simEngine.simulateMatch(simulatedFixtures[i])
            simulatedFixtures[i] = updated
        }

        val updatedTable = NrrCalculator.calculatePointsTable(simulatedFixtures)
        assertEquals(10, updatedTable.size)
        // Table should be sorted by points descending
        assertTrue(updatedTable[0].points >= updatedTable.last().points)
    }
}
