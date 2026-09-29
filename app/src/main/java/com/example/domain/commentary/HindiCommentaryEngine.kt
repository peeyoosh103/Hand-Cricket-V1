package com.example.domain.commentary

import android.content.Context
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HindiCommentaryEngine(private val context: Context) {

    private val conversationManager = CommentaryConversationManager()
    private val audioQueue = CommentaryAudioQueue(context)

    private val _currentCommentary = MutableStateFlow<CommentaryOutput?>(null)
    val currentCommentary: StateFlow<CommentaryOutput?> = _currentCommentary.asStateFlow()

    private val _commentaryHistory = MutableStateFlow<List<CommentaryOutput>>(emptyList())
    val commentaryHistory: StateFlow<List<CommentaryOutput>> = _commentaryHistory.asStateFlow()

    // Tracking consecutive events
    private var consecutiveDots = 0
    private var consecutiveFours = 0
    private var consecutiveSixes = 0
    private var consecutiveWickets = 0
    private var lastBallRuns = -1
    private var lastBallWasFour = false

    var isCommentaryEnabled: Boolean = true
    var isVoiceEnabled: Boolean = true
        set(value) {
            field = value
            audioQueue.isVoiceEnabled = value
        }

    fun applySettings(settings: GameSettings) {
        isCommentaryEnabled = settings.commentaryEnabled
        isVoiceEnabled = settings.commentaryVoiceEnabled
        audioQueue.isVoiceEnabled = settings.commentaryVoiceEnabled && settings.commentaryEnabled
        audioQueue.speechSpeedMultiplier = settings.commentarySpeed.speedMultiplier
        audioQueue.volumePercent = settings.commentaryVolumePercent
    }

    fun resetMatchSession() {
        conversationManager.clearHistory()
        audioQueue.stopCurrentAudio()
        consecutiveDots = 0
        consecutiveFours = 0
        consecutiveSixes = 0
        consecutiveWickets = 0
        lastBallRuns = -1
        lastBallWasFour = false
        _currentCommentary.value = null
        _commentaryHistory.value = emptyList()
    }

    fun onMatchStart(
        team1: Team,
        team2: Team,
        format: MatchFormat,
        matchType: MatchType
    ) {
        if (!isCommentaryEnabled) return
        val eventType = when (matchType) {
            MatchType.WORLD_CUP_SEMI_FINAL -> CommentaryEventType.SEMI_FINAL
            MatchType.WORLD_CUP_FINAL -> CommentaryEventType.FINAL
            else -> CommentaryEventType.MATCH_START
        }

        val ctx = CommentaryContext(
            battingTeamName = team1.fullName,
            bowlingTeamName = team2.fullName,
            matchFormat = format,
            matchType = matchType
        )

        dispatchCommentary(eventType, ctx, CommentaryPriority.HIGH)
    }

    fun onToss(
        winner: Team,
        choice: TossChoice,
        playerTeam: Team,
        opponentTeam: Team
    ) {
        if (!isCommentaryEnabled) return
        val decisionText = if (choice == TossChoice.BAT) "बल्लेबाज़ी" else "गेंदबाज़ी"
        val ctx = CommentaryContext(
            tossWinnerName = winner.fullName,
            tossDecisionText = decisionText
        )
        dispatchCommentary(CommentaryEventType.TOSS, ctx, CommentaryPriority.MEDIUM)
    }

    fun onBallResult(
        innings: InningsState,
        batsmanChoice: Int,
        bowlerChoice: Int,
        isWicket: Boolean,
        runsScored: Int,
        matchFormat: MatchFormat,
        matchType: MatchType,
        isUserBatting: Boolean,
        playAudio: Boolean = true
    ) {
        if (!isCommentaryEnabled) return

        // Update consecutive streaks
        if (isWicket) {
            consecutiveWickets++
            consecutiveDots = 0
            consecutiveFours = 0
            consecutiveSixes = 0
        } else {
            consecutiveWickets = 0
            when (runsScored) {
                0 -> {
                    consecutiveDots++
                    consecutiveFours = 0
                    consecutiveSixes = 0
                }
                4 -> {
                    consecutiveFours++
                    consecutiveDots = 0
                    consecutiveSixes = 0
                }
                6 -> {
                    consecutiveSixes++
                    consecutiveDots = 0
                    consecutiveFours = 0
                }
                else -> {
                    consecutiveDots = 0
                    consecutiveFours = 0
                    consecutiveSixes = 0
                }
            }
        }

        val hadFourThenSix = (lastBallWasFour && runsScored == 6)
        lastBallWasFour = (runsScored == 4)
        lastBallRuns = runsScored

        val isLastOver = (innings.ballsBowled >= (innings.maxBalls - 6)) && (innings.ballsBowled < innings.maxBalls)
        val isLastBall = (innings.remainingBalls == 0) || (innings.requiredRuns != null && innings.requiredRuns!! <= 0)

        val runsInThisOver = innings.ballEvents.takeLast(innings.ballsBowled % 6 + if (innings.ballsBowled % 6 == 0) 6 else 0)
            .sumOf { it.runsScored }

        val context = CommentaryContext(
            battingTeamName = innings.battingTeam.fullName,
            bowlingTeamName = innings.bowlingTeam.fullName,
            batsmanName = if (isUserBatting) "आप" else innings.battingTeam.shortName,
            bowlerName = if (!isUserBatting) "आप" else innings.bowlingTeam.shortName,
            isUserBatting = isUserBatting,
            batsmanChoice = batsmanChoice,
            bowlerChoice = bowlerChoice,
            runsScored = runsScored,
            isWicket = isWicket,
            totalRuns = innings.runs,
            totalWickets = innings.wickets,
            maxWickets = innings.maxWickets,
            oversBowled = innings.oversFormatted,
            overNumber = (innings.ballsBowled - 1).coerceAtLeast(0) / 6,
            ballInOver = ((innings.ballsBowled - 1).coerceAtLeast(0) % 6) + 1,
            target = innings.target,
            requiredRuns = innings.requiredRuns,
            remainingBalls = innings.remainingBalls,
            requiredRunRate = innings.requiredRunRate,
            currentRunRate = innings.runRate,
            runsInCurrentOver = runsInThisOver,
            isLastOver = isLastOver,
            isLastBall = isLastBall,
            consecutiveDots = consecutiveDots,
            consecutiveFours = consecutiveFours,
            consecutiveSixes = consecutiveSixes,
            consecutiveWickets = consecutiveWickets,
            hadFourThenSix = hadFourThenSix,
            matchFormat = matchFormat,
            matchType = matchType
        )

        // Strict Event Mapping - NEVER mismatch ball event with commentary!
        val (eventType, priority) = when {
            isWicket -> CommentaryEventType.WICKET to CommentaryPriority.HIGH
            runsScored == 6 -> CommentaryEventType.BOUNDARY_SIX to CommentaryPriority.HIGH
            runsScored == 5 -> CommentaryEventType.FIVE_RUNS to CommentaryPriority.MEDIUM
            runsScored == 4 -> CommentaryEventType.BOUNDARY_FOUR to CommentaryPriority.MEDIUM
            runsScored == 3 -> CommentaryEventType.THREE_RUNS to CommentaryPriority.NORMAL
            runsScored == 2 -> CommentaryEventType.TWO_RUNS to CommentaryPriority.NORMAL
            runsScored == 1 -> CommentaryEventType.ONE_RUN to CommentaryPriority.NORMAL
            runsScored == 0 -> CommentaryEventType.DOT_BALL to CommentaryPriority.NORMAL
            else -> CommentaryEventType.DOT_BALL to CommentaryPriority.NORMAL
        }

        dispatchCommentary(eventType, context, priority, playAudio = playAudio)
    }

    fun onMatchCompleted(
        result: MatchResult,
        userTeam: Team,
        playAudio: Boolean = true
    ) {
        if (!isCommentaryEnabled) return
        val userWon = (result.winner?.id == userTeam.id)
        val isFinal = (result.matchType == MatchType.WORLD_CUP_FINAL)
        val isSemiFinal = (result.matchType == MatchType.WORLD_CUP_SEMI_FINAL)

        val context = CommentaryContext(
            winnerName = result.winner?.fullName ?: "विजेता टीम",
            loserName = if (result.winner?.id == result.team1.id) result.team2.fullName else result.team1.fullName,
            marginText = result.marginText,
            matchType = result.matchType,
            matchFormat = result.format,
            isTie = result.isTie
        )

        val (eventType, priority) = when {
            isFinal && userWon -> CommentaryEventType.CHAMPION to CommentaryPriority.HIGHEST
            isFinal -> CommentaryEventType.FINAL to CommentaryPriority.HIGHEST
            isSemiFinal && userWon -> CommentaryEventType.SEMI_FINAL to CommentaryPriority.HIGHEST
            userWon -> CommentaryEventType.MATCH_WIN to CommentaryPriority.HIGH
            result.isTie -> CommentaryEventType.SUPER_OVER to CommentaryPriority.HIGHEST
            else -> CommentaryEventType.MATCH_LOSS to CommentaryPriority.HIGH
        }

        dispatchCommentary(eventType, context, priority, playAudio = playAudio)
    }

    fun onSuperOverTriggered(playAudio: Boolean = true) {
        if (!isCommentaryEnabled) return
        val ctx = CommentaryContext()
        dispatchCommentary(CommentaryEventType.SUPER_OVER, ctx, CommentaryPriority.HIGHEST, playAudio = playAudio)
    }

    private fun dispatchCommentary(
        eventType: CommentaryEventType,
        context: CommentaryContext,
        priority: CommentaryPriority,
        playAudio: Boolean = true
    ) {
        // Strict Validation: Ensure actual finalized runs equals commentary category
        val validatedEventType = if (context.isWicket) {
            CommentaryEventType.WICKET
        } else if (context.runsScored in 0..6 && eventType in listOf(
                CommentaryEventType.DOT_BALL,
                CommentaryEventType.ONE_RUN,
                CommentaryEventType.TWO_RUNS,
                CommentaryEventType.THREE_RUNS,
                CommentaryEventType.BOUNDARY_FOUR,
                CommentaryEventType.FIVE_RUNS,
                CommentaryEventType.BOUNDARY_SIX
            )) {
            when (context.runsScored) {
                0 -> CommentaryEventType.DOT_BALL
                1 -> CommentaryEventType.ONE_RUN
                2 -> CommentaryEventType.TWO_RUNS
                3 -> CommentaryEventType.THREE_RUNS
                4 -> CommentaryEventType.BOUNDARY_FOUR
                5 -> CommentaryEventType.FIVE_RUNS
                6 -> CommentaryEventType.BOUNDARY_SIX
                else -> eventType
            }
        } else {
            eventType
        }

        val dialogueList = conversationManager.selectCommentary(validatedEventType, context)
        val output = CommentaryOutput(
            lines = dialogueList,
            eventType = validatedEventType,
            runsScored = context.runsScored,
            isWicket = context.isWicket,
            batsmanName = context.batsmanName,
            bowlerName = context.bowlerName
        )

        _currentCommentary.value = output
        _commentaryHistory.value = (listOf(output) + _commentaryHistory.value).take(50)

        if (playAudio && isVoiceEnabled) {
            audioQueue.enqueueDialogue(dialogueList, priority)
        }
    }

    fun stopAudio() {
        audioQueue.stopCurrentAudio()
    }

    fun shutdown() {
        audioQueue.shutdown()
    }
}
