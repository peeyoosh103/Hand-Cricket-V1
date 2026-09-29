package com.example.domain.commentary

import com.example.data.model.MatchFormat
import com.example.data.model.MatchStage
import com.example.data.model.MatchType
import java.util.UUID

enum class Commentator(
    val id: String,
    val displayName: String,
    val badgeLabel: String,
    val emojiAvatar: String,
    val isMale: Boolean
) {
    RAHUL("rahul", "राहुल", "🎙️ राहुल", "👨‍💼", true),
    NEHA("neha", "नेहा", "🎙️ नेहा", "👩‍💼", false)
}

enum class CommentaryPriority(val level: Int) {
    LOW(1),
    NORMAL(2),
    MEDIUM(3),
    HIGH(4),
    HIGHEST(5)
}

enum class CommentaryEventType {
    TOSS,
    MATCH_START,
    DOT_BALL,
    ONE_RUN,
    TWO_RUNS,
    THREE_RUNS,
    BOUNDARY_FOUR,
    FIVE_RUNS,
    BOUNDARY_SIX,
    WICKET,
    CONSECUTIVE_DOTS,
    CONSECUTIVE_FOURS,
    CONSECUTIVE_SIXES,
    CONSECUTIVE_WICKETS,
    FOUR_THEN_SIX,
    OVER_END,
    TARGET_CHASE,
    LAST_OVER,
    LAST_BALL,
    MATCH_WIN,
    MATCH_LOSS,
    SUPER_OVER,
    SEMI_FINAL,
    FINAL,
    CHAMPION
}

data class CommentaryDialogue(
    val speaker: Commentator,
    val text: String,
    val priority: CommentaryPriority = CommentaryPriority.NORMAL,
    val pauseAfterMs: Long = 300L
)

data class CommentaryOutput(
    val id: String = UUID.randomUUID().toString(),
    val lines: List<CommentaryDialogue>,
    val eventType: CommentaryEventType,
    val runsScored: Int = 0,
    val isWicket: Boolean = false,
    val batsmanName: String = "",
    val bowlerName: String = "",
    val summaryText: String = lines.joinToString(" ") { it.text },
    val timestamp: Long = System.currentTimeMillis()
)

data class CommentaryContext(
    val battingTeamName: String = "",
    val bowlingTeamName: String = "",
    val batsmanName: String = "बल्लेबाज़",
    val bowlerName: String = "गेंदबाज़",
    val isUserBatting: Boolean = true,
    val batsmanChoice: Int = 0,
    val bowlerChoice: Int = 0,
    val runsScored: Int = 0,
    val isWicket: Boolean = false,
    val totalRuns: Int = 0,
    val totalWickets: Int = 0,
    val maxWickets: Int = 3,
    val oversBowled: String = "0.0",
    val overNumber: Int = 0,
    val ballInOver: Int = 0,
    val target: Int? = null,
    val requiredRuns: Int? = null,
    val remainingBalls: Int = 0,
    val requiredRunRate: Double? = null,
    val currentRunRate: Double = 0.0,
    val runsInCurrentOver: Int = 0,
    val wicketsInCurrentOver: Int = 0,
    val isLastOver: Boolean = false,
    val isLastBall: Boolean = false,
    val consecutiveDots: Int = 0,
    val consecutiveFours: Int = 0,
    val consecutiveSixes: Int = 0,
    val consecutiveWickets: Int = 0,
    val hadFourThenSix: Boolean = false,
    val matchFormat: MatchFormat = MatchFormat.FIVE_OVERS,
    val matchType: MatchType = MatchType.QUICK_MATCH,
    val stage: MatchStage = MatchStage.INNINGS_1,
    val winnerName: String? = null,
    val loserName: String? = null,
    val marginText: String? = null,
    val isTie: Boolean = false,
    val tossWinnerName: String? = null,
    val tossDecisionText: String? = null
)
