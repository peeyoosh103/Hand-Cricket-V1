package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PreferencesManager
import com.example.data.model.*
import com.example.data.repository.GameRepository
import com.example.domain.audio.SoundManager
import com.example.domain.audio.SoundType
import com.example.domain.engine.CpuAiEngine
import com.example.domain.engine.HandCricketEngine
import com.example.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ActiveMatchState(
    val matchId: String,
    val matchType: MatchType,
    val fixtureId: String? = null,
    val playerTeam: Team,
    val opponentTeam: Team,
    val format: MatchFormat,
    val stage: MatchStage = MatchStage.TOSS,
    val tossWinner: Team? = null,
    val tossChoice: TossChoice? = null,
    val isPlayerTossWinner: Boolean = false,
    val innings1: InningsState? = null,
    val innings2: InningsState? = null,
    val superOverInnings1: InningsState? = null,
    val superOverInnings2: InningsState? = null,
    val superOverCount: Int = 0,
    val lastPlayerChoice: Int? = null,
    val lastCpuChoice: Int? = null,
    val lastBallWicket: Boolean = false,
    val lastRunsScored: Int = 0,
    val isBallProcessing: Boolean = false,
    val completedResult: MatchResult? = null
) {
    val currentInnings: InningsState?
        get() = when (stage) {
            MatchStage.INNINGS_1 -> innings1
            MatchStage.INNINGS_2 -> innings2
            MatchStage.SUPER_OVER_INNINGS_1 -> superOverInnings1
            MatchStage.SUPER_OVER_INNINGS_2 -> superOverInnings2
            else -> null
        }

    val isUserBatting: Boolean
        get() {
            val cur = currentInnings ?: return false
            return cur.battingTeam.id == playerTeam.id
        }
}

data class PracticeState(
    val isBattingMode: Boolean = true,
    val format: MatchFormat = MatchFormat.FIVE_OVERS,
    val runs: Int = 0,
    val wickets: Int = 0,
    val balls: Int = 0,
    val highestScoreInSession: Int = 0,
    val currentStreak: Int = 0,
    val lastPlayerChoice: Int? = null,
    val lastCpuChoice: Int? = null,
    val lastBallWicket: Boolean = false,
    val lastRunsScored: Int = 0,
    val recentBalls: List<BallEvent> = emptyList()
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val gameRepository = GameRepository(preferencesManager)
    private val handCricketEngine = HandCricketEngine()
    private val cpuAiEngine = CpuAiEngine()
    private val soundManager = SoundManager(application)

    // Navigation Stack
    private val _navStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home)

    // Observables from Repository
    val tournamentState: StateFlow<TournamentState?> = gameRepository.tournamentState
    val careerStats: StateFlow<PlayerCareerStats> = gameRepository.careerStats
    val matchHistory: StateFlow<List<MatchResult>> = gameRepository.matchHistory
    val settings: StateFlow<GameSettings> = gameRepository.settings

    // Active Gameplay State
    private val _activeMatch = MutableStateFlow<ActiveMatchState?>(null)
    val activeMatch: StateFlow<ActiveMatchState?> = _activeMatch.asStateFlow()

    // Practice Mode State
    private val _practiceState = MutableStateFlow(PracticeState())
    val practiceState: StateFlow<PracticeState> = _practiceState.asStateFlow()

    init {
        // Keep currentScreen in sync with navStack top
        viewModelScope.launch {
            _navStack.collect { stack ->
                (currentScreen as MutableStateFlow).value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        playSound(SoundType.BUTTON_CLICK)
        _navStack.value = _navStack.value + screen
    }

    fun navigateBack(): Boolean {
        if (_navStack.value.size > 1) {
            playSound(SoundType.BUTTON_CLICK)
            _navStack.value = _navStack.value.dropLast(1)
            return true
        }
        return false
    }

    fun navigateToHome() {
        playSound(SoundType.BUTTON_CLICK)
        _navStack.value = listOf(Screen.Home)
    }

    // Sound & Haptic Helper
    fun playSound(type: SoundType) {
        soundManager.playSound(type, settings.value.soundEffectsEnabled)
        soundManager.playHaptic(type, settings.value.vibrationEnabled)
    }

    // Match Lifecycle
    fun startMatch(
        matchType: MatchType,
        fixtureId: String? = null,
        playerTeam: Team,
        opponentTeam: Team,
        format: MatchFormat
    ) {
        cpuAiEngine.resetHistory()
        val matchId = fixtureId ?: "MATCH_${System.currentTimeMillis()}"

        _activeMatch.value = ActiveMatchState(
            matchId = matchId,
            matchType = matchType,
            fixtureId = fixtureId,
            playerTeam = playerTeam,
            opponentTeam = opponentTeam,
            format = format,
            stage = MatchStage.TOSS
        )

        navigateTo(
            Screen.Gameplay(
                matchType = matchType,
                fixtureId = fixtureId,
                playerTeam = playerTeam,
                opponentTeam = opponentTeam,
                format = format
            )
        )
    }

    fun executeToss(playerCallsHeads: Boolean) {
        val current = _activeMatch.value ?: return
        playSound(SoundType.TOSS_COIN)

        val coinIsHeads = Random.nextBoolean()
        val playerWonToss = (playerCallsHeads == coinIsHeads)
        val tossWinner = if (playerWonToss) current.playerTeam else current.opponentTeam

        if (playerWonToss) {
            // Player gets choice in UI
            _activeMatch.value = current.copy(
                tossWinner = tossWinner,
                isPlayerTossWinner = true
            )
        } else {
            // CPU chooses automatically
            val cpuChoice = if (Random.nextBoolean()) TossChoice.BAT else TossChoice.BOWL
            applyTossDecision(tossWinner, cpuChoice, isPlayerWinner = false)
        }
    }

    fun applyTossDecision(winner: Team, choice: TossChoice, isPlayerWinner: Boolean) {
        val current = _activeMatch.value ?: return
        val playerTeam = current.playerTeam
        val opponentTeam = current.opponentTeam
        val format = current.format

        val battingFirst = if (winner.id == playerTeam.id) {
            if (choice == TossChoice.BAT) playerTeam else opponentTeam
        } else {
            if (choice == TossChoice.BAT) opponentTeam else playerTeam
        }
        val bowlingFirst = if (battingFirst.id == playerTeam.id) opponentTeam else playerTeam

        val innings1 = handCricketEngine.createFirstInnings(battingFirst, bowlingFirst, format)

        _activeMatch.value = current.copy(
            tossWinner = winner,
            tossChoice = choice,
            isPlayerTossWinner = isPlayerWinner,
            stage = MatchStage.INNINGS_1,
            innings1 = innings1
        )
    }

    fun onPlayerSelectNumber(playerChoice: Int) {
        val current = _activeMatch.value ?: return
        if (current.isBallProcessing) return
        val currentInnings = current.currentInnings ?: return
        if (currentInnings.isCompleted) return

        val isUserBatting = current.isUserBatting
        val diff = settings.value.difficulty

        // CPU AI selects its number
        val cpuChoice = cpuAiEngine.getCpuChoice(
            isCpuBatting = !isUserBatting,
            difficulty = diff,
            remainingRuns = currentInnings.requiredRuns,
            remainingBalls = currentInnings.remainingBalls
        )

        // Record player pattern in AI
        cpuAiEngine.recordPlayerChoice(playerChoice)

        val batsmanChoice = if (isUserBatting) playerChoice else cpuChoice
        val bowlerChoice = if (isUserBatting) cpuChoice else playerChoice

        val updatedInnings = handCricketEngine.processBall(currentInnings, batsmanChoice, bowlerChoice)
        val isWicket = (batsmanChoice == bowlerChoice)
        val runs = if (isWicket) 0 else batsmanChoice

        // Sound feedback
        when {
            isWicket -> playSound(SoundType.WICKET_OUT)
            runs == 6 -> playSound(SoundType.SIX_MASSIVE)
            runs == 4 -> playSound(SoundType.FOUR_BOUNDARY)
            else -> playSound(SoundType.BAT_HIT)
        }

        // Apply updated innings to state
        val updatedMatch = when (current.stage) {
            MatchStage.INNINGS_1 -> current.copy(
                innings1 = updatedInnings,
                lastPlayerChoice = playerChoice,
                lastCpuChoice = cpuChoice,
                lastBallWicket = isWicket,
                lastRunsScored = runs
            )
            MatchStage.INNINGS_2 -> current.copy(
                innings2 = updatedInnings,
                lastPlayerChoice = playerChoice,
                lastCpuChoice = cpuChoice,
                lastBallWicket = isWicket,
                lastRunsScored = runs
            )
            MatchStage.SUPER_OVER_INNINGS_1 -> current.copy(
                superOverInnings1 = updatedInnings,
                lastPlayerChoice = playerChoice,
                lastCpuChoice = cpuChoice,
                lastBallWicket = isWicket,
                lastRunsScored = runs
            )
            MatchStage.SUPER_OVER_INNINGS_2 -> current.copy(
                superOverInnings2 = updatedInnings,
                lastPlayerChoice = playerChoice,
                lastCpuChoice = cpuChoice,
                lastBallWicket = isWicket,
                lastRunsScored = runs
            )
            else -> current
        }

        _activeMatch.value = updatedMatch

        // Check if innings just completed
        if (updatedInnings.isCompleted) {
            handleInningsCompletion(updatedMatch)
        }
    }

    private fun handleInningsCompletion(match: ActiveMatchState) {
        viewModelScope.launch {
            delay(1200) // Allow animation and score acknowledgment
            when (match.stage) {
                MatchStage.INNINGS_1 -> {
                    val innings1 = match.innings1 ?: return@launch
                    val innings2Batting = innings1.bowlingTeam
                    val innings2Bowling = innings1.battingTeam
                    val innings2 = handCricketEngine.createSecondInnings(
                        innings1,
                        innings2Batting,
                        innings2Bowling,
                        match.format
                    )
                    _activeMatch.value = match.copy(
                        stage = MatchStage.INNINGS_BREAK,
                        innings2 = innings2
                    )
                }
                MatchStage.INNINGS_2 -> {
                    val innings1 = match.innings1 ?: return@launch
                    val innings2 = match.innings2 ?: return@launch

                    val result = handCricketEngine.determineMatchResult(
                        matchId = match.matchId,
                        matchType = match.matchType,
                        format = match.format,
                        team1 = match.playerTeam,
                        team2 = match.opponentTeam,
                        innings1 = innings1,
                        innings2 = innings2
                    )

                    // Check if tie and needs Super Over (knockout match)
                    if (result.isTie && (match.matchType == MatchType.WORLD_CUP_SEMI_FINAL || match.matchType == MatchType.WORLD_CUP_FINAL)) {
                        startSuperOver(match)
                    } else {
                        finishMatch(match, result)
                    }
                }
                MatchStage.SUPER_OVER_INNINGS_1 -> {
                    val so1 = match.superOverInnings1 ?: return@launch
                    val so2 = handCricketEngine.createSecondInnings(
                        so1,
                        so1.bowlingTeam,
                        so1.battingTeam,
                        MatchFormat.ONE_OVER
                    )
                    _activeMatch.value = match.copy(
                        stage = MatchStage.SUPER_OVER_BREAK,
                        superOverInnings2 = so2
                    )
                }
                MatchStage.SUPER_OVER_INNINGS_2 -> {
                    val innings1 = match.innings1 ?: return@launch
                    val innings2 = match.innings2 ?: return@launch
                    val so1 = match.superOverInnings1 ?: return@launch
                    val so2 = match.superOverInnings2 ?: return@launch

                    val result = handCricketEngine.determineMatchResult(
                        matchId = match.matchId,
                        matchType = match.matchType,
                        format = match.format,
                        team1 = match.playerTeam,
                        team2 = match.opponentTeam,
                        innings1 = innings1,
                        innings2 = innings2,
                        superOver1 = so1,
                        superOver2 = so2
                    )

                    if (result.isTie) {
                        // Another Super Over until winner is found
                        startSuperOver(match)
                    } else {
                        finishMatch(match, result)
                    }
                }
                else -> {}
            }
        }
    }

    fun startSecondInnings() {
        val current = _activeMatch.value ?: return
        if (current.stage == MatchStage.INNINGS_BREAK) {
            playSound(SoundType.BUTTON_CLICK)
            _activeMatch.value = current.copy(stage = MatchStage.INNINGS_2)
        }
    }

    fun startSuperOverSecondInnings() {
        val current = _activeMatch.value ?: return
        if (current.stage == MatchStage.SUPER_OVER_BREAK) {
            playSound(SoundType.BUTTON_CLICK)
            _activeMatch.value = current.copy(stage = MatchStage.SUPER_OVER_INNINGS_2)
        }
    }

    private fun startSuperOver(match: ActiveMatchState) {
        val battingFirst = match.innings1?.battingTeam ?: match.playerTeam
        val bowlingFirst = match.innings1?.bowlingTeam ?: match.opponentTeam

        val so1 = handCricketEngine.createFirstInnings(battingFirst, bowlingFirst, MatchFormat.ONE_OVER)
        _activeMatch.value = match.copy(
            stage = MatchStage.SUPER_OVER_INNINGS_1,
            superOverInnings1 = so1,
            superOverCount = match.superOverCount + 1
        )
    }

    private fun finishMatch(match: ActiveMatchState, result: MatchResult) {
        playSound(SoundType.MATCH_WIN)
        gameRepository.saveMatchResult(result)

        if (match.matchType.name.startsWith("WORLD_CUP")) {
            gameRepository.updateTournamentAfterMatch(result)
        }

        _activeMatch.value = match.copy(
            stage = MatchStage.COMPLETED,
            completedResult = result
        )

        val fromTournament = match.matchType.name.startsWith("WORLD_CUP")
        navigateTo(Screen.ResultScreen(result, fromTournament = fromTournament))
    }

    // Practice Mode Actions
    fun togglePracticeMode() {
        playSound(SoundType.BUTTON_CLICK)
        _practiceState.value = _practiceState.value.copy(
            isBattingMode = !_practiceState.value.isBattingMode
        )
    }

    fun setPracticeFormat(format: MatchFormat) {
        _practiceState.value = _practiceState.value.copy(
            format = format,
            runs = 0,
            wickets = 0,
            balls = 0,
            recentBalls = emptyList()
        )
    }

    fun onPracticeSelectNumber(playerChoice: Int) {
        val state = _practiceState.value
        val cpuChoice = Random.nextInt(1, 7)
        val isBatting = state.isBattingMode

        val batsmanChoice = if (isBatting) playerChoice else cpuChoice
        val bowlerChoice = if (isBatting) cpuChoice else playerChoice

        val isWicket = (batsmanChoice == bowlerChoice)
        val runs = if (isWicket) 0 else batsmanChoice

        when {
            isWicket -> playSound(SoundType.WICKET_OUT)
            runs == 6 -> playSound(SoundType.SIX_MASSIVE)
            runs == 4 -> playSound(SoundType.FOUR_BOUNDARY)
            else -> playSound(SoundType.BAT_HIT)
        }

        val newRuns = state.runs + runs
        val newWickets = if (isWicket) state.wickets + 1 else state.wickets
        val newBalls = state.balls + 1
        val newStreak = if (isWicket) 0 else state.currentStreak + 1
        val highest = maxOf(state.highestScoreInSession, newRuns)

        val ballEvent = BallEvent(
            ballIndex = newBalls,
            overNumber = (newBalls - 1) / 6,
            ballInOver = ((newBalls - 1) % 6) + 1,
            batsmanChoice = batsmanChoice,
            bowlerChoice = bowlerChoice,
            isWicket = isWicket,
            runsScored = runs,
            totalRunsAfter = newRuns,
            totalWicketsAfter = newWickets,
            commentary = if (isWicket) "OUT! Both showed $batsmanChoice" else "$runs runs scored!"
        )

        _practiceState.value = state.copy(
            runs = newRuns,
            wickets = newWickets,
            balls = newBalls,
            currentStreak = newStreak,
            highestScoreInSession = highest,
            lastPlayerChoice = playerChoice,
            lastCpuChoice = cpuChoice,
            lastBallWicket = isWicket,
            lastRunsScored = runs,
            recentBalls = (listOf(ballEvent) + state.recentBalls).take(12)
        )
    }

    fun checkAndAutoProgressTournament() {
        gameRepository.autoProgressTournament()
    }

    fun resetPracticeScore() {
        playSound(SoundType.BUTTON_CLICK)
        _practiceState.value = _practiceState.value.copy(
            runs = 0,
            wickets = 0,
            balls = 0,
            currentStreak = 0,
            recentBalls = emptyList()
        )
    }

    // Tournament Actions
    fun startNewTournament(userTeamId: String, format: MatchFormat) {
        playSound(SoundType.BUTTON_CLICK)
        gameRepository.createNewTournament(userTeamId, format)
        navigateTo(Screen.WorldCupHub)
    }

    fun simulateFixture(fixtureId: String) {
        playSound(SoundType.BUTTON_CLICK)
        gameRepository.simulateFixture(fixtureId)
    }

    fun simulateAllUpcomingLeagueMatches() {
        playSound(SoundType.BUTTON_CLICK)
        gameRepository.simulateAllUpcomingCpuLeagueMatches()
    }

    fun resetTournament() {
        playSound(SoundType.BUTTON_CLICK)
        gameRepository.resetTournament()
        navigateToHome()
    }

    // Reset Operations
    fun resetMatchHistory() {
        playSound(SoundType.BUTTON_CLICK)
        gameRepository.resetMatchHistory()
    }

    fun resetCareerStats() {
        playSound(SoundType.BUTTON_CLICK)
        gameRepository.resetCareerStats()
    }

    fun updateSettings(settings: GameSettings) {
        playSound(SoundType.BUTTON_CLICK)
        gameRepository.updateSettings(settings)
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
