package com.example.data.model

enum class TournamentStage {
    LEAGUE,
    SEMI_FINALS,
    FINAL,
    CHAMPION
}

enum class FixtureStatus {
    UPCOMING,
    IN_PROGRESS,
    COMPLETED
}

data class Fixture(
    val id: String,
    val matchNumber: Int,
    val round: Int,
    val team1Id: String,
    val team2Id: String,
    val format: MatchFormat,
    val status: FixtureStatus = FixtureStatus.UPCOMING,
    val resultSummary: String? = null,
    val winnerTeamId: String? = null,
    val isTie: Boolean = false,
    val team1Runs: Int? = null,
    val team1Wickets: Int? = null,
    val team1Balls: Int? = null,
    val team2Runs: Int? = null,
    val team2Wickets: Int? = null,
    val team2Balls: Int? = null,
    val stage: TournamentStage = TournamentStage.LEAGUE,
    val stageMatchName: String? = null // e.g. "Semi-Final 1", "Final"
) {
    val team1: Team get() = Team.getTeamById(team1Id)
    val team2: Team get() = Team.getTeamById(team2Id)

    fun involvesTeam(teamId: String): Boolean {
        return team1Id.equals(teamId, ignoreCase = true) || team2Id.equals(teamId, ignoreCase = true)
    }

    fun getOpponentOf(teamId: String): Team {
        return if (team1Id.equals(teamId, ignoreCase = true)) team2 else team1
    }
}

data class PointsTableRow(
    val teamId: String,
    val played: Int = 0,
    val won: Int = 0,
    val lost: Int = 0,
    val tied: Int = 0,
    val points: Int = 0,
    val runsScored: Int = 0,
    val ballsFaced: Int = 0,
    val runsConceded: Int = 0,
    val ballsBowled: Int = 0,
    val nrr: Double = 0.0
) {
    val team: Team get() = Team.getTeamById(teamId)

    val oversFacedFormatted: String
        get() = "${ballsFaced / 6}.${ballsFaced % 6}"

    val oversBowledFormatted: String
        get() = "${ballsBowled / 6}.${ballsBowled % 6}"
}

data class TournamentState(
    val tournamentId: String,
    val userTeamId: String,
    val format: MatchFormat,
    val stage: TournamentStage = TournamentStage.LEAGUE,
    val currentMatchIndex: Int = 0,
    val fixtures: List<Fixture> = emptyList(),
    val pointsTable: List<PointsTableRow> = emptyList(),
    val semiFinal1FixtureId: String? = null,
    val semiFinal2FixtureId: String? = null,
    val finalFixtureId: String? = null,
    val championTeamId: String? = null,
    val runnerUpTeamId: String? = null,
    val playerOfTournament: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val userTeam: Team get() = Team.getTeamById(userTeamId)
    val championTeam: Team? get() = championTeamId?.let { Team.getTeamById(it) }
    val runnerUpTeam: Team? get() = runnerUpTeamId?.let { Team.getTeamById(it) }

    val completedFixturesCount: Int get() = fixtures.count { it.status == FixtureStatus.COMPLETED }
    val totalLeagueFixturesCount: Int get() = fixtures.count { it.stage == TournamentStage.LEAGUE }
    
    val nextUserFixture: Fixture?
        get() = fixtures.firstOrNull { 
            it.status == FixtureStatus.UPCOMING && it.involvesTeam(userTeamId) 
        }

    val isLeagueFinished: Boolean
        get() = fixtures.filter { it.stage == TournamentStage.LEAGUE }.all { it.status == FixtureStatus.COMPLETED }

    val top4Teams: List<PointsTableRow>
        get() = pointsTable.take(4)
}
