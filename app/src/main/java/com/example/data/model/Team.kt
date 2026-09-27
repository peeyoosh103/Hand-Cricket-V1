package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class Difficulty(val displayName: String, val description: String) {
    EASY("Easy", "CPU makes random predictions"),
    MEDIUM("Medium", "CPU adapts slightly to your playing style"),
    HARD("Hard", "Smart AI with pattern recognition and counter-tactics")
}

enum class MatchFormat(
    val overs: Int,
    val maxWickets: Int,
    val displayName: String,
    val wicketRuleDescription: String
) {
    ONE_OVER(1, 1, "1 Over", "Max 1 Wicket"),
    TWO_OVERS(2, 1, "2 Overs", "Max 1 Wicket"),
    FIVE_OVERS(5, 3, "5 Overs", "Max 3 Wickets"),
    TEN_OVERS(10, 6, "10 Overs", "Max 6 Wickets");

    val totalBalls: Int
        get() = overs * 6

    companion object {
        fun fromOvers(overs: Int): MatchFormat {
            return entries.find { it.overs == overs } ?: FIVE_OVERS
        }
    }
}

data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val flagEmoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val worldRanking: Int
) {
    val primaryColor: Color get() = Color(primaryColorHex)
    val secondaryColor: Color get() = Color(secondaryColorHex)

    companion object {
        val ALL_TEAMS = listOf(
            Team(
                id = "IND",
                name = "India",
                shortName = "IND",
                flagEmoji = "🇮🇳",
                primaryColorHex = 0xFF0D47A1, // Deep Blue
                secondaryColorHex = 0xFFFF9800, // Saffron
                worldRanking = 1
            ),
            Team(
                id = "AUS",
                name = "Australia",
                shortName = "AUS",
                flagEmoji = "🇦🇺",
                primaryColorHex = 0xFFFFD700, // Gold
                secondaryColorHex = 0xFF005A36, // Bottle Green
                worldRanking = 2
            ),
            Team(
                id = "ENG",
                name = "England",
                shortName = "ENG",
                flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
                primaryColorHex = 0xFF1E3A8A, // Navy
                secondaryColorHex = 0xFFDC2626, // Red
                worldRanking = 3
            ),
            Team(
                id = "SA",
                name = "South Africa",
                shortName = "SA",
                flagEmoji = "🇿🇦",
                primaryColorHex = 0xFF065F46, // Dark Emerald
                secondaryColorHex = 0xFFFBBF24, // Gold
                worldRanking = 4
            ),
            Team(
                id = "NZ",
                name = "New Zealand",
                shortName = "NZ",
                flagEmoji = "🇳🇿",
                primaryColorHex = 0xFF18181B, // Black Caps
                secondaryColorHex = 0xFF0284C7, // Sky Blue
                worldRanking = 5
            ),
            Team(
                id = "PAK",
                name = "Pakistan",
                shortName = "PAK",
                flagEmoji = "🇵🇰",
                primaryColorHex = 0xFF047857, // Star Green
                secondaryColorHex = 0xFFF3F4F6, // White
                worldRanking = 6
            ),
            Team(
                id = "SL",
                name = "Sri Lanka",
                shortName = "SL",
                flagEmoji = "🇱🇰",
                primaryColorHex = 0xFF1E40AF, // Royal Blue
                secondaryColorHex = 0xFFF59E0B, // Lion Gold
                worldRanking = 7
            ),
            Team(
                id = "BAN",
                name = "Bangladesh",
                shortName = "BAN",
                flagEmoji = "🇧🇩",
                primaryColorHex = 0xFF064E3B, // Forest Green
                secondaryColorHex = 0xFFEF4444, // Red
                worldRanking = 8
            ),
            Team(
                id = "AFG",
                name = "Afghanistan",
                shortName = "AFG",
                flagEmoji = "🇦🇫",
                primaryColorHex = 0xFF2563EB, // Blue
                secondaryColorHex = 0xFFDC2626, // Red
                worldRanking = 9
            ),
            Team(
                id = "WI",
                name = "West Indies",
                shortName = "WI",
                flagEmoji = "🌴",
                primaryColorHex = 0xFF831843, // Maroon
                secondaryColorHex = 0xFFFACC15, // Sun Yellow
                worldRanking = 10
            )
        )

        fun getTeamById(id: String): Team {
            return ALL_TEAMS.find { it.id.equals(id, ignoreCase = true) } ?: ALL_TEAMS[0]
        }
    }
}
