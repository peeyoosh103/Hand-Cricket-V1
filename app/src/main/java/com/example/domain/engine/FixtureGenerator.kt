package com.example.domain.engine

import com.example.data.model.Fixture
import com.example.data.model.FixtureStatus
import com.example.data.model.MatchFormat
import com.example.data.model.Team
import com.example.data.model.TournamentStage

object FixtureGenerator {

    /**
     * Generates a 10-team single round robin schedule (45 matches in 9 rounds).
     * Uses Berger tournament scheduling algorithm to ensure every team plays exactly once per round.
     */
    fun generateLeagueFixtures(
        format: MatchFormat,
        teams: List<Team> = Team.ALL_TEAMS
    ): List<Fixture> {
        val numTeams = teams.size // 10
        val teamIds = teams.map { it.id }.toMutableList()
        val fixtures = mutableListOf<Fixture>()
        var matchCounter = 1

        val rounds = numTeams - 1 // 9 rounds
        val matchesPerRound = numTeams / 2 // 5 matches per round

        val rotation = teamIds.toMutableList()

        for (round in 1..rounds) {
            for (match in 0 until matchesPerRound) {
                val homeIndex = match
                val awayIndex = numTeams - 1 - match

                val team1Id = rotation[homeIndex]
                val team2Id = rotation[awayIndex]

                val fixtureId = "WC_M_${matchCounter.toString().padStart(2, '0')}"
                fixtures.add(
                    Fixture(
                        id = fixtureId,
                        matchNumber = matchCounter,
                        round = round,
                        team1Id = team1Id,
                        team2Id = team2Id,
                        format = format,
                        status = FixtureStatus.UPCOMING,
                        stage = TournamentStage.LEAGUE,
                        stageMatchName = "League Match $matchCounter (Round $round)"
                    )
                )
                matchCounter++
            }

            // Round robin rotation (keep 1st element fixed, rotate others)
            val fixed = rotation[0]
            val last = rotation.removeAt(rotation.size - 1)
            rotation.add(1, last)
        }

        return fixtures
    }

    /**
     * Creates Semi-Final 1 and Semi-Final 2 fixtures based on top 4 positions in the points table.
     */
    fun createSemiFinalFixtures(
        format: MatchFormat,
        top4TeamIds: List<String>
    ): Pair<Fixture, Fixture> {
        require(top4TeamIds.size >= 4) { "At least 4 teams are required for semi-finals" }

        val sf1 = Fixture(
            id = "WC_SF_01",
            matchNumber = 46,
            round = 10,
            team1Id = top4TeamIds[0], // 1st Place
            team2Id = top4TeamIds[3], // 4th Place
            format = format,
            status = FixtureStatus.UPCOMING,
            stage = TournamentStage.SEMI_FINALS,
            stageMatchName = "Semi-Final 1: 1st vs 4th"
        )

        val sf2 = Fixture(
            id = "WC_SF_02",
            matchNumber = 47,
            round = 10,
            team1Id = top4TeamIds[1], // 2nd Place
            team2Id = top4TeamIds[2], // 3rd Place
            format = format,
            status = FixtureStatus.UPCOMING,
            stage = TournamentStage.SEMI_FINALS,
            stageMatchName = "Semi-Final 2: 2nd vs 3rd"
        )

        return Pair(sf1, sf2)
    }

    /**
     * Creates Final fixture from the winners of Semi-Final 1 and Semi-Final 2.
     */
    fun createFinalFixture(
        format: MatchFormat,
        sf1WinnerId: String,
        sf2WinnerId: String
    ): Fixture {
        return Fixture(
            id = "WC_FINAL",
            matchNumber = 48,
            round = 11,
            team1Id = sf1WinnerId,
            team2Id = sf2WinnerId,
            format = format,
            status = FixtureStatus.UPCOMING,
            stage = TournamentStage.FINAL,
            stageMatchName = "WORLD CUP FINAL"
        )
    }
}
