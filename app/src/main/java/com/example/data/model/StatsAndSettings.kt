package com.example.data.model

data class PlayerCareerStats(
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val ties: Int = 0,
    val totalRuns: Int = 0,
    val totalBallsFaced: Int = 0,
    val totalWicketsTaken: Int = 0,
    val totalBallsBowled: Int = 0,
    val highestScore: Int = 0,
    val foursCount: Int = 0,
    val sixesCount: Int = 0,
    val ducksCount: Int = 0,
    val bestBowlingWickets: Int = 0,
    val bestBowlingRuns: Int = 0,
    val worldCupAppearances: Int = 0,
    val worldCupWins: Int = 0
) {
    val winRatePercent: Double
        get() {
            if (matchesPlayed == 0) return 0.0
            return (wins.toDouble() / matchesPlayed) * 100.0
        }

    val battingStrikeRate: Double
        get() {
            if (totalBallsFaced == 0) return 0.0
            return (totalRuns.toDouble() / totalBallsFaced) * 100.0
        }

    val bowlingEconomy: Double
        get() {
            if (totalBallsBowled == 0) return 0.0
            return (bestBowlingRuns.toDouble() / totalBallsBowled) * 6.0
        }
}

data class TournamentStats(
    val leadingRunScorers: List<Pair<String, Int>> = emptyList(), // Team Name to Runs
    val highestMatchScores: List<Pair<String, Int>> = emptyList(),
    val leadingWicketTakers: List<Pair<String, Int>> = emptyList(),
    val totalSixes: Int = 0,
    val totalFours: Int = 0,
    val highestTeamScore: Pair<String, Int>? = null,
    val lowestTeamScore: Pair<String, Int>? = null
)

enum class CommentarySpeed(val label: String, val speedMultiplier: Float) {
    SLOW("Slow (धीमी)", 0.85f),
    NORMAL("Normal (सामान्य)", 1.0f),
    FAST("Fast (तेज़)", 1.2f)
}

data class GameSettings(
    val difficulty: Difficulty = Difficulty.HARD,
    val defaultOvers: Int = 5,
    val soundEffectsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val isDarkTheme: Boolean = true,
    val highAnimationIntensity: Boolean = true,
    val commentaryEnabled: Boolean = true,
    val commentaryVoiceEnabled: Boolean = true,
    val commentarySpeed: CommentarySpeed = CommentarySpeed.SLOW,
    val commentaryVolumePercent: Int = 100
) {
    val defaultFormat: MatchFormat
        get() = MatchFormat.fromOvers(defaultOvers)
}
