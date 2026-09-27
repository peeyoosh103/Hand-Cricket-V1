package com.example.domain.engine

import com.example.data.model.*

class HandCricketEngine {

    /**
     * Process a single ball delivery in Hand Cricket
     */
    fun processBall(
        currentInnings: InningsState,
        batsmanChoice: Int,
        bowlerChoice: Int
    ): InningsState {
        require(batsmanChoice in 1..6) { "Batsman choice must be between 1 and 6" }
        require(bowlerChoice in 1..6) { "Bowler choice must be between 1 and 6" }
        require(!currentInnings.isCompleted) { "Cannot deliver ball in a completed innings" }

        val isWicket = (batsmanChoice == bowlerChoice)
        val runsScored = if (isWicket) 0 else batsmanChoice
        val newRuns = currentInnings.runs + runsScored
        val newWickets = if (isWicket) currentInnings.wickets + 1 else currentInnings.wickets
        val newBallsBowled = currentInnings.ballsBowled + 1

        val overNumber = currentInnings.ballsBowled / 6
        val ballInOver = (currentInnings.ballsBowled % 6) + 1

        val commentary = when {
            isWicket -> "OUT! Batsman and Bowler both showed $batsmanChoice! Wicket falls!"
            runsScored == 6 -> "SIX! Massive strike for 6 runs! (Batsman: $batsmanChoice vs Bowler: $bowlerChoice)"
            runsScored == 4 -> "FOUR! Boundary smashed to the fence! (Batsman: $batsmanChoice vs Bowler: $bowlerChoice)"
            runsScored == 1 -> "Single taken! (Batsman: $batsmanChoice vs Bowler: $bowlerChoice)"
            runsScored == 2 -> "Good running between the wickets, 2 runs! (Batsman: $batsmanChoice vs Bowler: $bowlerChoice)"
            runsScored == 3 -> "Great placement! 3 runs taken! (Batsman: $batsmanChoice vs Bowler: $bowlerChoice)"
            runsScored == 5 -> "Superb 5 runs! (Batsman: $batsmanChoice vs Bowler: $bowlerChoice)"
            else -> "$runsScored runs added."
        }

        val ballEvent = BallEvent(
            ballIndex = newBallsBowled,
            overNumber = overNumber,
            ballInOver = ballInOver,
            batsmanChoice = batsmanChoice,
            bowlerChoice = bowlerChoice,
            isWicket = isWicket,
            runsScored = runsScored,
            totalRunsAfter = newRuns,
            totalWicketsAfter = newWickets,
            commentary = commentary
        )

        val updatedBallEvents = currentInnings.ballEvents + ballEvent

        // Check completion conditions
        var isCompleted = false
        var completedReason: String? = null

        // 1. Chasing target reached
        if (currentInnings.target != null && newRuns >= currentInnings.target) {
            isCompleted = true
            completedReason = "Target achieved (${newRuns}/${newWickets})"
        }
        // 2. Maximum wickets reached for the selected format
        else if (newWickets >= currentInnings.maxWickets) {
            isCompleted = true
            completedReason = "All out! Maximum wicket limit reached (${newWickets}/${currentInnings.maxWickets})"
        }
        // 3. Maximum overs reached
        else if (newBallsBowled >= currentInnings.maxBalls) {
            isCompleted = true
            completedReason = "Overs completed (${currentInnings.maxOvers}.0 Overs)"
        }

        return currentInnings.copy(
            runs = newRuns,
            wickets = newWickets,
            ballsBowled = newBallsBowled,
            ballEvents = updatedBallEvents,
            isCompleted = isCompleted,
            completedReason = completedReason
        )
    }

    /**
     * Creates an initial 1st innings state
     */
    fun createFirstInnings(
        battingTeam: Team,
        bowlingTeam: Team,
        format: MatchFormat
    ): InningsState {
        return InningsState(
            inningsNumber = 1,
            battingTeam = battingTeam,
            bowlingTeam = bowlingTeam,
            maxOvers = format.overs,
            maxWickets = format.maxWickets,
            target = null
        )
    }

    /**
     * Creates the 2nd innings state with target calculated from 1st innings
     */
    fun createSecondInnings(
        firstInnings: InningsState,
        battingTeam: Team,
        bowlingTeam: Team,
        format: MatchFormat
    ): InningsState {
        val target = firstInnings.runs + 1
        return InningsState(
            inningsNumber = 2,
            battingTeam = battingTeam,
            bowlingTeam = bowlingTeam,
            maxOvers = format.overs,
            maxWickets = format.maxWickets,
            target = target
        )
    }

    /**
     * Determine match result after both innings complete
     */
    fun determineMatchResult(
        matchId: String,
        matchType: MatchType,
        format: MatchFormat,
        team1: Team,
        team2: Team,
        innings1: InningsState,
        innings2: InningsState,
        superOver1: InningsState? = null,
        superOver2: InningsState? = null
    ): MatchResult {
        // If super over was played:
        if (superOver1 != null && superOver2 != null) {
            val soTeam1Runs = superOver1.runs
            val soTeam2Runs = superOver2.runs
            val isTie = (soTeam1Runs == soTeam2Runs)

            val winner = when {
                soTeam2Runs > soTeam1Runs -> superOver2.battingTeam
                soTeam1Runs > soTeam2Runs -> superOver1.battingTeam
                else -> null
            }

            val marginText = when {
                isTie -> "Super Over Tied!"
                winner == superOver2.battingTeam -> "${winner.name} won the Super Over by ${superOver2.maxWickets - superOver2.wickets} wickets"
                winner == superOver1.battingTeam -> "${winner?.name} won the Super Over by ${soTeam1Runs - soTeam2Runs} runs"
                else -> "Super Over Tied!"
            }

            return MatchResult(
                matchId = matchId,
                matchType = matchType,
                format = format,
                team1 = team1,
                team2 = team2,
                winner = winner,
                isTie = isTie,
                marginText = marginText,
                playerOfTheMatch = selectPlayerOfTheMatch(innings1, innings2, winner ?: team1),
                innings1 = innings1,
                innings2 = innings2,
                superOverInnings1 = superOver1,
                superOverInnings2 = superOver2
            )
        }

        // Standard Match result
        val team1Runs = innings1.runs
        val team2Runs = innings2.runs
        val target = innings1.runs + 1

        val isTie = (team1Runs == team2Runs)
        val winner = when {
            team2Runs >= target -> innings2.battingTeam
            team1Runs > team2Runs -> innings1.battingTeam
            else -> null
        }

        val marginText = when {
            isTie -> "Match Tied!"
            winner == innings2.battingTeam -> {
                val wicketsRemaining = (innings2.maxWickets - innings2.wickets).coerceAtLeast(1)
                val ballsRemaining = (innings2.maxBalls - innings2.ballsBowled).coerceAtLeast(0)
                "${winner.name} won by $wicketsRemaining ${if (wicketsRemaining == 1) "wicket" else "wickets"} ($ballsRemaining balls left)"
            }
            winner == innings1.battingTeam -> {
                val runsMargin = team1Runs - team2Runs
                "${winner.name} won by $runsMargin ${if (runsMargin == 1) "run" else "runs"}"
            }
            else -> "Match Tied"
        }

        val potm = selectPlayerOfTheMatch(innings1, innings2, winner ?: team1)

        return MatchResult(
            matchId = matchId,
            matchType = matchType,
            format = format,
            team1 = team1,
            team2 = team2,
            winner = winner,
            isTie = isTie,
            marginText = marginText,
            playerOfTheMatch = potm,
            innings1 = innings1,
            innings2 = innings2
        )
    }

    private fun selectPlayerOfTheMatch(
        innings1: InningsState,
        innings2: InningsState,
        winnerTeam: Team
    ): String {
        // Find standout performer from the winning team
        val (winningInnings, _) = if (innings1.battingTeam.id == winnerTeam.id) {
            Pair(innings1, innings2)
        } else {
            Pair(innings2, innings1)
        }

        return "${winnerTeam.name} Top Performer (${winningInnings.runs} runs)"
    }
}
