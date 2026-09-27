package com.example.data.model

enum class MatchType {
    QUICK_MATCH,
    WORLD_CUP_LEAGUE,
    WORLD_CUP_SEMI_FINAL,
    WORLD_CUP_FINAL,
    PRACTICE
}

enum class MatchStage {
    TOSS,
    INNINGS_1,
    INNINGS_BREAK,
    INNINGS_2,
    SUPER_OVER_TOSS,
    SUPER_OVER_INNINGS_1,
    SUPER_OVER_BREAK,
    SUPER_OVER_INNINGS_2,
    COMPLETED
}

enum class TossChoice {
    BAT,
    BOWL
}

data class BallEvent(
    val ballIndex: Int,
    val overNumber: Int, // 0-based over
    val ballInOver: Int, // 1 to 6
    val batsmanChoice: Int,
    val bowlerChoice: Int,
    val isWicket: Boolean,
    val runsScored: Int,
    val isBoundaryFour: Boolean = (runsScored == 4),
    val isBoundarySix: Boolean = (runsScored == 6),
    val totalRunsAfter: Int,
    val totalWicketsAfter: Int,
    val commentary: String
)

data class InningsState(
    val inningsNumber: Int,
    val battingTeam: Team,
    val bowlingTeam: Team,
    val maxOvers: Int,
    val maxWickets: Int,
    val target: Int? = null,
    val runs: Int = 0,
    val wickets: Int = 0,
    val ballsBowled: Int = 0,
    val ballEvents: List<BallEvent> = emptyList(),
    val isCompleted: Boolean = false,
    val completedReason: String? = null
) {
    val maxBalls: Int get() = maxOvers * 6
    val remainingBalls: Int get() = (maxBalls - ballsBowled).coerceAtLeast(0)
    val remainingWickets: Int get() = (maxWickets - wickets).coerceAtLeast(0)
    
    val oversFormatted: String
        get() {
            val overs = ballsBowled / 6
            val balls = ballsBowled % 6
            return "$overs.$balls"
        }

    val runRate: Double
        get() {
            if (ballsBowled == 0) return 0.0
            return (runs.toDouble() / ballsBowled) * 6.0
        }

    val requiredRuns: Int?
        get() = target?.let { (it - runs).coerceAtLeast(0) }

    val requiredRunRate: Double?
        get() {
            val req = requiredRuns ?: return null
            if (remainingBalls <= 0) return if (req == 0) 0.0 else 99.99
            return (req.toDouble() / remainingBalls) * 6.0
        }

    val countOnes: Int get() = ballEvents.count { !it.isWicket && it.runsScored == 1 }
    val countTwos: Int get() = ballEvents.count { !it.isWicket && it.runsScored == 2 }
    val countThrees: Int get() = ballEvents.count { !it.isWicket && it.runsScored == 3 }
    val countFours: Int get() = ballEvents.count { it.isBoundaryFour }
    val countFives: Int get() = ballEvents.count { !it.isWicket && it.runsScored == 5 }
    val countSixes: Int get() = ballEvents.count { it.isBoundarySix }
}

data class TossResult(
    val winner: Team,
    val isPlayerWinner: Boolean,
    val choice: TossChoice
)

data class MatchResult(
    val matchId: String,
    val matchType: MatchType,
    val format: MatchFormat,
    val team1: Team,
    val team2: Team,
    val winner: Team?,
    val isTie: Boolean,
    val marginText: String,
    val playerOfTheMatch: String,
    val innings1: InningsState,
    val innings2: InningsState,
    val superOverInnings1: InningsState? = null,
    val superOverInnings2: InningsState? = null,
    val timestamp: Long = System.currentTimeMillis()
)
