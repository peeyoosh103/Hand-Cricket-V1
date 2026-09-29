package com.example.domain.commentary

import com.example.domain.commentary.dialogues.*
import java.util.Locale
import kotlin.random.Random

class CommentaryConversationManager {

    // History buffer to avoid repetition in recent 25 lines
    private val recentTemplateIds = ArrayDeque<String>(30)
    
    // Controlled Male/Female alternation tracking
    private var consecutiveMaleCount = 0
    private var consecutiveFemaleCount = 0
    private var totalMaleCount = 0
    private var totalFemaleCount = 0

    /**
     * Controlled dynamic commentator selection:
     * Guarantees that neither commentator speaks more than 2 times in a row,
     * while keeping natural variety (e.g. Male -> Female -> Female -> Male -> Male -> Female).
     */
    fun selectNextSpeaker(): Commentator {
        return when {
            consecutiveMaleCount >= 2 -> {
                consecutiveMaleCount = 0
                consecutiveFemaleCount = 1
                totalFemaleCount++
                Commentator.NEHA
            }
            consecutiveFemaleCount >= 2 -> {
                consecutiveFemaleCount = 0
                consecutiveMaleCount = 1
                totalMaleCount++
                Commentator.RAHUL
            }
            else -> {
                // 50/50 with slight bias towards the one who spoke less
                val pickMale = if (totalMaleCount < totalFemaleCount) {
                    Random.nextFloat() < 0.65f
                } else if (totalFemaleCount < totalMaleCount) {
                    Random.nextFloat() < 0.35f
                } else {
                    Random.nextBoolean()
                }

                if (pickMale) {
                    consecutiveMaleCount++
                    consecutiveFemaleCount = 0
                    totalMaleCount++
                    Commentator.RAHUL
                } else {
                    consecutiveFemaleCount++
                    consecutiveMaleCount = 0
                    totalFemaleCount++
                    Commentator.NEHA
                }
            }
        }
    }

    fun selectCommentary(eventType: CommentaryEventType, context: CommentaryContext): List<CommentaryDialogue> {
        val pool: List<DialogueTemplate> = when (eventType) {
            CommentaryEventType.DOT_BALL -> BallDialogues.DOT_BALL_TEMPLATES
            CommentaryEventType.ONE_RUN -> BallDialogues.ONE_RUN_TEMPLATES
            CommentaryEventType.TWO_RUNS -> BallDialogues.TWO_RUNS_TEMPLATES
            CommentaryEventType.THREE_RUNS -> BallDialogues.THREE_RUNS_TEMPLATES
            CommentaryEventType.BOUNDARY_FOUR -> BallDialogues.FOUR_TEMPLATES
            CommentaryEventType.FIVE_RUNS -> BallDialogues.FIVE_RUNS_TEMPLATES
            CommentaryEventType.BOUNDARY_SIX -> BallDialogues.SIX_TEMPLATES
            CommentaryEventType.CONSECUTIVE_DOTS -> BallDialogues.DOT_BALL_TEMPLATES
            CommentaryEventType.CONSECUTIVE_FOURS -> BallDialogues.FOUR_TEMPLATES
            CommentaryEventType.CONSECUTIVE_SIXES, CommentaryEventType.FOUR_THEN_SIX -> BallDialogues.SIX_TEMPLATES
            CommentaryEventType.WICKET, CommentaryEventType.CONSECUTIVE_WICKETS -> WicketDialogues.WICKET_TEMPLATES
            CommentaryEventType.OVER_END -> MatchDialogues.OVER_COMPLETE_TEMPLATES
            CommentaryEventType.TARGET_CHASE -> MatchDialogues.TARGET_CHASE_TEMPLATES
            CommentaryEventType.LAST_OVER -> MatchDialogues.LAST_OVER_TEMPLATES
            CommentaryEventType.LAST_BALL -> MatchDialogues.LAST_BALL_TEMPLATES
            CommentaryEventType.TOSS -> MatchDialogues.TOSS_TEMPLATES
            CommentaryEventType.MATCH_START -> MatchDialogues.MATCH_START_TEMPLATES
            CommentaryEventType.MATCH_WIN -> MatchDialogues.MATCH_WIN_TEMPLATES
            CommentaryEventType.MATCH_LOSS -> MatchDialogues.MATCH_LOSS_TEMPLATES
            CommentaryEventType.SUPER_OVER -> KnockoutDialogues.SUPER_OVER_TEMPLATES
            CommentaryEventType.SEMI_FINAL -> KnockoutDialogues.SEMI_FINAL_TEMPLATES
            CommentaryEventType.FINAL -> KnockoutDialogues.FINAL_TEMPLATES
            CommentaryEventType.CHAMPION -> KnockoutDialogues.CHAMPION_TEMPLATES
        }

        if (pool.isEmpty()) {
            val fallbackSpeaker = selectNextSpeaker()
            return listOf(CommentaryDialogue(fallbackSpeaker, "शानदार मुकाबला!"))
        }

        // Filter out recently played templates to ensure rich variation
        val freshCandidates = pool.filter { it.id !in recentTemplateIds }
            .ifEmpty { pool }

        val selectedTemplate = freshCandidates.randomOrNull() ?: pool.first()

        // Track history to avoid immediate repetition
        recentTemplateIds.addLast(selectedTemplate.id)
        if (recentTemplateIds.size > 25) {
            recentTemplateIds.removeFirst()
        }

        // Dynamically assign the selected speaker for natural Male/Female mixing
        val chosenSpeaker = selectNextSpeaker()

        // Variable substitution & dialogue creation
        return selectedTemplate.lines.map { dialogue ->
            val processedText = resolveVariables(dialogue.text, context)
            dialogue.copy(
                speaker = chosenSpeaker,
                text = processedText
            )
        }
    }

    private fun resolveVariables(text: String, context: CommentaryContext): String {
        var result = text
            .replace("{battingTeamName}", context.battingTeamName.ifEmpty { "बल्लेबाज़ी टीम" })
            .replace("{bowlingTeamName}", context.bowlingTeamName.ifEmpty { "गेंदबाज़ी टीम" })
            .replace("{batsmanName}", context.batsmanName.ifEmpty { "बल्लेबाज़" })
            .replace("{bowlerName}", context.bowlerName.ifEmpty { "गेंदबाज़" })
            .replace("{batsmanChoice}", context.batsmanChoice.toString())
            .replace("{bowlerChoice}", context.bowlerChoice.toString())
            .replace("{runs}", context.runsScored.toString())
            .replace("{score}", context.totalRuns.toString())
            .replace("{totalRuns}", context.totalRuns.toString())
            .replace("{wickets}", context.totalWickets.toString())
            .replace("{totalWickets}", context.totalWickets.toString())
            .replace("{overs}", context.oversBowled)
            .replace("{oversBowled}", context.oversBowled)
            .replace("{remainingBalls}", context.remainingBalls.toString())
            .replace("{runsInCurrentOver}", context.runsInCurrentOver.toString())
            .replace("{currentRunRate}", String.format(Locale.US, "%.1f", context.currentRunRate))

        context.target?.let {
            result = result.replace("{target}", it.toString())
        }
        context.requiredRuns?.let {
            result = result.replace("{requiredRuns}", it.toString())
        }
        context.requiredRunRate?.let {
            result = result.replace("{requiredRunRate}", String.format(Locale.US, "%.1f", it))
        }
        context.winnerName?.let {
            result = result.replace("{winnerName}", it)
        }
        context.loserName?.let {
            result = result.replace("{loserName}", it)
        }
        context.marginText?.let {
            result = result.replace("{marginText}", it)
        }
        context.tossWinnerName?.let {
            result = result.replace("{tossWinnerName}", it)
        }
        context.tossDecisionText?.let {
            result = result.replace("{tossDecisionText}", it)
        }

        return result
    }

    fun clearHistory() {
        recentTemplateIds.clear()
        consecutiveMaleCount = 0
        consecutiveFemaleCount = 0
        totalMaleCount = 0
        totalFemaleCount = 0
    }
}
