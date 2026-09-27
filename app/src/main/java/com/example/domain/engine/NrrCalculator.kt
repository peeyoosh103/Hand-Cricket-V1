package com.example.domain.engine

import com.example.data.model.Fixture
import com.example.data.model.FixtureStatus
import com.example.data.model.PointsTableRow
import com.example.data.model.Team
import com.example.data.model.TournamentStage

object NrrCalculator {

    /**
     * Calculates the entire Points Table from completed league fixtures.
     * NRR formula: (Total Runs Scored / Total Overs Faced) - (Total Runs Conceded / Total Overs Bowled)
     * If all-out before max overs, in standard cricket rules the full quota of overs is used for NRR.
     */
    fun calculatePointsTable(
        fixtures: List<Fixture>,
        teams: List<Team> = Team.ALL_TEAMS
    ): List<PointsTableRow> {
        val completedLeagueFixtures = fixtures.filter { 
            it.stage == TournamentStage.LEAGUE && it.status == FixtureStatus.COMPLETED 
        }

        val rows = teams.map { team ->
            var played = 0
            var won = 0
            var lost = 0
            var tied = 0
            var points = 0
            var runsScored = 0
            var ballsFaced = 0
            var runsConceded = 0
            var ballsBowled = 0

            for (fixture in completedLeagueFixtures) {
                if (!fixture.involvesTeam(team.id)) continue

                played++
                val isTeam1 = fixture.team1Id.equals(team.id, ignoreCase = true)
                val myRuns = (if (isTeam1) fixture.team1Runs else fixture.team2Runs) ?: 0
                val myWickets = (if (isTeam1) fixture.team1Wickets else fixture.team2Wickets) ?: 0
                val myBalls = (if (isTeam1) fixture.team1Balls else fixture.team2Balls) ?: (fixture.format.overs * 6)

                val oppRuns = (if (isTeam1) fixture.team2Runs else fixture.team1Runs) ?: 0
                val oppWickets = (if (isTeam1) fixture.team2Wickets else fixture.team1Wickets) ?: 0
                val oppBalls = (if (isTeam1) fixture.team2Balls else fixture.team1Balls) ?: (fixture.format.overs * 6)

                // Effective overs for NRR: if a team was bowled all out (wickets == maxWickets), count full quota
                val effectiveBallsFaced = if (myWickets >= fixture.format.maxWickets) {
                    fixture.format.overs * 6
                } else {
                    myBalls
                }

                val effectiveBallsBowled = if (oppWickets >= fixture.format.maxWickets) {
                    fixture.format.overs * 6
                } else {
                    oppBalls
                }

                runsScored += myRuns
                ballsFaced += effectiveBallsFaced
                runsConceded += oppRuns
                ballsBowled += effectiveBallsBowled

                if (fixture.isTie) {
                    tied++
                    points += 1
                } else if (fixture.winnerTeamId.equals(team.id, ignoreCase = true)) {
                    won++
                    points += 2
                } else {
                    lost++
                }
            }

            val oversFaced = (ballsFaced / 6) + ((ballsFaced % 6) / 6.0)
            val oversBowled = (ballsBowled / 6) + ((ballsBowled % 6) / 6.0)

            val forRunRate = if (oversFaced > 0) runsScored / oversFaced else 0.0
            val againstRunRate = if (oversBowled > 0) runsConceded / oversBowled else 0.0
            val nrr = forRunRate - againstRunRate

            PointsTableRow(
                teamId = team.id,
                played = played,
                won = won,
                lost = lost,
                tied = tied,
                points = points,
                runsScored = runsScored,
                ballsFaced = ballsFaced,
                runsConceded = runsConceded,
                ballsBowled = ballsBowled,
                nrr = Math.round(nrr * 1000.0) / 1000.0
            )
        }

        // Sort by Points (descending), then NRR (descending), then Won (descending), then Team ranking
        return rows.sortedWith(
            compareByDescending<PointsTableRow> { it.points }
                .thenByDescending { it.nrr }
                .thenByDescending { it.won }
                .thenBy { it.team.worldRanking }
        )
    }
}
