package com.example.domain.engine

import com.example.data.model.Difficulty
import kotlin.random.Random

class CpuAiEngine {
    private val playerHistory = mutableListOf<Int>()

    fun recordPlayerChoice(choice: Int) {
        playerHistory.add(choice)
        if (playerHistory.size > 20) {
            playerHistory.removeAt(0)
        }
    }

    fun resetHistory() {
        playerHistory.clear()
    }

    /**
     * Determine CPU choice (1..6)
     * @param isCpuBatting true if CPU is batting, false if CPU is bowling (player is batting)
     * @param difficulty Easy, Medium, or Hard
     * @param remainingRuns runs needed to win (if chasing)
     * @param remainingBalls balls left in innings
     */
    fun getCpuChoice(
        isCpuBatting: Boolean,
        difficulty: Difficulty,
        remainingRuns: Int? = null,
        remainingBalls: Int? = null
    ): Int {
        return when (difficulty) {
            Difficulty.EASY -> {
                Random.nextInt(1, 7)
            }
            Difficulty.MEDIUM -> {
                getMediumChoice(isCpuBatting, remainingRuns, remainingBalls)
            }
            Difficulty.HARD -> {
                getHardChoice(isCpuBatting, remainingRuns, remainingBalls)
            }
        }
    }

    private fun getMediumChoice(
        isCpuBatting: Boolean,
        remainingRuns: Int?,
        remainingBalls: Int?
    ): Int {
        // Medium AI has slight preference for boundaries when required run rate is high
        if (isCpuBatting && remainingRuns != null && remainingBalls != null && remainingBalls > 0) {
            val rrr = (remainingRuns.toDouble() / remainingBalls) * 6.0
            if (rrr > 10.0 && Random.nextFloat() < 0.60f) {
                return listOf(4, 5, 6).random()
            }
        }
        
        // When bowling, 30% chance to counter the player's most frequent recent move
        if (!isCpuBatting && playerHistory.isNotEmpty() && Random.nextFloat() < 0.35f) {
            val mostFrequent = playerHistory.groupBy { it }.maxByOrNull { it.value.size }?.key
            if (mostFrequent != null) {
                return mostFrequent
            }
        }

        return Random.nextInt(1, 7)
    }

    private fun getHardChoice(
        isCpuBatting: Boolean,
        remainingRuns: Int?,
        remainingBalls: Int?
    ): Int {
        // Pattern recognition on player choices
        if (!isCpuBatting && playerHistory.size >= 3) {
            val lastChoice = playerHistory.last()
            val secondLast = playerHistory[playerHistory.size - 2]

            // If player repeated the same number twice, high probability they change or repeat
            if (lastChoice == secondLast && Random.nextFloat() < 0.65f) {
                // Predict player will either repeat (35%) or pick an adjacent number (65%)
                return if (Random.nextBoolean()) lastChoice else ((lastChoice % 6) + 1)
            }

            // Frequency analysis over recent 10 balls
            val recent = playerHistory.takeLast(10)
            val freqMap = recent.groupingBy { it }.eachCount()
            val topChoice = freqMap.maxByOrNull { it.value }?.key

            if (topChoice != null && Random.nextFloat() < 0.50f) {
                return topChoice
            }
        }

        // When CPU is batting in Hard mode:
        if (isCpuBatting) {
            if (remainingRuns != null && remainingBalls != null && remainingBalls > 0) {
                val rrr = (remainingRuns.toDouble() / remainingBalls) * 6.0
                if (rrr >= 12.0) {
                    // Must hit big
                    return listOf(4, 5, 6).random()
                } else if (rrr <= 4.0 && remainingRuns <= 6) {
                    // Safe singles/twos
                    return listOf(1, 2, 3).random()
                }
            }

            // Avoid player's most recent bowling numbers if player repeats bowling numbers
            if (playerHistory.isNotEmpty()) {
                val playerLastBowl = playerHistory.last()
                val candidatePool = (1..6).filter { it != playerLastBowl }
                if (Random.nextFloat() < 0.55f && candidatePool.isNotEmpty()) {
                    return candidatePool.random()
                }
            }
        }

        return Random.nextInt(1, 7)
    }
}
